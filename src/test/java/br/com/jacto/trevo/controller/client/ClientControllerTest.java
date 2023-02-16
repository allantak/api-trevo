package br.com.jacto.trevo.controller.client;

import br.com.jacto.trevo.controller.client.dto.ClientDetailDto;
import br.com.jacto.trevo.controller.client.dto.ClientDto;
import br.com.jacto.trevo.controller.client.dto.ClientOrderDto;
import br.com.jacto.trevo.controller.client.form.ClientForm;
import br.com.jacto.trevo.controller.client.form.ClientUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.service.client.ClientService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.Test;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;


    @Autowired
    private JacksonTester<ClientOrderDto> clientOrderDtoJson;
    @Autowired
    private JacksonTester<ClientDto> clientDtoJson;
    @Autowired
    private JacksonTester<ClientForm> clientFormJson;
    @Autowired
    private JacksonTester<ClientDetailDto> clientDetailDtoJson;
    @Autowired
    private JacksonTester<ClientUpdateForm> clientUpdateFormJson;


    public Client client = new Client("testando", "testando@gmail.com", "(14) 99832-20566");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14));
    public OrderItem order = new OrderItem(3, client, product);

    @Test
    @DisplayName("Liste os cliente clientes cadastrados e retorne 200")
    public void listClient() throws Exception {
        UUID clientId = UUID.randomUUID();
        client.setClientId(clientId);
        List<ClientDto> listClient = new ArrayList<ClientDto>();
        listClient.add(new ClientDto(client));

        when(clientService.getAll()).thenReturn(listClient);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'clientId': " + clientId + ", 'email': 'testando@gmail.com'}]"));
    }

    @Test
    @DisplayName("Liste os pedidos do cliente")
    public void clientOrderId() throws Exception {
        UUID clientId = UUID.randomUUID();
        List<OrderItem> listOrder = new ArrayList<OrderItem>();
        listOrder.add(order);

        client.setClientId(clientId);
        client.setOrders(listOrder);
        ClientOrderDto orderDto = new ClientOrderDto(client);

        when(clientService.clientOrder(clientId)).thenReturn(Optional.of(orderDto));

        var response = mockMvc.perform(
                get("/clients/orders/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = clientOrderDtoJson.write(orderDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Retorne error caso o id for invalido")
    public void clientOrderIdCase2() throws Exception {

        var response = mockMvc.perform(
                get("/clients/orders/")
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("Retorne error caso o id esteja no formato errado")
    public void clientOrderIdCase3() throws Exception {

        var response = mockMvc.perform(
                get("/clients/orders/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }

    @Test
    @DisplayName("ao procurar o cliente cadastrado retorne seus dados detalhado")
    public void clientId() throws Exception {
        UUID clientId = UUID.randomUUID();


        client.setClientId(clientId);

        ClientDetailDto orderDto = new ClientDetailDto(client);

        when(clientService.getId(clientId)).thenReturn(Optional.of(orderDto));

        var response = mockMvc.perform(
                get("/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = clientDetailDtoJson.write(orderDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("ao procurar o cliente nao cadastrado retorne Not found")
    public void clientIdCase2() throws Exception {
        UUID clientId = UUID.randomUUID();


        client.setClientId(clientId);

        ClientDetailDto orderDto = new ClientDetailDto(client);

        when(clientService.getId(clientId)).thenReturn(Optional.of(orderDto));

        var response = mockMvc.perform(
                get("/clients/")
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    @DisplayName("ao procurar o cliente nao sendo no formato UUID deve retornar Bad Request")
    public void clientIdCase3() throws Exception {
        UUID clientId = UUID.randomUUID();


        client.setClientId(clientId);

        ClientDetailDto orderDto = new ClientDetailDto(client);

        when(clientService.getId(clientId)).thenReturn(Optional.of(orderDto));

        var response = mockMvc.perform(
                get("/clients/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    @DisplayName("Cadastro de um cliente corretamente")
    public void createClient() throws Exception {

        UUID clientId = UUID.randomUUID();
        client.setClientId(clientId);

        ClientForm form = new ClientForm();
        form.setClientName(client.getClientName());
        form.setPhone(client.getPhone());
        form.setEmail(client.getEmail());

        when(clientService.create(any())).thenReturn(client);

        var response = mockMvc.perform(
                post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        var jsonExpect = clientDtoJson.write(new ClientDto(client)).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Cadastro de um cliente corretamente")
    public void createClientCase2() throws Exception {

        UUID clientId = UUID.randomUUID();
        client.setClientId(clientId);

        ClientForm form = new ClientForm();
        form.setClientName(client.getClientName());
        form.setPhone(client.getPhone());
        form.setEmail(client.getEmail());

        when(clientService.create(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        var response = mockMvc.perform(
                post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());

    }

    @Test
    @DisplayName("Ao Cadastrar um cliente com dados nao validos")
    public void createClientCase3() throws Exception {

        UUID clientId = UUID.randomUUID();
        client.setClientId(clientId);

        ClientForm form = new ClientForm();

        when(clientService.create(any())).thenReturn(client);

        var response = mockMvc.perform(
                post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }

    @Test
    @DisplayName("Update corretamente deve retornar o cliente atualizado")
    public void updateClient() throws Exception {

        UUID clientId = UUID.randomUUID();
        client.setClientId(clientId);

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(clientId);
        form.setClientName(client.getClientName());
        form.setPhone(client.getPhone());
        form.setEmail(client.getEmail());

        when(clientService.update(any())).thenReturn(Optional.ofNullable(client));

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = clientDetailDtoJson.write(new ClientDetailDto(client)).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Caso update nao encontrar o cliente retorno Not Found")
    public void updateClientCase2() throws Exception {

        UUID clientId = UUID.randomUUID();

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(clientId);
        form.setClientName(client.getClientName());
        form.setPhone(client.getPhone());
        form.setEmail(client.getEmail());

        when(clientService.update(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    @DisplayName("Caso id do update nao valido retorne Bad request")
    public void updateClientCase3() throws Exception {

        UUID clientId = UUID.randomUUID();

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientName(client.getClientName());
        form.setPhone(client.getPhone());
        form.setEmail(client.getEmail());

        when(clientService.update(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Caso email vier com a formatacao errada ou vazi retorne Bad request")
    public void updateClientCase4() throws Exception {

        UUID clientId = UUID.randomUUID();

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientName(client.getClientName());
        form.setPhone(client.getPhone());
        form.setEmail("");

        when(clientService.update(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Caso Nome, Telefone e email estiver null retorne os dados ja existente sem alteracao")
    public void updateClientCase5() throws Exception {

        UUID clientId = UUID.randomUUID();

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(clientId);

        when(clientService.update(any())).thenReturn(Optional.ofNullable(client));

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = clientDetailDtoJson.write(new ClientDetailDto(client)).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Caso Nome, Telefone estiver vazio retorne os dados ja existente sem alteracao")
    public void updateClientCase6() throws Exception {

        UUID clientId = UUID.randomUUID();

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(clientId);
        form.setClientName("");
        form.setPhone("");

        when(clientService.update(any())).thenReturn(Optional.ofNullable(client));

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = clientDetailDtoJson.write(new ClientDetailDto(client)).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }


    @Test
    @DisplayName("Ao deletar retorne No-content")
    public void deleteClientId() throws Exception {
        UUID clientId = UUID.randomUUID();

        ClientDetailDto orderDto = new ClientDetailDto(client);

        when(clientService.delete(clientId)).thenReturn(Optional.ofNullable(client));

        var response = mockMvc.perform(
                delete("/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

    }

    @Test
    @DisplayName("Ao deletar com id no fomarto incorreto deve retornar um BAD REQUEST")
    public void deleteCase2() throws Exception {
        UUID clientId = UUID.randomUUID();

        ClientDetailDto orderDto = new ClientDetailDto(client);

        when(clientService.delete(clientId)).thenReturn(Optional.ofNullable(client));

        var response = mockMvc.perform(
                delete("/clients/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }

    @Test
    @DisplayName("Ao deletar com id nao cadastrado deve retornar um NOT FOUND")
    public void deleteCase3() throws Exception {
        UUID clientId = UUID.randomUUID();

        ClientDetailDto orderDto = new ClientDetailDto(client);

        when(clientService.delete(clientId)).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                delete("/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }


}
