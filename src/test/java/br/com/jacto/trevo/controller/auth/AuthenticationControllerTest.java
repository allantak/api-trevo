package br.com.jacto.trevo.controller.auth;

import br.com.jacto.trevo.config.exception.ErrorHandler;
import br.com.jacto.trevo.config.security.SecurityFilter;
import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.auth.dto.AccountCreateDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDetailDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDto;
import br.com.jacto.trevo.controller.auth.dto.TokenDto;
import br.com.jacto.trevo.controller.auth.form.AccountRegisterForm;
import br.com.jacto.trevo.controller.auth.form.AccountUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.repository.AccountRepository;
import br.com.jacto.trevo.service.account.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthenticationControllerTest {

    @Mock
    private TestRestTemplate restTemplate;

    private MockMvc mockMvc;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AccountService accountService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthenticationManager authenticationManager;


    private JacksonTester<AccountRegisterForm> managerFormJson;

    private JacksonTester<TokenDto> tokenDtoJson;

    private JacksonTester<AccountDto> managerDtoJson;

    private JacksonTester<AccountUpdateForm> managerUpdateFormJson;

    private JacksonTester<AccountCreateDto> managerCreateDtoJson;

    private JacksonTester<List<AccountDto>> listJson;

    private JacksonTester<AccountDetailDto> accountDetailDtoJson;


    public Account account = new Account("test@gmail.com", "12345", "test", Account.Role.COLABORADOR);

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new ErrorHandler())
                .addFilters(new SecurityFilter())
                .build();
    }

    @Test
    @DisplayName("Listagem de usuario")
    public void getAll() throws Exception {
        account.setAccountId(UUID.randomUUID());
        List<AccountDto> listAccount = new ArrayList<AccountDto>();
        listAccount.add(new AccountDto(account));

        when(accountService.getAll()).thenReturn(listAccount);

        var response = mockMvc.perform(
                        get("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = listJson.write(listAccount).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Listagem de usuario nao permitida")
    public void getAllCase2() throws Exception {
        account.setAccountId(UUID.randomUUID());
        List<AccountDto> listAccount = new ArrayList<AccountDto>();
        listAccount.add(new AccountDto(account));

        when(accountService.getAll()).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        get("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }


    @Test
    @DisplayName("detalhes do usuario pelo Id")
    public void getId() throws Exception {
        UUID id = UUID.randomUUID();
        AccountDetailDto mockAccount = new AccountDetailDto(account);
        when(accountService.findAccount(any())).thenReturn(Optional.of(mockAccount));

        var response = mockMvc.perform(
                        get("/accounts/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.OK.value(), response.getStatus());
        var jsonExpect = accountDetailDtoJson.write(mockAccount).getJson();
        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("Nao achar usuario pelo ID")
    public void getIdCase2() throws Exception {
        UUID id = UUID.randomUUID();
        when(accountService.findAccount(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        get("/accounts/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    @Test
    @DisplayName("Nao autenticado para procurar pelo ID")
    public void getIdCase3() throws Exception {
        UUID id = UUID.randomUUID();
        when(accountService.findAccount(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        get("/accounts/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("Login com a conta de gerente")
    public void login() throws Exception {
        AccountRegisterForm form = new AccountRegisterForm();
        form.setEmail(account.getUsername());
        form.setPassword(account.getPassword());
        TokenDto token = new TokenDto(UUID.randomUUID(), "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0YW5kbzIiLCJpc3MiOiJBUEkgdHJldm8iLCJleHAiOjE2Nzc1MTQzNzR9.qJgioyfQPvUO0Dbo50JsMkF43wFRac-t1Dz9y-p6NSI");
        Authentication authenticationMock = mock(Authentication.class);

        when(accountService.auth(any())).thenReturn(authenticationMock);
        when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
        when(tokenService.token(any())).thenReturn(token);

        var response = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.OK.value(), response.getStatus());
        var jsonExpect = tokenDtoJson.write(token).getJson();
        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("Nao encontre gerente cadastrado")
    public void loginCase2() throws Exception {
        AccountRegisterForm form = new AccountRegisterForm();
        form.setEmail(account.getUsername());
        form.setPassword(account.getPassword());
        Authentication authenticationMock = mock(Authentication.class);

        when(accountService.auth(any())).thenReturn(authenticationMock);
        when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
        when(tokenService.token(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

    }

    @Test
    @DisplayName("Nao encontre gerente cadastrado")
    public void loginCase3() throws Exception {
        AccountRegisterForm form = new AccountRegisterForm();

        var response = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Login com a conta de gerente")
    public void register() throws Exception {
        AccountRegisterForm form = new AccountRegisterForm();
        form.setEmail(account.getUsername());
        form.setPassword(account.getPassword());
        form.setAccountName(account.getAccountName());
        form.setAccountRole(account.getAccountRole());
        AccountDto managerDto = new AccountDto(account);

        when(accountService.createAccount(any())).thenReturn(managerDto);

        var response = mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        var jsonExpect = managerDtoJson.write(managerDto).getJson();
        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("Registar com username ja registrado")
    public void registerCase2() throws Exception {
        AccountRegisterForm form = new AccountRegisterForm();
        form.setEmail(account.getUsername());
        form.setPassword(account.getPassword());
        form.setAccountName(account.getAccountName());
        form.setAccountRole(account.getAccountRole());

        when(accountService.createAccount(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        var response = mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    @DisplayName("Registar sem permissao")
    public void registerCase3() throws Exception {
        AccountRegisterForm form = new AccountRegisterForm();
        form.setEmail(account.getUsername());
        form.setPassword(account.getPassword());
        form.setAccountName(account.getAccountName());
        form.setAccountRole(account.getAccountRole());

        when(accountService.createAccount(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("Registar de forma incorreta")
    public void registerCase4() throws Exception {
        AccountRegisterForm form = new AccountRegisterForm();
        form.setEmail(account.getUsername());
        form.setPassword(account.getPassword());

        when(accountService.createAccount(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        var response = mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();


        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("fazer atualizcao")
    public void updateManager() throws Exception {
        UUID managerId = UUID.randomUUID();
        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newUsername");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        AccountCreateDto managerDto = new AccountCreateDto(account);

        when(accountService.updateAccount(any())).thenReturn(Optional.of(managerDto));

        var response = mockMvc.perform(
                        put("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerUpdateFormJson.write(updateForm).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = managerCreateDtoJson.write(managerDto).getJson();
        assertEquals(jsonExpect, response.getContentAsString());
    }


    @Test
    @DisplayName("atualizacao com id incorreto do gerente ou senha incorreta")
    public void updateManagerCase2() throws Exception {
        UUID managerId = UUID.randomUUID();
        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newUsername");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");


        when(accountService.updateAccount(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        put("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerUpdateFormJson.write(updateForm).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("atualizacao com em formato errado ou campo faltante")
    public void updateManagerCase3() throws Exception {
        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setEmail("newUsername");

        when(accountService.updateAccount(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        put("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerUpdateFormJson.write(updateForm).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("atualizacao com em formato errado ou campo faltante")
    public void updateManagerCase4() throws Exception {
        UUID managerId = UUID.randomUUID();
        AccountUpdateForm updateForm = new AccountUpdateForm();
        updateForm.setAccountId(managerId);
        updateForm.setEmail("newUsername");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        when(accountService.updateAccount(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        put("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerUpdateFormJson.write(updateForm).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("Deletar gerente")
    public void deleteManager() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(accountService.delete(orderId)).thenReturn(true);

        var response = mockMvc.perform(
                        delete("/accounts/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    @DisplayName("Gerente nao cadastrado para deletar retorne not found")
    public void deleteManagerCase2() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(accountService.delete(orderId)).thenReturn(false);

        var response = mockMvc.perform(
                        delete("/orders/" + orderId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("id do gerente em formato incorreto ao deletar")
    public void deleteManagerCase3() throws Exception {
        var response = mockMvc.perform(
                        delete("/accounts/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


}
