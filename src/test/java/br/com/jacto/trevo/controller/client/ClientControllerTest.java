package br.com.jacto.trevo.controller.client;

import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.client.dto.ClientDetailDto;
import br.com.jacto.trevo.controller.client.dto.ClientDto;
import br.com.jacto.trevo.controller.client.dto.ClientOrderDto;
import br.com.jacto.trevo.controller.client.form.ClientForm;
import br.com.jacto.trevo.controller.client.form.ClientUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.AccountRepository;
import br.com.jacto.trevo.service.client.ClientService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ClientController.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private AccountRepository managerRepository;

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


    public Account account = new Account("test", "12345");
    public Product product = new Product("Trator Jacto", Product.Status.DISPONIVEL, "Trator jacto para agricultura", 120.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

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
    @DisplayName("Nao ter permissao para listar os clientes")
    public void listClientCase2() throws Exception {
        UUID clientId = UUID.randomUUID();
        client.setClientId(clientId);
        List<ClientDto> listClient = new ArrayList<ClientDto>();
        listClient.add(new ClientDto(client));

        when(clientService.getAll()).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isForbidden());
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
        UUID clientId = UUID.randomUUID();
        when(clientService.clientOrder(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                get("/clients/orders/" + clientId)
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

        when(clientService.getEmail(client.getEmail())).thenReturn(Optional.of(orderDto));

        var response = mockMvc.perform(
                get("/clients/" + client.getEmail())
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

        when(clientService.getEmail(client.getEmail())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                get("/clients/" + client.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
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
        ClientDto clientDto = new ClientDto(client);

        when(clientService.create(any())).thenReturn(clientDto);

        var response = mockMvc.perform(
                post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertTrue(response.containsHeader("Location"));
        var jsonExpect = clientDtoJson.write(clientDto).getJson();

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

        when(clientService.create(any())).thenReturn(new ClientDto(client));

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

        ClientDetailDto clientDetailDto = new ClientDetailDto(client);

        when(clientService.update(any())).thenReturn(Optional.of(clientDetailDto));

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = clientDetailDtoJson.write(clientDetailDto).getJson();

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
        ClientDetailDto clientDetailDto = new ClientDetailDto(client);

        when(clientService.update(any())).thenReturn(Optional.of(clientDetailDto));

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = clientDetailDtoJson.write(clientDetailDto).getJson();

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

        ClientDetailDto clientDetailDto = new ClientDetailDto(client);

        when(clientService.update(any())).thenReturn(Optional.of(clientDetailDto));

        var response = mockMvc.perform(
                put("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientUpdateFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = clientDetailDtoJson.write(clientDetailDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }


    @Test
    @DisplayName("Ao deletar retorne No-content")
    public void deleteClientId() throws Exception {
        UUID clientId = UUID.randomUUID();

        when(clientService.delete(clientId)).thenReturn(true);

        var response = mockMvc.perform(
                delete("/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    @DisplayName("Ao deletar com id no fomarto incorreto deve retornar um BAD REQUEST")
    public void deleteCase2() throws Exception {
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
        when(clientService.delete(clientId)).thenReturn(false);

        var response = mockMvc.perform(
                delete("/clients/" + clientId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }


}
