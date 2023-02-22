package br.com.jacto.trevo.service.order;

import br.com.jacto.trevo.controller.order.dto.OrderItemCreateDto;
import br.com.jacto.trevo.controller.order.dto.OrderItemDto;
import br.com.jacto.trevo.controller.order.form.OrderItemForm;
import br.com.jacto.trevo.controller.order.form.OrderItemUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.ClientRepository;
import br.com.jacto.trevo.repository.OrderItemRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class OrderItemServiceTest {
    @Autowired
    OrderItemService orderItemService;

    @MockBean
    OrderItemRepository orderItemRepository;

    @MockBean
    ClientRepository clientRepository;

    @MockBean
    ProductRepository productRepository;

    public Client client = new Client("testando", "testando@gmail.com", "(14) 99832-20566");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14));
    public OrderItem order = new OrderItem(3, client, product);

    public OrderItemUpdateForm formUpdate() {
        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(order.getOrderItemId());
        form.setClientId(client.getId());
        form.setProductName(product.getProductName());
        form.setQuantity(order.getQuantity());

        return form;
    }

    @Test
    @DisplayName("Listagem de todos pedidos cadastrados")
    public void orderGetAll() {
        List<OrderItem> orderItemList = Collections.singletonList(order);

        when(orderItemRepository.findAll()).thenReturn(orderItemList);

        List<OrderItemDto> result = orderItemService.getAll();
        assertEquals(orderItemList.size(), result.size());
        assertEquals(orderItemList.get(0).getOrderItemId(), result.get(0).getOrderItemId());
    }

    @Test
    @DisplayName("Pedido pelo id")
    public void orderGetById() {

        OrderItemDto orderItemDto = new OrderItemDto(order);
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(order));

        Optional<OrderItemDto> result = orderItemService.getId(UUID.randomUUID());
        assertEquals(orderItemDto.getOrderItemId(), result.get().getOrderItemId());
        assertEquals(orderItemDto.getEmail(), result.get().getEmail());
        assertEquals(orderItemDto.getProductName(), result.get().getProductName());
        assertEquals(orderItemDto.getQuantity(), result.get().getQuantity());
    }

    @Test
    @DisplayName("Caso nao encontre id do pedido")
    public void orderGetByIdCase2() {
        when(orderItemRepository.findById(any())).thenReturn(Optional.empty());

        Optional<OrderItemDto> result = orderItemService.getId(UUID.randomUUID());
        assertEquals(Optional.empty(), result);
    }


    @Test
    @DisplayName("Atualizacao de pedido")
    public void updateOrderItem() {
        OrderItemUpdateForm form = formUpdate();

        when(productRepository.findByProductName(any())).thenReturn(Optional.ofNullable(product));
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(order));
        when(orderItemRepository.save(any())).thenReturn(order);

        Optional<OrderItemDto> update = orderItemService.update(form);

        assertNotNull(update);
        assertEquals(form.getProductName(), update.get().getProductName());
        assertEquals(form.getOrderItemId(), update.get().getOrderItemId());
        assertEquals(form.getQuantity(), update.get().getQuantity());
        assertEquals(client.getEmail(), update.get().getEmail());
    }


    @Test
    @DisplayName("Caso o id de pedido nao encontrado")
    public void updateCase2() {
        OrderItemUpdateForm form = formUpdate();

        when(productRepository.findByProductName(any())).thenReturn(Optional.ofNullable(product));
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(orderItemRepository.findById(any())).thenReturn(Optional.empty());
        when(orderItemRepository.save(any())).thenReturn(order);


        Optional<OrderItemDto> update = orderItemService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("se O Id Cliente Nao For Correspondete Do Pedido Deve Retornar Optianl Empty")
    public void updateCase3() {
        OrderItemUpdateForm form = formUpdate();

        when(productRepository.findByProductName(any())).thenReturn(Optional.ofNullable(product));
        when(clientRepository.findById(any())).thenReturn(Optional.empty());
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(order));
        when(orderItemRepository.save(any())).thenReturn(order);

        Optional<OrderItemDto> update = orderItemService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("se O Id Product Nao For Correspondete Do Pedido Deve Retornar Optianl Empty")
    public void updateCase4() {
        OrderItemUpdateForm form = formUpdate();

        when(productRepository.findByProductName(any())).thenReturn(Optional.empty());
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(order));
        when(orderItemRepository.save(any())).thenReturn(order);

        Optional<OrderItemDto> update = orderItemService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("update Se Os Valores Estiverem Null DeveUtilizar Os Dados Ja Existente")
    public void updateCase5() {
        OrderItemUpdateForm form = formUpdate();
        form.setProductName(null);
        form.setProductName(null);
        form.setQuantity(null);

        when(productRepository.findByProductName(any())).thenReturn(Optional.ofNullable(product));
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(order));
        when(orderItemRepository.save(any())).thenReturn(order);


        Optional<OrderItemDto> update = orderItemService.update(form);

        assertNotNull(update);
        assertEquals(product.getProductName(), update.get().getProductName());
        assertEquals(form.getOrderItemId(), update.get().getOrderItemId());
        assertEquals(order.getQuantity(), update.get().getQuantity());
        assertEquals(client.getEmail(), update.get().getEmail());
    }


    @Test
    @DisplayName("update Se Os Valores Estiverem Vazio DeveUtilizar Os Dados Ja Existente")
    public void updateCase6() {
        OrderItemUpdateForm form = formUpdate();
        form.setProductName("");
        form.setProductName("");

        when(productRepository.findByProductName(any())).thenReturn(Optional.ofNullable(product));
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(order));
        when(orderItemRepository.save(any())).thenReturn(order);


        Optional<OrderItemDto> update = orderItemService.update(form);

        assertNotNull(update);
        assertEquals(product.getProductName(), update.get().getProductName());
        assertEquals(form.getOrderItemId(), update.get().getOrderItemId());
        assertEquals(order.getQuantity(), update.get().getQuantity());
        assertEquals(client.getEmail(), update.get().getEmail());
    }


    @Test
    @DisplayName("create Com Dados Corretamente Deve Retornar Os Dados Criado Do Pedido")
    public void createOrder() {

        OrderItemForm form = new OrderItemForm();
        form.setClientId(client.getId());
        form.setProductName(product.getProductName());
        form.setQuantity(order.getQuantity());

        when(productRepository.findByProductName(any())).thenReturn(Optional.ofNullable(product));
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(orderItemRepository.save(any())).thenReturn(order);


        Optional<OrderItemCreateDto> create = orderItemService.create(form);

        assertNotNull(create);
        assertEquals(order.getOrderItemId(), create.get().getOrderItemId());
    }

    @Test
    @DisplayName("caso O Cliente Nao E Cadastrado Nao Pode Fazer Um Pedido")
    public void createOrderCase2() {
        OrderItemForm form = new OrderItemForm();
        form.setClientId(client.getId());
        form.setProductName(product.getProductName());
        form.setQuantity(order.getQuantity());

        when(productRepository.findByProductName(any())).thenReturn(Optional.ofNullable(product));
        when(clientRepository.findById(any())).thenReturn(Optional.empty());
        when(orderItemRepository.save(any())).thenReturn(order);


        Optional<OrderItemCreateDto> update = orderItemService.create(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("caso O Produto Name Nao Achado Nao Pode Fazer Um Pedido")
    public void createOrderCase3() {
        OrderItemForm form = new OrderItemForm();
        form.setClientId(client.getId());
        form.setProductName(product.getProductName());
        form.setQuantity(order.getQuantity());

        when(productRepository.findByProductName(any())).thenReturn(Optional.empty());
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(orderItemRepository.save(any())).thenReturn(order);


        Optional<OrderItemCreateDto> update = orderItemService.create(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("Deletar o pedido pelo id")
    public void deleteOrder() {
        UUID orderId = UUID.randomUUID();

        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(order));

        Boolean update = orderItemService.delete(orderId);

        assertTrue(update);
    }


    @Test
    @DisplayName("Caso ao deletar nao encontre o pedido")
    public void deleteOrderCase2() {
        UUID orderId = UUID.randomUUID();

        when(orderItemRepository.findById(any())).thenReturn(Optional.empty());

        Boolean update = orderItemService.delete(orderId);

        assertFalse(update);
    }
}
