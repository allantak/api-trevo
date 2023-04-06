package br.com.jacto.trevo.service.product.culture;

import br.com.jacto.trevo.controller.product.dto.ProductCultureDto;
import br.com.jacto.trevo.controller.product.form.ProductCultureDeleteForm;
import br.com.jacto.trevo.controller.product.form.ProductCultureForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.CultureRepository;
import br.com.jacto.trevo.service.product.CultureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CulturaUpdateServiceTest {
    @InjectMocks
    CultureService cultureService;
    @Mock
    private CultureRepository cultureRepository;

    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);
    public Product product = new Product("Trator Jacto", Product.Status.DISPONIVEL, Product.Category.ELETRICO, "Trator jacto para agricultura", 120.0, 2.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

    public Culture culture = new Culture("Cerejeiras", product);

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

        Optional<ProductCultureDto> update = cultureService.update(form);

        assertNotNull(update);
        assertEquals(form.getCultureName(), update.get().getCultureName());
        assertEquals(form.getCultureId(), update.get().getCultureId());
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

        Optional<ProductCultureDto> update = cultureService.update(form);

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

        Optional<ProductCultureDto> update = cultureService.update(form);

        assertEquals(Optional.empty(), update);
    }

}
