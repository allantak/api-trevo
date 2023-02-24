package br.com.jacto.trevo.repository;

import br.com.jacto.trevo.model.manager.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface ManagerRepository extends JpaRepository<Manager, UUID> {
    UserDetails findByUsername(String username);
}
