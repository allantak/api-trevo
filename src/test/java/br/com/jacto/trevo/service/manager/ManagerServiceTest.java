package br.com.jacto.trevo.service.manager;

import br.com.jacto.trevo.controller.auth.dto.ManagerDto;
import br.com.jacto.trevo.controller.auth.form.ManagerForm;
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
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    public void findByUsername(){
        UserDetails userDetails = mock(UserDetails.class);
        when(managerRepository.findByUsername(any())).thenReturn(userDetails);

        UserDetails result = managerService.loadUserByUsername("teste");


        assertNotNull(result);
        assertEquals(userDetails, result);
    }


    @Test
    @DisplayName("Autenticar gerente")
    public void auth(){
        ManagerForm managerForm  = new ManagerForm();
        managerForm.setUsername(manager.getUsername());
        managerForm.setPassword(manager.getPassword());

        Authentication result = managerService.auth(managerForm);


        assertNotNull(result);
        assertEquals(UsernamePasswordAuthenticationToken.class, result.getClass());
    }

    @Test
    @DisplayName("Registrar manager")
    public void register(){
        ManagerForm managerForm  = new ManagerForm();
        managerForm.setUsername(manager.getUsername());
        managerForm.setPassword(manager.getPassword());
        manager.setManagerId(UUID.randomUUID());

        ManagerDto managerDto = new ManagerDto(manager);
        when(managerRepository.save(any())).thenReturn(manager);
        ManagerDto result = managerService.createManager(managerForm);


        assertNotNull(result);
        assertEquals(managerDto.getManagerId(), result.getManagerId());
    }

}
