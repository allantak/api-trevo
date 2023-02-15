package br.com.jacto.trevo.service.product;

import static org.junit.Assert.*;

import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
public class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Autowired
    TestEntityManager em;

    public Client client = new Client("testando", "testando@gmail.com", "(14) 99832-20566");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14));
    public OrderItem order = new OrderItem(3, client, product);

    public Culture culture = new Culture("Cerejeiras", product);

    @Test
    public void DeveEstarlistandoProduct() {
        em.persist(product);
        em.persist(culture);
        List<Culture> listCulture = new ArrayList<Culture>();
        listCulture.add(culture);
        product.setCultures(listCulture);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDto> product = productService.getAll(pageable);
        assertNotNull(product);
    }

    @Test
    public void criarUmProductoComAsInformacaoCorretaDeveRetornarOProduto() {
        em.persist(product);
        em.persist(culture);
        List<String> listCulture = new ArrayList<String>();
        listCulture.add(culture.getCultureName());

        ProductForm form = new ProductForm();
        form.setCultures(listCulture);
        form.setProductName(product.getProductName());
        form.setStatus(product.isStatus());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setCreateAt(product.getCreateAt());

        Product product = productService.create(form);
        assertNotNull(product);
        assertEquals(product.getCreateAt(), form.getCreateAt());
        assertEquals(product.getProductName(), form.getProductName());
        assertEquals(product.isStatus(), form.getStatus());
        assertEquals(product.getAreaSize(), form.getAreaSize());
        assertEquals(product.getDescription(), form.getDescription());
    }

    @Test
    public void aoFazerUpdateComIdExistenteDeveFazerUpdate(){
        em.persist(product);
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(product.getProductId());
        form.setProductName("update");
        form.setDescription("update");
        form.setAreaSize(123.0);
        form.setStatus(false);
        form.setCreateAt(LocalDate.parse("2023-02-14"));

        Optional<Product> update = productService.update(form);

        assertNotNull(update);
        assertEquals(LocalDate.parse("2023-02-14"), update.get().getCreateAt());
        assertEquals("update", update.get().getProductName());
        assertEquals(false, update.get().isStatus());
        assertEquals(Optional.of(123.0), Optional.ofNullable(update.get().getAreaSize()));
        assertEquals("update", update.get().getDescription());
    }

    @Test
    public void aoFazerUpdateComIdNaoExistenteDeveRetonarOptinalEmpty(){
        em.persist(product);
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setStatus(product.isStatus());
        form.setCreateAt(product.getCreateAt());

        Optional<Product> update = productService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    public void aoFazerUpdateComIdExistenteComDadosNullDeveRetornarODadoJaExistente(){
        em.persist(product);
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(product.getProductId());


        Optional<Product> update = productService.update(form);

        assertNotNull(update);
        assertEquals(product.getCreateAt(), update.get().getCreateAt());
        assertEquals(product.getProductName(), update.get().getProductName());
        assertEquals(product.isStatus(), update.get().isStatus());
        assertEquals( product.getAreaSize(), update.get().getAreaSize());
        assertEquals(product.getDescription(), update.get().getDescription());
    }

    @Test
    public void aoFazerUpdateComIdExistenteComDadosNullEVazioDeveRetornarODadoJaExistente(){
        em.persist(product);
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(product.getProductId());
        form.setProductName("");
        form.setDescription("");


        Optional<Product> update = productService.update(form);

        assertNotNull(update);
        assertEquals(product.getCreateAt(), update.get().getCreateAt());
        assertEquals(product.getProductName(), update.get().getProductName());
        assertEquals(product.isStatus(), update.get().isStatus());
        assertEquals( product.getAreaSize(), update.get().getAreaSize());
        assertEquals(product.getDescription(), update.get().getDescription());
    }


}
