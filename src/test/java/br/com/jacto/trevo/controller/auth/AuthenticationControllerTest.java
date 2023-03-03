package br.com.jacto.trevo.controller.auth;

import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.auth.dto.ManagerCreateDto;
import br.com.jacto.trevo.controller.auth.dto.ManagerDto;
import br.com.jacto.trevo.controller.auth.dto.TokenDto;
import br.com.jacto.trevo.controller.auth.form.ManagerForm;
import br.com.jacto.trevo.controller.auth.form.ManagerUpdateForm;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.repository.ManagerRepository;
import br.com.jacto.trevo.service.manager.ManagerService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class AuthenticationControllerTest {

    @MockBean
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private ManagerRepository managerRepository;

    @MockBean
    private AuthenticationManager authenticationManager;


    @Autowired
    private JacksonTester<ManagerForm> managerFormJson;
    @Autowired
    private JacksonTester<TokenDto> tokenDtoJson;
    @Autowired
    private JacksonTester<ManagerDto> managerDtoJson;
    @Autowired
    private JacksonTester<ManagerUpdateForm> managerUpdateFormJson;
    @Autowired
    private JacksonTester<ManagerCreateDto> managerCreateDtoJson;





    public Manager manager = new Manager("test", "12345");

    @Test
    @DisplayName("Login com a conta de gerente")
    public void login() throws Exception {
        ManagerForm form = new ManagerForm();
        form.setUsername(manager.getUsername());
        form.setPassword(manager.getPassword());
        TokenDto token = new TokenDto(UUID.randomUUID(), "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0YW5kbzIiLCJpc3MiOiJBUEkgdHJldm8iLCJleHAiOjE2Nzc1MTQzNzR9.qJgioyfQPvUO0Dbo50JsMkF43wFRac-t1Dz9y-p6NSI");
        Authentication authenticationMock = mock(Authentication.class);

        when(managerService.auth(any())).thenReturn(authenticationMock);
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
        ManagerForm form = new ManagerForm();
        form.setUsername(manager.getUsername());
        form.setPassword(manager.getPassword());
        Authentication authenticationMock = mock(Authentication.class);

        when(managerService.auth(any())).thenReturn(authenticationMock);
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
        ManagerForm form = new ManagerForm();

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
        ManagerForm form = new ManagerForm();
        form.setUsername(manager.getUsername());
        form.setPassword(manager.getPassword());
        ManagerDto managerDto = new ManagerDto(manager);

        when(managerService.createManager(any())).thenReturn(managerDto);

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
        ManagerForm form = new ManagerForm();
        form.setUsername(manager.getUsername());
        form.setPassword(manager.getPassword());
        ManagerDto managerDto = new ManagerDto(manager);

        when(managerService.createManager(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

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
        ManagerForm form = new ManagerForm();
        form.setUsername(manager.getUsername());
        form.setPassword(manager.getPassword());
        ManagerDto managerDto = new ManagerDto(manager);

        when(managerService.createManager(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

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
        ManagerForm form = new ManagerForm();
        form.setUsername(manager.getUsername());
        form.setPassword(manager.getPassword());
        ManagerDto managerDto = new ManagerDto(manager);

        when(managerService.createManager(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

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
    public void updateManager() throws Exception{
        UUID managerId = UUID.randomUUID();
        ManagerUpdateForm updateForm = new ManagerUpdateForm();
        updateForm.setManagerId(managerId);
        updateForm.setUsername("newUsername");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        ManagerCreateDto managerDto = new ManagerCreateDto(manager);

        when(managerService.updateManager(any())).thenReturn(Optional.of(managerDto));

        var response = mockMvc.perform(
                        put("/managers")
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
    public void updateManagerCase2() throws Exception{
        UUID managerId = UUID.randomUUID();
        ManagerUpdateForm updateForm = new ManagerUpdateForm();
        updateForm.setManagerId(managerId);
        updateForm.setUsername("newUsername");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");


        when(managerService.updateManager(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        put("/managers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerUpdateFormJson.write(updateForm).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("atualizacao com em formato errado ou campo faltante")
    public void updateManagerCase3() throws Exception{
        ManagerUpdateForm updateForm = new ManagerUpdateForm();
        updateForm.setUsername("newUsername");

        ManagerCreateDto managerDto = new ManagerCreateDto(manager);

        when(managerService.updateManager(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        put("/managers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(managerUpdateFormJson.write(updateForm).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("atualizacao com em formato errado ou campo faltante")
    public void updateManagerCase4() throws Exception{
        UUID managerId = UUID.randomUUID();
        ManagerUpdateForm updateForm = new ManagerUpdateForm();
        updateForm.setManagerId(managerId);
        updateForm.setUsername("newUsername");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        ManagerCreateDto managerDto = new ManagerCreateDto(manager);

        when(managerService.updateManager(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        put("/managers")
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

        when(managerService.delete(orderId)).thenReturn(true);

        var response = mockMvc.perform(
                        delete("/managers/" + orderId)
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

        when(managerService.delete(orderId)).thenReturn(false);

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
                        delete("/managers/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }



}
