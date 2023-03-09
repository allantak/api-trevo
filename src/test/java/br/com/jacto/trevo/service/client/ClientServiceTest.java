package br.com.jacto.trevo.service.client;

import br.com.jacto.trevo.controller.client.dto.ClientDetailDto;
import br.com.jacto.trevo.controller.client.dto.ClientDto;
import br.com.jacto.trevo.controller.client.dto.ClientOrderDto;
import br.com.jacto.trevo.controller.client.form.ClientForm;
import br.com.jacto.trevo.controller.client.form.ClientUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.ClientRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @MockBean
    private ClientRepository clientRepository;

    public Client client = new Client("testando", "testando@gmail.com", "(14) 99832-20566");

    public Manager manager = new Manager("test", "12345");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14), manager);

    public OrderItem order = new OrderItem(3, client, product);

    @Test
    @DisplayName("deve Trazer Uma Lista De Cadastro De Cliente No Formato Do DTO")
    public void listClient() {

        client.setClientId(UUID.randomUUID());

        List<Client> listClient = new ArrayList<Client>();
        listClient.add(client);

        when(clientRepository.findAll()).thenReturn(listClient);

        List<ClientDto> testList = clientService.getAll();

        assertNotNull(testList);
        assertEquals(client.getId(), testList.get(0).getClientId());
        assertEquals(client.getEmail(), testList.get(0).getEmail());
    }

    @Test
    @DisplayName("deve Retornar Um Cliente Detalhado")
    public void clientDetail() {

        client.setClientId(UUID.randomUUID());

        when(clientRepository.findByEmail(any())).thenReturn(Optional.ofNullable(client));

        Optional<ClientDetailDto> clientDetail = clientService.getEmail(client.getEmail());

        assertEquals(client.getId(), clientDetail.get().getClientId());
        assertEquals(client.getEmail(), clientDetail.get().getEmail());
        assertEquals(client.getPhone(), clientDetail.get().getPhone());
        assertEquals(client.getClientName(), clientDetail.get().getClientName());
    }

    @Test
    @DisplayName("deve Retornar False Se For Invalido O Cliente Id Optinal Empty")
    public void clientDetailCase2() {
        UUID clientId = UUID.fromString("ce896c60-05a8-465b-a7ad-706e1cc17169");

        when(clientRepository.findByEmail(any())).thenReturn(Optional.empty());

        Optional<ClientDetailDto> clientDetail = clientService.getEmail(client.getEmail());

        assertEquals(Optional.empty(), clientDetail);
    }

    @Test
    @DisplayName("deve Criar E Retornar Um Cadastro Novo De Cliente")
    public void createCliente() {
        client.setClientId(UUID.randomUUID());

        ClientForm form = new ClientForm();
        form.setClientName("Testando");
        form.setEmail(client.getEmail());
        form.setPhone("(14) 99832-20566");

        when(clientRepository.save(any())).thenReturn(client);

        ClientDto create = clientService.create(form);

        assertEquals(form.getEmail(), create.getEmail());
        assertNotNull(create.getClientId());
    }


    @Test
    @DisplayName("Ao Fazer Update Com Todas As Informacoes Corretas Deve Retornar Client Atualizado")
    public void updateClient() {
        client.setClientId(UUID.randomUUID());

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(client.getId());
        form.setEmail("atualizado@gmail.com");
        form.setClientName("Atualizado");
        form.setPhone("17 9982294859");

        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(clientRepository.save(any())).thenReturn(client);

        Optional<ClientDetailDto> update = clientService.update(form);

        assertEquals(form.getClientId(), update.get().getClientId());
        assertEquals(form.getEmail(), update.get().getEmail());
        assertEquals(form.getPhone(), update.get().getPhone());
        assertEquals(form.getClientName(), update.get().getClientName());
    }

    @Test
    @DisplayName("Ao Fazer Update Com Id Nao Existente Deve Retornar Optinal Empty")
    public void updateClientCase2() {
        client.setClientId(UUID.randomUUID());

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setEmail("atualizado@gmail.com");
        form.setClientName("Atualizado");
        form.setPhone("17 9982294859");

        when(clientRepository.findById(any())).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenReturn(client);

        Optional<ClientDetailDto> update = clientService.update(form);

        assertEquals(Optional.empty(), update);

    }

    @Test
    @DisplayName("Ao Fazer Update Com Campo Vazio Deve Retornar O Dado Ja Existente")
    public void updateClientCase3() {
        client.setClientId(UUID.randomUUID());

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(client.getId());
        form.setEmail("");
        form.setClientName("");
        form.setPhone("");


        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(clientRepository.save(any())).thenReturn(client);

        Optional<ClientDetailDto> update = clientService.update(form);
        assertEquals(client.getId(), update.get().getClientId());
        assertEquals(client.getClientName(), update.get().getClientName());
        assertEquals(client.getEmail(), update.get().getEmail());
        assertEquals(client.getPhone(), update.get().getPhone());
    }

    @Test
    @DisplayName("Ao Fazer Update Com Campo Null Deve Retornar O Dado Ja Existente")
    public void updateClietCase4() {
        client.setClientId(UUID.randomUUID());

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(client.getId());

        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));
        when(clientRepository.save(any())).thenReturn(client);

        Optional<ClientDetailDto> update = clientService.update(form);
        assertEquals(client.getId(), update.get().getClientId());
        assertEquals(client.getClientName(), update.get().getClientName());
        assertEquals(client.getEmail(), update.get().getEmail());
        assertEquals(client.getPhone(), update.get().getPhone());
    }

    @Test
    @DisplayName("Ao Fazer Delete Com Id Corretamente Deve Excluir O Cliente")
    public void deleteClient() {
        client.setClientId(UUID.randomUUID());

        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));

        Boolean deleteClient = clientService.delete(client.getId());

        assertTrue(deleteClient);

    }

    @Test
    @DisplayName("Ao Fazer Delete Com Id Nao Existente Deve Retorna False")
    public void deleteClientCase2() {

        client.setClientId(UUID.randomUUID());

        when(clientRepository.findById(any())).thenReturn(Optional.empty());

        boolean deleteClient = clientService.delete(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        assertFalse(deleteClient);
    }

    @Test
    @DisplayName("Cliente Com Suas Pedidos")
    public void clientOrder() {
        List<OrderItem> list = new ArrayList<OrderItem>();
        list.add(order);

        client.setOrders(list);

        client.setClientId(UUID.randomUUID());

        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(client));

        Optional<ClientOrderDto> clientOrder = clientService.clientOrder(client.getId());

        assertNotNull(clientOrder.get());
        assertEquals(client.getOrders().get(0).getOrderItemId(), clientOrder.get().getOrders().get(0).getOrderItemId());
        assertEquals(client.getClientName(), clientOrder.get().getClientName());
        assertEquals(client.getPhone(), clientOrder.get().getPhone());
        assertEquals(client.getEmail(), clientOrder.get().getEmail());
    }

    @Test
    @DisplayName("Ao Listar A Order Do Cliente Com Id Nao Existente Retorne Optinal Empty")
    public void clientOrderCase2() {
        when(clientRepository.findById(any())).thenReturn(Optional.empty());
        Optional<ClientOrderDto> clientOrder = clientService.clientOrder(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        assertEquals(Optional.empty(), clientOrder);
    }
}