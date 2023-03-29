package br.com.jacto.trevo.service.account;

import br.com.jacto.trevo.controller.auth.dto.AccountCreateDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDto;
import br.com.jacto.trevo.controller.auth.form.AccountLoginForm;
import br.com.jacto.trevo.controller.auth.form.AccountRegisterForm;
import br.com.jacto.trevo.controller.auth.form.AccountUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.AccountRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ManagerServiceTest {

    @Autowired
    private AccountService managerService;

    @MockBean
    private AccountRepository managerRepository;


    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);

    @Test
    @DisplayName("Procurar pelo gerente existente")
    public void findByUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(managerRepository.findByEmail(any())).thenReturn(userDetails);

        UserDetails result = managerService.loadUserByUsername("teste");


        assertNotNull(result);
        assertEquals(userDetails, result);
    }


    @Test
    @DisplayName("Autenticar gerente")
    public void auth() {
        AccountLoginForm managerForm = new AccountLoginForm();
        managerForm.setEmail(account.getUsername());
        managerForm.setPassword(account.getPassword());

        Authentication result = managerService.auth(managerForm);


        assertNotNull(result);
        assertEquals(UsernamePasswordAuthenticationToken.class, result.getClass());
    }

    @Test
    @DisplayName("Registrar manager")
    public void register() {
        AccountRegisterForm managerForm = new AccountRegisterForm();
        managerForm.setEmail(account.getUsername());
        managerForm.setPassword(account.getPassword());
        account.setAccountId(UUID.randomUUID());

        AccountDto managerDto = new AccountDto(account);
        when(managerRepository.save(any())).thenReturn(account);
        AccountDto result = managerService.createAccount(managerForm);


        assertNotNull(result);
        assertEquals(managerDto.getAccountId(), result.getAccountId());
    }

    @Test
    @DisplayName("Atualizacao manager")
    public void updateManager() {
        UUID managerId = UUID.randomUUID();
        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        when(managerRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(managerRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = managerService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(updateForm.getEmail(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Exception quando atualizar e senha nao ser compativel com ja existente")
    public void updateManagerCase2() {
        UUID accountId = UUID.randomUUID();
        account.setAccountId(accountId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(accountId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("123456");
        updateForm.setNewPassword("newPassword");

        when(managerRepository.findById(accountId)).thenReturn(Optional.empty());
        when(managerRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = managerService.updateAccount(updateForm);


        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Atualizacao manager caso nao ache o id")
    public void updateManagerCase3() {
        UUID accountId = UUID.randomUUID();
        account.setAccountId(accountId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(accountId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        when(managerRepository.findById(accountId)).thenReturn(Optional.empty());
        when(managerRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = managerService.updateAccount(updateForm);


        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Atualizacao sem o campo ou vazio do username deve retornar o antigo")
    public void updateManagerCase4() {
        UUID accountId = UUID.randomUUID();
        account.setAccountId(accountId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(accountId);
        updateForm.setEmail("");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        when(managerRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(managerRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = managerService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(account.getUsername(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Deletar o gerente pelo id")
    public void deleteOrder() {
        UUID managerId = UUID.randomUUID();

        when(managerRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Boolean update = managerService.delete(managerId);

        assertTrue(update);
    }


    @Test
    @DisplayName("Caso ao delete nao encontre o gerente")
    public void deleteOrderCase2() {
        UUID managerId = UUID.randomUUID();

        when(managerRepository.findById(any())).thenReturn(Optional.empty());

        Boolean update = managerService.delete(managerId);

        assertFalse(update);
    }


}
