package br.com.jacto.trevo.service.client;

import br.com.jacto.trevo.controller.client.dto.ClientDetailDto;
import br.com.jacto.trevo.controller.client.dto.ClientDto;

import static org.junit.Assert.*;

import br.com.jacto.trevo.controller.client.dto.ClientOrderDto;
import br.com.jacto.trevo.controller.client.form.ClientForm;
import br.com.jacto.trevo.controller.client.form.ClientUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private TestEntityManager em;
    public Client client = new Client("testando", "testando@gmail.com", "(14) 99832-20566");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura",120.0, LocalDate.ofEpochDay(2023-02-14));
    public OrderItem order = new OrderItem(3, client, product);

    @Test
    public void deveTrazerUmaListaDeCadastroDeClienteNoFormatoDoDTO() {


        UUID id = (UUID) em.persistAndGetId(client);
        List<ClientDto> testList = clientService.getAll();

        assertNotNull(testList);
        assertEquals(id, testList.get(0).getClientId());
        assertEquals(client.getEmail(), testList.get(0).getEmail());
    }

    @Test
    public void deveRetornarUmClienteDetalhado(){

        UUID id = (UUID) em.persistAndGetId(client);
        Optional<ClientDetailDto> clientDetail =clientService.getId(id);

        assertEquals(id, clientDetail.get().getClientId());
        assertEquals(client.getEmail(), clientDetail.get().getEmail());
        assertEquals(client.getPhone(), clientDetail.get().getPhone());
        assertEquals(client.getClientName(), clientDetail.get().getClientName());
    }

    @Test
    public void deveRetornarFalseSeForInvalidoOClienteIdOptinalEmpty(){
        UUID clientId = UUID.fromString("ce896c60-05a8-465b-a7ad-706e1cc17169");

        Optional<ClientDetailDto> clientDetail =clientService.getId(clientId);

        assertEquals(Optional.empty(), clientDetail);
    }

    @Test
    public void deveCriarERetornarUmCadastroNovoDeCliente(){

        ClientForm form = new ClientForm();
        form.setClientName("Testando");
        form.setEmail("test@gmail.com");
        form.setPhone("(14) 99832-20566");


        ClientDto create = clientService.create(form);

        assertEquals(form.getEmail(), create.getEmail());
        assertNotNull(create.getClientId());
    }


    @Test
    public void AoFazerUpdateComTodasAsInformacoesCorretasDeveRetornarClientAtualizado(){
        UUID id = (UUID) em.persistAndGetId(client);

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(id);
        form.setEmail("atualizado@gmail.com");
        form.setClientName("Atualizado");
        form.setPhone("17 9982294859");

        Optional<ClientDetailDto> update = clientService.update(form);

        assertEquals(form.getClientId(), update.get().getClientId());
        assertEquals(form.getEmail(), update.get().getEmail());
        assertEquals(form.getPhone(), update.get().getPhone());
        assertEquals(form.getClientName(), update.get().getClientName());
    }

    @Test
    public void AoFazerUpdateComIdNaoExistenteDeveRetornarOptinalEmpty(){
        UUID id = (UUID) em.persistAndGetId(client);

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setEmail("atualizado@gmail.com");
        form.setClientName("Atualizado");
        form.setPhone("17 9982294859");

        Optional<ClientDetailDto> update = clientService.update(form);

        assertEquals(Optional.empty(), update);

    }

    @Test
    public void AoFazerUpdateComCampoVazioDeveRetornarODadoJaExistente(){
        UUID id = (UUID) em.persistAndGetId(client);

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(id);
        form.setEmail("");
        form.setClientName("");
        form.setPhone("");

        Optional<ClientDetailDto> update = clientService.update(form);
        assertEquals(client.getId(), update.get().getClientId());
        assertEquals(client.getClientName(), update.get().getClientName());
        assertEquals(client.getEmail(), update.get().getEmail());
        assertEquals(client.getPhone(), update.get().getPhone());
    }

    @Test
    public void AoFazerUpdateComCampoNullDeveRetornarODadoJaExistente(){
        UUID id = (UUID) em.persistAndGetId(client);

        ClientUpdateForm form = new ClientUpdateForm();
        form.setClientId(id);

        Optional<ClientDetailDto> update = clientService.update(form);
        assertEquals(client.getId(), update.get().getClientId());
        assertEquals(client.getClientName(), update.get().getClientName());
        assertEquals(client.getEmail(), update.get().getEmail());
        assertEquals(client.getPhone(), update.get().getPhone());
    }

    @Test
    public void AoFazerDeleteComIdCorretamenteDeveExcluirOCliente(){
        UUID id = (UUID) em.persistAndGetId(client);

        Boolean deleteClient = clientService.delete(id);
        Optional<ClientDetailDto> findClient = clientService.getId(id);
        assertTrue(deleteClient);
        assertEquals(Optional.empty(), findClient);

    }

    @Test
    public void AoFazerDeleteComIdNaoExistenteDeveRetornaFalse(){
        UUID id = (UUID) em.persistAndGetId(client);

        boolean deleteClient = clientService.delete(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        assertFalse(deleteClient);
    }


    @Test
    public void ListarClienteComSuasPedidos(){
        em.persistAndFlush(client);
        em.persistAndFlush(product);
        em.persist(order);

        List<OrderItem> list = new ArrayList<OrderItem>();
        list.add(order);

        client.setOrders(list);
        product.setOrders(list);

        em.persist(client);
        em.persist(product);
        em.flush();

        Optional<ClientOrderDto> clientOrder = clientService.clientOrder(client.getId());

        assertNotNull(clientOrder.get());
        assertEquals(client.getOrders().get(0).getOrderItemId(), clientOrder.get().getOrders().get(0).getOrderItemId());
        assertEquals(client.getClientName(), clientOrder.get().getClientName());
        assertEquals(client.getPhone(), clientOrder.get().getPhone());
        assertEquals(client.getEmail(), clientOrder.get().getEmail());
    }

    @Test
    public void AoListarAOrderDoClienteComIdNaoExistenteRetorneOptinalEmpty(){
        Optional<ClientOrderDto> clientOrder = clientService.clientOrder(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        assertEquals(Optional.empty(), clientOrder);
    }
}