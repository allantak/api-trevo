package br.com.jacto.trevo.service.manager;

import br.com.jacto.trevo.controller.auth.dto.ManagerCreateDto;
import br.com.jacto.trevo.controller.auth.dto.ManagerDto;
import br.com.jacto.trevo.controller.auth.form.ManagerForm;
import br.com.jacto.trevo.controller.auth.form.ManagerUpdateForm;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManagerService implements UserDetailsService {
    @Autowired
    ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return managerRepository.findByUsername(username);
    }

    public Authentication auth(ManagerForm user) {
        return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
    }

    public ManagerDto createManager(ManagerForm manager) {
        String encoder = new BCryptPasswordEncoder().encode(manager.getPassword());
        Manager save = new Manager(manager.getUsername(), encoder);
        Manager convert = managerRepository.save(save);
        return new ManagerDto(convert);
    }

    public Optional<ManagerCreateDto> updateManager(ManagerUpdateForm manager) {
        Optional<Manager> findManager = managerRepository.findById(manager.getManagerId());

        if (findManager.isEmpty()) {
            return Optional.empty();
        }

        boolean verify = new BCryptPasswordEncoder().matches(manager.getPassword(), findManager.get().getPassword());
        if (!verify) {
            return Optional.empty();
        }
        if (manager.getUsername() != null && !manager.getUsername().trim().isEmpty()) {
            findManager.get().setUsername(manager.getUsername());
        }

        String encoder = new BCryptPasswordEncoder().encode(manager.getNewPassword());
        findManager.get().setManagerPassword(encoder);
        Manager save = managerRepository.save(findManager.get());
        return Optional.of(new ManagerCreateDto(save));
    }

    public Boolean delete(UUID id) {
        Optional<Manager> findManager = managerRepository.findById(id);
        if (findManager.isEmpty()) {
            return false;
        }
        managerRepository.deleteById(id);
        return true;
    }

}
