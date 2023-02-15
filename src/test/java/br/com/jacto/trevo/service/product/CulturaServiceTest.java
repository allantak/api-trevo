package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.product.form.ProductCultureDeleteForm;
import br.com.jacto.trevo.controller.product.form.ProductCultureForm;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
public class CulturaServiceTest {
    @Autowired
    CultureService cultureService;

    @Autowired
    TestEntityManager em;
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14));

    public Culture culture = new Culture("Cerejeiras", product);

    @Test
    public void updateComDadosCorretamenteDeveRetornarOsDadosAtualizado(){
        em.persist(product);
        em.persist(culture);

        ProductCultureForm form = new ProductCultureForm();
        form.setCultureId(culture.getCultureId());
        form.setProductId(product.getProductId());
        form.setCultureName("Update");

        Optional<Culture> update = cultureService.update(form);

        assertNotNull(update);
        assertEquals(form.getCultureName(), update.get().getCultureName());
        assertEquals(form.getCultureId(), update.get().getCultureId());
        assertEquals(form.getProductId(), update.get().getProduct().getProductId());
    }


    @Test
    public void updateComProductIdNaoCorrespondenteDeveRetornarOptinalEmpty() {
        em.persist(product);
        em.persist(culture);

        ProductCultureForm form = new ProductCultureForm();
        form.setCultureId(culture.getCultureId());
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setCultureName("Update");

        Optional<Culture> update = cultureService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    public void updateComCultureIdNaoCorrespondenteDeveRetornarOptinalEmpty() {
        em.persist(product);
        em.persist(culture);

        ProductCultureForm form = new ProductCultureForm();
        form.setCultureId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductId(product.getProductId());
        form.setCultureName("Update");

        Optional<Culture> update = cultureService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    public void deleteComDadosCorretamenteDeveRetornarDadosDeletados(){
        em.persist(product);
        em.persist(culture);

        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(culture.getCultureId());
        form.setProductId(product.getProductId());

        Optional<Culture> update = cultureService.delete(form);

        assertNotNull(update);
        assertEquals(form.getCultureId(), update.get().getCultureId());
        assertEquals(form.getProductId(), update.get().getProduct().getProductId());
    }


    @Test
    public void deleteComProductIdNaoCorrespondenteDeveRetornarOptinalEmpty() {
        em.persist(product);
        em.persist(culture);

        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(culture.getCultureId());
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));


        Optional<Culture> update = cultureService.delete(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    public void deleteComCultureIdNaoCorrespondenteDeveRetornarOptinalEmpty() {
        em.persist(product);
        em.persist(culture);

        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductId(product.getProductId());

        Optional<Culture> update = cultureService.delete(form);

        assertEquals(Optional.empty(), update);
    }






}
