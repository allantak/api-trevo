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
        ManagerUpdateForm managerForm = new ManagerUpdateForm();
        managerForm.setManagerId(managerId);
        managerForm.setUsername(manager.getUsername());
        managerForm.setPassword(manager.getPassword());
        managerForm.setNewPassword("12345");


        when(managerRepository.findById(any())).thenReturn(Optional.of(manager));
        when(managerRepository.save(any())).thenReturn(manager);
        Optional<ManagerCreateDto> result = managerService.updateManager(managerForm);


        assertNotNull(result);
        assertEquals(managerForm.getManagerId(), result.get().getManagerId());
        assertEquals(managerForm.getUsername(), result.get().getUsername());
    }

    @Test
    @DisplayName("Exception quando atualizar e senha nao ser compativel com ja existente")
    public void updateManagerCase2() {
        UUID managerId = UUID.randomUUID();
        manager.setManagerId(managerId);
        String encode = new BCryptPasswordEncoder().encode(manager.getPassword());
        String encodeTest = new BCryptPasswordEncoder().encode("Comparacao");
        manager.setManagerPassword(encode);

        ManagerUpdateForm managerForm = new ManagerUpdateForm();
        managerForm.setManagerId(managerId);
        managerForm.setUsername(manager.getUsername());
        managerForm.setPassword(encodeTest);
        managerForm.setNewPassword("12345");


        when(managerRepository.findById(any())).thenReturn(Optional.of(manager));
        when(managerRepository.save(any())).thenReturn(manager);
        Optional<ManagerCreateDto> result = managerService.updateManager(managerForm);


        assertNotNull(result);
        assertEquals(managerForm.getManagerId(), result.get().getManagerId());
    }

    @Test
    @DisplayName("Atualizacao manager caso nao ache o id")
    public void updateManagerCase3() {
        UUID managerId = UUID.randomUUID();
        manager.setManagerId(managerId);
        ManagerUpdateForm managerForm = new ManagerUpdateForm();
        managerForm.setManagerId(managerId);
        managerForm.setUsername(manager.getUsername());
        managerForm.setPassword(manager.getPassword());


        when(managerRepository.findById(any())).thenReturn(Optional.empty());
        when(managerRepository.save(any())).thenReturn(manager);
        Optional<ManagerCreateDto> result = managerService.updateManager(managerForm);


        assertNotNull(result);
        assertEquals(Optional.empty(), result);
    }

}
