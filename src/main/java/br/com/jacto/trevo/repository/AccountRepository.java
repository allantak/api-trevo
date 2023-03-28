package br.com.jacto.trevo.repository;

import br.com.jacto.trevo.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    UserDetails findByEmail(String email);
}
