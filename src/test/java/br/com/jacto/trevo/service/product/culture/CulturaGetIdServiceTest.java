package br.com.jacto.trevo.service.product.culture;

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
public class CulturaGetIdServiceTest {
    @InjectMocks
    CultureService cultureService;
    @Mock
    private CultureRepository cultureRepository;

    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);
    public Product product = new Product("Trator Jacto", Product.Status.DISPONIVEL, Product.Category.ELETRICO, "Trator jacto para agricultura", 120.0, 2.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

    public Culture culture = new Culture("Cerejeiras", product);


    @Test
    @DisplayName("mostra cultura pelo id")
    public void getCultureId() throws IOException {
        culture.setCultureId(UUID.randomUUID());

        when(cultureRepository.findById(any())).thenReturn(Optional.ofNullable(culture));

        Optional<Culture> response = cultureService.getId(culture.getCultureId());

        assertNull(response);
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
}
