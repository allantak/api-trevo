package br.com.jacto.trevo.service.account;

import br.com.jacto.trevo.controller.auth.dto.AccountCreateDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDetailDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDto;
import br.com.jacto.trevo.controller.auth.form.AccountLoginForm;
import br.com.jacto.trevo.controller.auth.form.AccountRegisterForm;
import br.com.jacto.trevo.controller.auth.form.AccountUpdateForm;
import br.com.jacto.trevo.model.account.Account;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;


    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);

    @Test
    @DisplayName("Procurar pelo gerente existente")
    public void findByUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(accountRepository.findByEmail(any())).thenReturn(userDetails);

        UserDetails result = accountService.loadUserByUsername("teste");


        assertNotNull(result);
        assertEquals(userDetails, result);
    }

    @Test
    @DisplayName("Listagem de usuarios registrados")
    public void getAll(){
        account.setAccountId(UUID.randomUUID());
        List<Account> listAccount = new ArrayList<Account>();
        listAccount.add(account);

        when(accountRepository.findAll()).thenReturn(listAccount);

        List<AccountDto> result = accountService.getAll();

        assertNotNull(listAccount);
        assertEquals(account.getAccountId(), result.get(0).getAccountId());
        assertEquals(account.getEmail(), result.get(0).getEmail());
    }

    @Test
    @DisplayName("Achar pelo ID do usuario")
    public void getId(){
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));
        Optional<AccountDetailDto> result = accountService.findAccount(id);

        assertNotNull(result);
        assertEquals(account.getEmail(), result.get().getEmail());
        assertEquals(account.getAccountName(), result.get().getName());
        assertEquals(account.getCreateAt(), result.get().getCreate_at());
        assertEquals(account.getAccountRole(), result.get().getRole());
    }

    @Test
    @DisplayName("Achar pelo ID do usuario")
    public void getIdCase2(){
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        Optional<AccountDetailDto> result = accountService.findAccount(id);

        assertEquals(Optional.empty(), result);
    }



    @Test
    @DisplayName("Autenticar gerente")
    public void auth() {
        AccountLoginForm managerForm = new AccountLoginForm();
        managerForm.setEmail(account.getUsername());
        managerForm.setPassword(account.getPassword());

        Authentication result = accountService.auth(managerForm);


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
        when(accountRepository.save(any())).thenReturn(account);
        AccountDto result = accountService.createAccount(managerForm);


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

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


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

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


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

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


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

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(account.getUsername(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Deletar o gerente pelo id")
    public void deleteOrder() {
        UUID managerId = UUID.randomUUID();

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Boolean update = accountService.delete(managerId);

        assertTrue(update);
    }


    @Test
    @DisplayName("Caso ao delete nao encontre o gerente")
    public void deleteOrderCase2() {
        UUID managerId = UUID.randomUUID();

        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        Boolean update = accountService.delete(managerId);

        assertFalse(update);
    }


}
