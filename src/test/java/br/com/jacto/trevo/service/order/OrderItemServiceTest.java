package br.com.jacto.trevo.service.order;

import br.com.jacto.trevo.controller.order.dto.OrderItemCreateDto;
import br.com.jacto.trevo.controller.order.dto.OrderItemDto;
import br.com.jacto.trevo.controller.order.form.OrderItemForm;
import br.com.jacto.trevo.controller.order.form.OrderItemUpdateForm;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
public class OrderItemServiceTest {
    @Autowired
    OrderItemService orderItemService;

    @Autowired
    TestEntityManager em;

    public Client client = new Client("testando", "testando@gmail.com", "(14) 99832-20566");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14));
    public OrderItem order = new OrderItem(3, client, product);

    public void persisting() {
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
    }


    @Test
    public void updateComDadosCorretamenteDeveRetornarOsDadosAtualizadoDoPedido() {
        persisting();
        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(order.getOrderItemId());
        form.setClientId(client.getId());
        form.setProductName(product.getProductName());
        form.setQuantity(order.getQuantity());


        Optional<OrderItemDto> update = orderItemService.update(form);

        assertNotNull(update);
        assertEquals(form.getProductName(), update.get().getProductName());
        assertEquals(form.getOrderItemId(), update.get().getOrderItemId());
        assertEquals(form.getQuantity(), update.get().getQuantity());
        assertEquals(client.getEmail(), update.get().getEmail());
    }


    @Test
    public void seOPedidoNaoExistirDeveRetornarOptianlEmpty() {
        persisting();
        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setClientId(client.getId());
        form.setProductName(product.getProductName());


        Optional<OrderItemDto> update = orderItemService.update(form);

        assertEquals(Optional.empty(), update);

    }

    @Test
    public void seOIdClienteNaoForCorrespondeteDoPedidoDeveRetornarOptianlEmpty() {
        persisting();
        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(order.getOrderItemId());
        form.setClientId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductName(product.getProductName());
        form.setQuantity(order.getQuantity());


        Optional<OrderItemDto> update = orderItemService.update(form);

        assertEquals(Optional.empty(), update);

    }

    @Test
    public void updateSeOsValoresEstiveremNullDeveUtilizarOsDadosJaExistente() {
        persisting();
        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(order.getOrderItemId());
        form.setClientId(client.getId());


        Optional<OrderItemDto> update = orderItemService.update(form);

        assertNotNull(update);
        assertEquals(product.getProductName(), update.get().getProductName());
        assertEquals(form.getOrderItemId(), update.get().getOrderItemId());
        assertEquals(order.getQuantity(), update.get().getQuantity());
        assertEquals(client.getEmail(), update.get().getEmail());

    }


    @Test
    public void updateSeOsValoresEstiveremVazioDeveRetornarOsValoresJaExistente() {
        persisting();
        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(order.getOrderItemId());
        form.setClientId(client.getId());
        form.setProductName("");

        Optional<OrderItemDto> update = orderItemService.update(form);

        assertEquals(product.getProductName(), update.get().getProductName());
    }

    @Test
    public void casoProdutoNameNaoForEncontradoDeveRetornarUmOptionalEmpty() {
        persisting();

        OrderItemUpdateForm form = new OrderItemUpdateForm();
        form.setOrderItemId(order.getOrderItemId());
        form.setClientId(client.getId());
        form.setProductName("Trator Nao Existente");


        Optional<OrderItemDto> update = orderItemService.update(form);

        assertEquals(Optional.empty(), update);
    }


    @Test
    public void createComDadosCorretamenteDeveRetornarOsDadosCriadoDoPedido() {
        persisting();

        OrderItemForm form = new OrderItemForm();
        form.setClientId(client.getId());
        form.setProductName(product.getProductName());
        form.setQuantity(2);


        Optional<OrderItemCreateDto> update = orderItemService.create(form);

        assertNotNull(update);
        assertNotNull(update.get().getOrderItemId());
    }

    @Test
    public void casoOClienteNaoECadastradoNaoPodeFazerUmPedido() {
        persisting();

        OrderItemForm form = new OrderItemForm();
        form.setClientId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductName(product.getProductName());
        form.setQuantity(2);


        Optional<OrderItemCreateDto> update = orderItemService.create(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    public void casoOProdutoNameNaoAchadoNaoPodeFazerUmPedido() {
        persisting();
        OrderItemForm form = new OrderItemForm();
        form.setClientId(client.getId());
        form.setProductName("Trator Nao Registrado");
        form.setQuantity(2);


        Optional<OrderItemCreateDto> update = orderItemService.create(form);

        assertEquals(Optional.empty(), update);
    }
}
