package br.com.jacto.trevo.service.manager;

import br.com.jacto.trevo.controller.auth.dto.ManagerCreateDto;
import br.com.jacto.trevo.controller.auth.dto.ManagerDto;
import br.com.jacto.trevo.controller.auth.form.ManagerForm;
import br.com.jacto.trevo.controller.auth.form.ManagerUpdateForm;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.repository.ManagerRepository;
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
    private ManagerService managerService;

    @MockBean
    private ManagerRepository managerRepository;


    public Manager manager = new Manager("test", "12345");


    @Test
    @DisplayName("Procurar pelo gerente existente")
    public void findByUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(managerRepository.findByUsername(any())).thenReturn(userDetails);

        UserDetails result = managerService.loadUserByUsername("teste");


        assertNotNull(result);
        assertEquals(userDetails, result);
    }


    @Test
    @DisplayName("Autenticar gerente")
    public void auth() {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setUsername(manager.getUsername());
        managerForm.setPassword(manager.getPassword());

        Authentication result = managerService.auth(managerForm);


        assertNotNull(result);
        assertEquals(UsernamePasswordAuthenticationToken.class, result.getClass());
    }

    @Test
    @DisplayName("Registrar manager")
    public void register() {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setUsername(manager.getUsername());
        managerForm.setPassword(manager.getPassword());
        manager.setManagerId(UUID.randomUUID());

        ManagerDto managerDto = new ManagerDto(manager);
        when(managerRepository.save(any())).thenReturn(manager);
        ManagerDto result = managerService.createManager(managerForm);


        assertNotNull(result);
        assertEquals(managerDto.getManagerId(), result.getManagerId());
    }

    @Test
    @DisplayName("Atualizacao manager")
    public void updateManager() {
        UUID managerId = UUID.randomUUID();
        manager.setManagerId(managerId);
        String encode = new BCryptPasswordEncoder().encode(manager.getPassword());
        manager.setManagerPassword(encode);

        ManagerUpdateForm updateForm = new ManagerUpdateForm();
        updateForm.setManagerId(managerId);
        updateForm.setUsername("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(managerRepository.save(any())).thenReturn(manager);

        Optional<ManagerCreateDto> result = managerService.updateManager(updateForm);


        assertTrue(result.isPresent());
        assertEquals(updateForm.getUsername(), result.get().getUsername());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), manager.getPassword()));
    }

    @Test
    @DisplayName("Exception quando atualizar e senha nao ser compativel com ja existente")
    public void updateManagerCase2() {
        UUID managerId = UUID.randomUUID();
        manager.setManagerId(managerId);
        String encode = new BCryptPasswordEncoder().encode(manager.getPassword());
        manager.setManagerPassword(encode);

        ManagerUpdateForm updateForm = new ManagerUpdateForm();
        updateForm.setManagerId(managerId);
        updateForm.setUsername("newTest");
        updateForm.setPassword("123456");
        updateForm.setNewPassword("newPassword");

        when(managerRepository.findById(managerId)).thenReturn(Optional.empty());
        when(managerRepository.save(any())).thenReturn(manager);

        Optional<ManagerCreateDto> result = managerService.updateManager(updateForm);


        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Atualizacao manager caso nao ache o id")
    public void updateManagerCase3() {
        UUID managerId = UUID.randomUUID();
        manager.setManagerId(managerId);
        String encode = new BCryptPasswordEncoder().encode(manager.getPassword());
        manager.setManagerPassword(encode);

        ManagerUpdateForm updateForm = new ManagerUpdateForm();
        updateForm.setManagerId(managerId);
        updateForm.setUsername("newTest");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        when(managerRepository.findById(managerId)).thenReturn(Optional.empty());
        when(managerRepository.save(any())).thenReturn(manager);

        Optional<ManagerCreateDto> result = managerService.updateManager(updateForm);


        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Atualizacao sem o campo ou vazio do username deve retornar o antigo")
    public void updateManagerCase4() {
        UUID managerId = UUID.randomUUID();
        manager.setManagerId(managerId);
        String encode = new BCryptPasswordEncoder().encode(manager.getPassword());
        manager.setManagerPassword(encode);

        ManagerUpdateForm updateForm = new ManagerUpdateForm();
        updateForm.setManagerId(managerId);
        updateForm.setUsername("");
        updateForm.setPassword("12345");
        updateForm.setNewPassword("newPassword");

        when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(managerRepository.save(any())).thenReturn(manager);

        Optional<ManagerCreateDto> result = managerService.updateManager(updateForm);


        assertTrue(result.isPresent());
        assertEquals(manager.getUsername(), result.get().getUsername());

        assertTrue(new BCryptPasswordEncoder().matches(updateForm.getNewPassword(), manager.getPassword()));
    }

    @Test
    @DisplayName("Deletar o gerente pelo id")
    public void deleteOrder() {
        UUID managerId = UUID.randomUUID();

        when(managerRepository.findById(any())).thenReturn(Optional.ofNullable(manager));

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
