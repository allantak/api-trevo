package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.product.dto.ProductCreateDto;
import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.dto.ProductOrderDto;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.AccountRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    ProductService productService;

    @MockBean
    ProductRepository productRepository;

    @MockBean
    AccountRepository managerRepository;


    public Client client = new Client("testando", "testando@gmail.com", "(14) 99832-20566");
    public Account account = new Account("test", "12345");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

    public OrderItem order = new OrderItem(3, client, product);
    public Culture culture = new Culture("Cerejeiras", product);

    @Test
    @DisplayName("Deve Estar listando Product")
    public void listProduct() {
        List<Culture> listCulture = new ArrayList<Culture>();
        listCulture.add(culture);
        product.setCultures(listCulture);
        List<Product> productList = Collections.singletonList(product);
        PageImpl<Product> page = new PageImpl<>(productList);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDto> result = productService.getAll(pageable);
        assertNotNull(result);
        assertEquals(productList.size(), result.getSize());
    }

    @Test
    @DisplayName("mostrar o produto com seus pedidos")
    public void productOrderId() {
        List<OrderItem> listCulture = new ArrayList<OrderItem>();
        listCulture.add(order);
        product.setOrders(listCulture);
        product.setProductId(UUID.randomUUID());

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));

        Optional<ProductOrderDto> result = productService.productOrder(product.getProductId());
        assertNotNull(result);
        assertEquals(product.getProductName(), result.get().getProductName());
        assertEquals(product.getOrders().get(0).getOrderItemId(), result.get().getOrders().get(0).getOrderItemId());
        assertEquals(product.isStatus(), result.get().getStatus());
        assertEquals(product.getDescription(), result.get().getDescription());
    }

    @Test
    @DisplayName("criar Um Producto Com As Informacao Correta Deve Retornar O Produto")
    public void createProduct() {
        List<String> listCulture = new ArrayList<String>();
        listCulture.add(culture.getCultureName());

        ProductForm form = new ProductForm();
        form.setCultures(listCulture);
        form.setProductName(product.getProductName());
        form.setStatus(product.isStatus());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        product.setProductId(UUID.randomUUID());

        when(productRepository.save(any())).thenReturn(product);
        when(managerRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Optional<ProductCreateDto> product = productService.create(form);
        assertNotNull(product);
        assertNotNull(product.get().getProductId());
        assertEquals(product.get().getProductName(), form.getProductName());
    }

    @Test
    @DisplayName("Cadastrar um produto sem o gerente")
    public void createProductCase2() {
        List<String> listCulture = new ArrayList<String>();
        listCulture.add(culture.getCultureName());

        ProductForm form = new ProductForm();
        form.setCultures(listCulture);
        form.setProductName(product.getProductName());
        form.setStatus(product.isStatus());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        product.setProductId(UUID.randomUUID());

        when(productRepository.save(any())).thenReturn(product);
        when(managerRepository.findById(any())).thenReturn(Optional.empty());

        Optional<ProductCreateDto> product = productService.create(form);
        assertEquals(Optional.empty(), product);
    }


    @Test
    @DisplayName("ao Fazer Update Com Id Existente Deve Fazer Atualizacao")
    public void updateProduct() {
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(product.getProductId());
        form.setProductName("update");
        form.setDescription("update");
        form.setAreaSize(123.0);
        form.setStatus(false);

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
        when(productRepository.save(any())).thenReturn(product);

        Optional<Product> update = productService.update(form);

        assertNotNull(update);
        assertEquals("update", update.get().getProductName());
        assertFalse(update.get().isStatus());
        assertEquals(Optional.of(123.0), Optional.ofNullable(update.get().getAreaSize()));
        assertEquals("update", update.get().getDescription());
    }

    @Test
    @DisplayName("ao Fazer Update Com Id Nao Existente Deve Retonar Optinal Empty")
    public void updateProductCase2() {
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setStatus(product.isStatus());

        when(productRepository.findById(any())).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(product);

        Optional<Product> update = productService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("ao Fazer Update Com Id Existente Com Dados Null Deve Retornar O Dado Ja Existente")
    public void updateProductCase3() {
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(product.getProductId());

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
        when(productRepository.save(any())).thenReturn(product);

        Optional<Product> update = productService.update(form);

        assertNotNull(update);
        assertEquals(product.getCreateAt(), update.get().getCreateAt());
        assertEquals(product.getProductName(), update.get().getProductName());
        assertEquals(product.isStatus(), update.get().isStatus());
        assertEquals(product.getAreaSize(), update.get().getAreaSize());
        assertEquals(product.getDescription(), update.get().getDescription());
    }

    @Test
    @DisplayName("ao Fazer Update Com Id Existente Com Dados Null E Vazio Deve Retornar O Dado Ja Existente")
    public void updateProductCase4() {
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(product.getProductId());
        form.setProductName("");
        form.setDescription("");

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
        when(productRepository.save(any())).thenReturn(product);

        Optional<Product> update = productService.update(form);

        assertNotNull(update);
        assertEquals(product.getCreateAt(), update.get().getCreateAt());
        assertEquals(product.getProductName(), update.get().getProductName());
        assertEquals(product.isStatus(), update.get().isStatus());
        assertEquals(product.getAreaSize(), update.get().getAreaSize());
        assertEquals(product.getDescription(), update.get().getDescription());
    }


    @Test
    @DisplayName("Deletar o pedido pelo id")
    public void deleteOrder() {
        UUID orderId = UUID.randomUUID();

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));

        Boolean update = productService.delete(orderId);

        assertTrue(update);
    }


    @Test
    @DisplayName("Caso ao deletar nao encontre o pedido")
    public void deleteOrderCase2() {
        UUID orderId = UUID.randomUUID();

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        Boolean update = productService.delete(orderId);

        assertFalse(update);
    }

}
