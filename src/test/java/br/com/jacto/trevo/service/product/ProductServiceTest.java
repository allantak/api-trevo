package br.com.jacto.trevo.service.product;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.com.jacto.trevo.controller.product.dto.ProductCreateDto;
import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.dto.ProductOrderDto;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.ProductRepository;
import jakarta.transaction.Transactional;
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

import java.time.LocalDate;
import java.util.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    ProductService productService;

    @MockBean
    ProductRepository productRepository;

    public Client client = new Client("testando", "testando@gmail.com", "(14) 99832-20566");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14));
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
        form.setCreateAt(product.getCreateAt());
        product.setProductId(UUID.randomUUID());

        when(productRepository.save(any())).thenReturn(product);

        ProductCreateDto product = productService.create(form);
        assertNotNull(product);
        assertNotNull(product.getProductId());
        assertEquals(product.getProductName(), form.getProductName());
    }


    @Test
    @DisplayName("ao Fazer Update Com Id Existente Deve Fazer Atualizacao")
    public void updateProduct(){
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(product.getProductId());
        form.setProductName("update");
        form.setDescription("update");
        form.setAreaSize(123.0);
        form.setStatus(false);
        form.setCreateAt(LocalDate.parse("2023-02-14"));

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
        when(productRepository.save(any())).thenReturn(product);

        Optional<Product> update = productService.update(form);

        assertNotNull(update);
        assertEquals(LocalDate.parse("2023-02-14"), update.get().getCreateAt());
        assertEquals("update", update.get().getProductName());
        assertEquals(false, update.get().isStatus());
        assertEquals(Optional.of(123.0), Optional.ofNullable(update.get().getAreaSize()));
        assertEquals("update", update.get().getDescription());
    }

    @Test
    @DisplayName("ao Fazer Update Com Id Nao Existente Deve Retonar Optinal Empty")
    public void updateProductCase2(){
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setStatus(product.isStatus());
        form.setCreateAt(product.getCreateAt());

        when(productRepository.findById(any())).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(product);

        Optional<Product> update = productService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("ao Fazer Update Com Id Existente Com Dados Null Deve Retornar O Dado Ja Existente")
    public void updateProductCase3(){
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(product.getProductId());

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));
        when(productRepository.save(any())).thenReturn(product);

        Optional<Product> update = productService.update(form);

        assertNotNull(update);
        assertEquals(product.getCreateAt(), update.get().getCreateAt());
        assertEquals(product.getProductName(), update.get().getProductName());
        assertEquals(product.isStatus(), update.get().isStatus());
        assertEquals( product.getAreaSize(), update.get().getAreaSize());
        assertEquals(product.getDescription(), update.get().getDescription());
    }

    @Test
    @DisplayName("ao Fazer Update Com Id Existente Com Dados Null E Vazio Deve Retornar O Dado Ja Existente")
    public void updateProductCase4(){
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
        assertEquals( product.getAreaSize(), update.get().getAreaSize());
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
