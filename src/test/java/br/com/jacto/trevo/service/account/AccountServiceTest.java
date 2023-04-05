package br.com.jacto.trevo.service.account;

import br.com.jacto.trevo.controller.auth.dto.AccountCreateDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDetailDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDto;
import br.com.jacto.trevo.controller.auth.form.AccountLoginForm;
import br.com.jacto.trevo.controller.auth.form.AccountRegisterForm;
import br.com.jacto.trevo.controller.auth.form.AccountUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SecurityContext securityContext;


    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);

    @Test()
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
    public void getAll() {
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
    public void getId() {
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
    public void getIdCase2() {
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
    @DisplayName("Registrar usuario com nivel admin cadastrando outro admin")
    public void register() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.ADMINISTRADOR);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);
        AccountDto result = accountService.createAccount(accountRegisterForm);


        assertEquals(result.getEmail(), account.getEmail());
        assertEquals(result.getAccountId(), account.getAccountId());
    }

    @Test
    @DisplayName("Registrar admin sendo colaborador")
    public void registerCase2() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.ADMINISTRADOR);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);

        assertThrows(AccessDeniedException.class, () -> accountService.createAccount(accountRegisterForm));
    }

    @Test
    @DisplayName("Registrar admin sendo cliente")
    public void registerCase3() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.ADMINISTRADOR);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);

        assertThrows(AccessDeniedException.class, () -> accountService.createAccount(accountRegisterForm));
    }

    @Test
    @DisplayName("Registrar Colaborador sendo admin")
    public void registerCase4() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.COLABORADOR);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);
        AccountDto result = accountService.createAccount(accountRegisterForm);


        assertEquals(result.getEmail(), account.getEmail());
        assertEquals(result.getAccountId(), account.getAccountId());
    }

    @Test
    @DisplayName("Registrar Colaborador sendo colaborador")
    public void registerCase5() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.COLABORADOR);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);
        AccountDto result = accountService.createAccount(accountRegisterForm);


        assertEquals(result.getEmail(), account.getEmail());
        assertEquals(result.getAccountId(), account.getAccountId());
    }

    @Test
    @DisplayName("Registrar Colaborador sendo cliente")
    public void registerCase6() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.COLABORADOR);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);

        assertThrows(AccessDeniedException.class, () -> accountService.createAccount(accountRegisterForm));
    }

    @Test
    @DisplayName("Registrar Cliente sendo admin")
    public void registerCase7() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.CLIENTE);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);
        AccountDto result = accountService.createAccount(accountRegisterForm);


        assertEquals(result.getEmail(), account.getEmail());
        assertEquals(result.getAccountId(), account.getAccountId());
    }

    @Test
    @DisplayName("Registrar Cliente sendo colaborador")
    public void registerCase8() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.CLIENTE);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);
        AccountDto result = accountService.createAccount(accountRegisterForm);


        assertEquals(result.getEmail(), account.getEmail());
        assertEquals(result.getAccountId(), account.getAccountId());
    }

    @Test
    @DisplayName("Registrar Cliente sendo cliente")
    public void registerCase9() throws AccessDeniedException {
        AccountRegisterForm accountRegisterForm = new AccountRegisterForm();
        accountRegisterForm.setAccountName(account.getAccountName());
        accountRegisterForm.setEmail(account.getUsername());
        accountRegisterForm.setPassword(account.getPassword());
        accountRegisterForm.setAccountRole(Account.Role.CLIENTE);
        account.setAccountId(UUID.randomUUID());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.save(any())).thenReturn(account);
        AccountDto result = accountService.createAccount(accountRegisterForm);


        assertEquals(result.getEmail(), account.getEmail());
        assertEquals(result.getAccountId(), account.getAccountId());
    }

    @Test
    @DisplayName("Atualizacao manager")
    public void updateManager() throws AccessDeniedException {
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
    public void updateManagerCase2() throws AccessDeniedException {
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
    public void updateManagerCase3() throws AccessDeniedException {
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
    public void updateManagerCase4() throws AccessDeniedException {
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
    @DisplayName("Atualizacao para admin sendo admin")
    public void updateManagerCase5() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.ADMINISTRADOR);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(updateForm.getEmail(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Atualizacao para admin sendo colaborador")
    public void updateManagerCase6() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.ADMINISTRADOR);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);


        assertThrows(AccessDeniedException.class, () -> accountService.updateAccount(updateForm));

    }

    @Test
    @DisplayName("Atualizacao para admin sendo cliente")
    public void updateManagerCase7() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.ADMINISTRADOR);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);


        assertThrows(AccessDeniedException.class, () -> accountService.updateAccount(updateForm));

    }

    @Test
    @DisplayName("Atualizacao para colaborador sendo admin")
    public void updateManagerCase8() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.COLABORADOR);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(updateForm.getEmail(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Atualizacao para colaborador sendo colaborador")
    public void updateManagerCase9() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.COLABORADOR);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(updateForm.getEmail(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Atualizacao para colaborador sendo cliente")
    public void updateManagerCase10() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.COLABORADOR);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);


        assertThrows(AccessDeniedException.class, () -> accountService.updateAccount(updateForm));
    }

    @Test
    @DisplayName("Atualizacao para cliente sendo admin")
    public void updateManagerCase11() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.CLIENTE);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(updateForm.getEmail(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Atualizacao para cliente sendo colaborador")
    public void updateManagerCase12() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.CLIENTE);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(updateForm.getEmail(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Atualizacao para cliente sendo cliente")
    public void updateManagerCase13() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);


        account.setAccountId(managerId);
        String encode = new BCryptPasswordEncoder().encode(account.getPassword());
        account.setAccountPassword(encode);

        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");
        updateForm.setAccountRole(Account.Role.CLIENTE);

        when(accountRepository.findById(managerId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Optional<AccountCreateDto> result = accountService.updateAccount(updateForm);


        assertTrue(result.isPresent());
        assertEquals(updateForm.getEmail(), result.get().getEmail());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), account.getPassword()));
    }

    @Test
    @DisplayName("Deletar admin sendo admin")
    public void delete() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Boolean update = accountService.delete(managerId);

        assertTrue(update);
    }

    @Test
    @DisplayName("Deletar admin sendo colaborador")
    public void deleteCase2() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        account.setAccountRole(Account.Role.ADMINISTRADOR);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        assertThrows(AccessDeniedException.class, () -> accountService.delete(managerId));
    }

    @Test
    @DisplayName("Deletar admin sendo cliente")
    public void deleteCase3() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        account.setAccountRole(Account.Role.ADMINISTRADOR);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        assertThrows(AccessDeniedException.class, () -> accountService.delete(managerId));
    }

    @Test
    @DisplayName("Deletar Colaborador sendo Admin")
    public void deleteCase4() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        account.setAccountRole(Account.Role.COLABORADOR);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Boolean update = accountService.delete(managerId);

        assertTrue(update);
    }

    @Test
    @DisplayName("Deletar colaborador sendo colaborador")
    public void deleteCase5() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        account.setAccountRole(Account.Role.COLABORADOR);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Boolean update = accountService.delete(managerId);

        assertTrue(update);
    }

    @Test
    @DisplayName("Deletar colaborador sendo cliente")
    public void deleteCase6() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        account.setAccountRole(Account.Role.COLABORADOR);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        assertThrows(AccessDeniedException.class, () -> accountService.delete(managerId));
    }

    @Test
    @DisplayName("Deletar Cliente sendo Admin")
    public void deleteCase7() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        account.setAccountRole(Account.Role.CLIENTE);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Boolean update = accountService.delete(managerId);

        assertTrue(update);
    }

    @Test
    @DisplayName("Deletar Cliente sendo colaborador")
    public void deleteCase8() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        account.setAccountRole(Account.Role.CLIENTE);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Boolean update = accountService.delete(managerId);

        assertTrue(update);
    }

    @Test
    @DisplayName("Deletar Cliente sendo cliente")
    public void deleteCase9() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();
        account.setAccountRole(Account.Role.CLIENTE);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findById(any())).thenReturn(Optional.ofNullable(account));

        Boolean update = accountService.delete(managerId);

        assertTrue(update);
    }

    @Test
    @DisplayName("Caso ao delete nao encontre o gerente")
    public void deleteCase10() throws AccessDeniedException {
        UUID managerId = UUID.randomUUID();

        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        Boolean update = accountService.delete(managerId);

        assertFalse(update);
    }


}
