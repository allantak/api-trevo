package br.com.jacto.trevo.controller.order;

import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.order.OrderItemController;
import br.com.jacto.trevo.controller.order.dto.OrderItemCreateDto;
import br.com.jacto.trevo.controller.order.dto.OrderItemDto;
import br.com.jacto.trevo.controller.order.form.OrderItemForm;
import br.com.jacto.trevo.controller.order.form.OrderItemUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.AccountRepository;
import br.com.jacto.trevo.service.order.OrderItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderItemController.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    OrderItemService orderItemService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private AccountRepository managerRepository;

    @Autowired
    private JacksonTester<List<OrderItemDto>> listOrderDtoJson;
    @Autowired
    private JacksonTester<OrderItemDto> orderItemDtoJson;
    @Autowired
    private JacksonTester<OrderItemForm> orderItemFormJson;
    @Autowired
    private JacksonTester<OrderItemCreateDto> orderItemCreateDtoJson;
    @Autowired
    private JacksonTester<OrderItemUpdateForm> OrderItemUpdateForm;


    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);
    public Product product = new Product("Trator Jacto", Product.Status.DISPONIVEL, Product.Category.ELETRICO, "Trator jacto para agricultura", 120.0, 2.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

    public OrderItem order = new OrderItem(3, account, product);

    @Test
    @DisplayName("Listando pedidos existente em formato correto")
    public void listOrder() throws Exception {

        List<OrderItemDto> listOrder = new ArrayList<OrderItemDto>();
        listOrder.add(new OrderItemDto(order));

        when(orderItemService.getAll()).thenReturn(listOrder);

        var response = mockMvc.perform(
                        get("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = listOrderDtoJson.write(listOrder).getJson();


        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("Nao permitido fazer listagem de pedidos")
    public void listOrderCase2() throws Exception {

        List<OrderItemDto> listOrder = new ArrayList<OrderItemDto>();
        listOrder.add(new OrderItemDto(order));

        when(orderItemService.getAll()).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        get("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

    }


    @Test
    @DisplayName("Pesquisar pelo id do pedido")
    public void orderId() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderItemDto orderDto = new OrderItemDto(order);

        when(orderItemService.getId(orderId)).thenReturn(Optional.of(orderDto));

        var response = mockMvc.perform(
                        get("/orders/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = orderItemDtoJson.write(orderDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }


    @Test
    @DisplayName("Caso a pesquisa de um pedido nao existente retorne not found")
    public void orderIdCase2() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderItemDto orderDto = new OrderItemDto(order);

        when(orderItemService.getId(orderId)).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        get("/orders/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Caso a pesquisa seja feito com id em formato invalido retorne bad request")
    public void orderIdCase3() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderItemDto orderDto = new OrderItemDto(order);

        when(orderItemService.getId(orderId)).thenReturn(Optional.of(orderDto));

        var response = mockMvc.perform(
                        get("/orders/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    @DisplayName("Pesquisar pelo id do pedido")
    public void creatOrder() throws Exception {
        UUID accountId = UUID.randomUUID();

        OrderItemForm form = new OrderItemForm();
        form.setAccountId(accountId);
        form.setProductName(product.getProductName());
        form.setQuantity(3);
        order.setOrderItemId(UUID.randomUUID());

        OrderItemCreateDto orderItemCreateDto = new OrderItemCreateDto(order);

        when(orderItemService.create(any())).thenReturn(Optional.of(orderItemCreateDto));

        var response = mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(orderItemFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertTrue(response.containsHeader("Location"));
        var jsonExpect = orderItemCreateDtoJson.write(orderItemCreateDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Caso o id do cliente nao for cadastrado deve retornar not found")
    public void creatOrderCase2() throws Exception {
        UUID accountId = UUID.randomUUID();

        OrderItemForm form = new OrderItemForm();
        form.setAccountId(accountId);
        form.setProductName(product.getProductName());
        form.setQuantity(3);
        order.setOrderItemId(UUID.randomUUID());

        when(orderItemService.create(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(orderItemFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    @DisplayName("Caso campos obrigatorio nao for preenchido deve retornar bad request")
    public void creatOrderCase3() throws Exception {

        OrderItemForm form = new OrderItemForm();

        var response = mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(orderItemFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("fazer atualizacao do pedido")
    public void updateOrder() throws Exception {
        UUID clientId = UUID.randomUUID();

        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(UUID.randomUUID());
        form.setAccountId(clientId);
        form.setProductName(product.getProductName());
        form.setQuantity(3);

        OrderItemCreateDto orderItemCreateDto = new OrderItemCreateDto(order);

        when(orderItemService.update(any())).thenReturn(Optional.of(orderItemCreateDto));

        var response = mockMvc.perform(
                        put("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(OrderItemUpdateForm.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = orderItemCreateDtoJson.write(orderItemCreateDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Ao fazer atualizacao com nome do produto e id nao cadastrado retorne not found")
    public void updateOrderCase2() throws Exception {
        UUID clientId = UUID.randomUUID();

        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(UUID.randomUUID());
        form.setAccountId(clientId);
        form.setProductName(product.getProductName());
        form.setQuantity(3);

        when(orderItemService.update(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        put("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(OrderItemUpdateForm.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("caso campo obrigatorio nao for inserido retorne bad request")
    public void updateOrderCase3() throws Exception {

        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setProductName(product.getProductName());
        form.setQuantity(3);


        var response = mockMvc.perform(
                        put("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(OrderItemUpdateForm.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Ao fazer atualizacao os campos null nao obrigatorio deve retornar os campos ja existente")
    public void updateOrderCase4() throws Exception {
        UUID clientId = UUID.randomUUID();

        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(UUID.randomUUID());
        form.setAccountId(clientId);

        OrderItemCreateDto orderItemCreateDto = new OrderItemCreateDto(order);

        when(orderItemService.update(any())).thenReturn(Optional.of(orderItemCreateDto));

        var response = mockMvc.perform(
                        put("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(OrderItemUpdateForm.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = orderItemCreateDtoJson.write(orderItemCreateDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Ao fazer atualizacao e o nome do produto esta vazio deve retornar o campo ja existente")
    public void updateOrderCase5() throws Exception {
        UUID clientId = UUID.randomUUID();

        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(UUID.randomUUID());
        form.setAccountId(clientId);
        form.setProductName("");
        form.setQuantity(3);


        OrderItemCreateDto orderItemCreateDto = new OrderItemCreateDto(order);

        when(orderItemService.update(any())).thenReturn(Optional.of(orderItemCreateDto));

        var response = mockMvc.perform(
                        put("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(OrderItemUpdateForm.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = orderItemCreateDtoJson.write(orderItemCreateDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Ao fazer atualizacao com nome ja existente de produto deve retornar Conflict")
    public void updateOrderCase6() throws Exception {
        UUID clientId = UUID.randomUUID();

        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(UUID.randomUUID());
        form.setAccountId(clientId);
        form.setProductName(product.getProductName());
        form.setQuantity(3);


        when(orderItemService.update(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        var response = mockMvc.perform(
                        put("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(OrderItemUpdateForm.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }


    @Test
    @DisplayName("Deletar pedido")
    public void deleteOrder() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(orderItemService.delete(orderId)).thenReturn(true);

        var response = mockMvc.perform(
                        delete("/orders/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    @DisplayName("Pedido nao cadastrado para deletar retorne not found")
    public void deleteOrderCase2() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(orderItemService.delete(orderId)).thenReturn(false);

        var response = mockMvc.perform(
                        delete("/orders/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("id do pedido em formato incorreto ao deletar")
    public void deleteOrderCase3() throws Exception {
        var response = mockMvc.perform(
                        delete("/orders/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }
}
