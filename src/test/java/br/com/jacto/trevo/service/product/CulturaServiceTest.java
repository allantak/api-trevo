package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.product.form.ProductCultureDeleteForm;
import br.com.jacto.trevo.controller.product.form.ProductCultureForm;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.CultureRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class CulturaServiceTest {
    @Autowired
    CultureService cultureService;
    @MockBean
    private CultureRepository cultureRepository;

    public Manager manager = new Manager("test", "12345");
    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14), manager);

    public Culture culture = new Culture("Cerejeiras", product);


    @Test
    @DisplayName("mostra cultura pelo id")
    public void getCultureId() throws IOException {
        culture.setCultureId(UUID.randomUUID());

        when(cultureRepository.findById(any())).thenReturn(Optional.ofNullable(culture));

        Optional<Culture> response = cultureService.getId(culture.getCultureId());

        assertNotNull(response);
        assertNotNull(response.get().getCultureId());
        assertEquals(culture.getCultureName(), response.get().getCultureName());
    }

    @Test
    @DisplayName("Caso nao encontre o id da culture")
    public void getCultureIdCase2() throws IOException {
        culture.setCultureId(UUID.randomUUID());

        when(cultureRepository.findById(any())).thenReturn(Optional.empty());

        Optional<Culture> response = cultureService.getId(culture.getCultureId());

        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("atualizacao de cultura")
    public void updateCulture() {
        product.setProductId(UUID.randomUUID());
        culture.setCultureId(UUID.randomUUID());

        ProductCultureForm form = new ProductCultureForm();
        form.setCultureId(culture.getCultureId());
        form.setProductId(product.getProductId());
        form.setCultureName("Update");
        when(cultureRepository.findById(any())).thenReturn(Optional.ofNullable(culture));
        when(cultureRepository.save(any())).thenReturn(culture);

        Optional<Culture> update = cultureService.update(form);

        assertNotNull(update);
        assertEquals(form.getCultureName(), update.get().getCultureName());
        assertEquals(form.getCultureId(), update.get().getCultureId());
        assertEquals(form.getProductId(), update.get().getProduct().getProductId());
    }


    @Test
    @DisplayName("ao fazer atualizacao com id produto nao valido")
    public void updateCultureCase2() {
        product.setProductId(UUID.randomUUID());
        culture.setCultureId(UUID.randomUUID());

        ProductCultureForm form = new ProductCultureForm();
        form.setCultureId(culture.getCultureId());
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setCultureName("Update");

        when(cultureRepository.findById(any())).thenReturn(Optional.ofNullable(culture));
        when(cultureRepository.save(any())).thenReturn(culture);

        Optional<Culture> update = cultureService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("Ao nao achar id da cultura para atualizar")
    public void updateCultureCase3() {
        product.setProductId(UUID.randomUUID());
        culture.setCultureId(UUID.randomUUID());

        ProductCultureForm form = new ProductCultureForm();
        form.setCultureId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductId(product.getProductId());
        form.setCultureName("Update");

        when(cultureRepository.findById(any())).thenReturn(Optional.empty());
        when(cultureRepository.save(any())).thenReturn(culture);

        Optional<Culture> update = cultureService.update(form);

        assertEquals(Optional.empty(), update);
    }

    @Test
    @DisplayName("delete cultura")
    public void deleteCulture() {
        product.setProductId(UUID.randomUUID());
        culture.setCultureId(UUID.randomUUID());

        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(culture.getCultureId());
        form.setProductId(product.getProductId());

        when(cultureRepository.findById(any())).thenReturn(Optional.ofNullable(culture));

        Boolean update = cultureService.delete(form);

        assertTrue(update);
    }


    @Test
    @DisplayName("nao encontrou id do produto relacionado com cultura")
    public void deleteCultureCase2() {
        product.setProductId(UUID.randomUUID());
        culture.setCultureId(UUID.randomUUID());

        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(culture.getCultureId());
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));

        when(cultureRepository.findById(any())).thenReturn(Optional.ofNullable(culture));

        Boolean update = cultureService.delete(form);

        assertFalse(update);
    }

    @Test
    @DisplayName("Caso id culture nao for encontrado")
    public void deleteCase3() {
        product.setProductId(UUID.randomUUID());
        culture.setCultureId(UUID.randomUUID());

        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductId(product.getProductId());

        when(cultureRepository.findById(any())).thenReturn(Optional.empty());

        Boolean update = cultureService.delete(form);

        assertFalse(update);
    }

}
