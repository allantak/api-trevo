package br.com.jacto.trevo.service.account;

import br.com.jacto.trevo.controller.auth.dto.AccountCreateDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDetailDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDto;
import br.com.jacto.trevo.controller.auth.dto.AccountOrderDto;
import br.com.jacto.trevo.controller.auth.form.AccountLoginForm;
import br.com.jacto.trevo.controller.auth.form.AccountRegisterForm;
import br.com.jacto.trevo.controller.auth.form.AccountUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByEmail(username);
    }

    public List<AccountDto> getAll() {
        return accountRepository.findAll().stream().map(AccountDto::new).collect(Collectors.toList());
    }

    public Optional<AccountDetailDto> findAccount(UUID userId) {
        return accountRepository.findById(userId).map(AccountDetailDto::new);
    }

    public Optional<AccountOrderDto> findAccountOrder(UUID userId) {
        return accountRepository.findById(userId).map(AccountOrderDto::new);
    }

    public Authentication auth(AccountLoginForm user) {
        return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
    }

    public AccountDto createAccount(AccountRegisterForm user) throws AccessDeniedException {
        String encoder = new BCryptPasswordEncoder().encode(user.getPassword());
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        if (Objects.equals(user.getAccountRole().toString(), "ADMINISTRADOR") && !Objects.equals(role, "[ROLE_ADMINISTRADOR]")) {
            throw new AccessDeniedException("Acesso negado. Somente ADMINISTRADOR pode atualizar ADMINISTRADOR");
        }

        if (Objects.equals(user.getAccountRole().toString(), "COLABORADOR") && (Objects.equals(role, "[ROLE_CLIENTE]") | Objects.equals(role, "[ROLE_ANONYMOUS]"))) {
            throw new AccessDeniedException("Acesso negado. Somente ADMINISTRADOR ou COLABORADOR pode atualizar COLABORADOR");
        }
        Account save = new Account(user.getEmail(), encoder, user.getAccountName(), user.getAccountRole());
        save.setCreateAt(LocalDateTime.now());
        Account convert = accountRepository.save(save);
        return new AccountDto(convert);
    }

    public Optional<AccountCreateDto> updateAccount(AccountUpdateForm user) throws AccessDeniedException {
        Optional<Account> findAccount = accountRepository.findById(user.getAccountId());

        if (findAccount.isEmpty()) {
            return Optional.empty();
        }

        boolean verify = new BCryptPasswordEncoder().matches(user.getPassword(), findAccount.get().getPassword());
        if (!verify) {
            return Optional.empty();
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            findAccount.get().setEmail(user.getEmail());
        }

        if (user.getAccountName() != null && !user.getAccountName().trim().isEmpty()) {
            findAccount.get().setAccountName(user.getAccountName());
        }

        if (user.getAccountRole() != null && !user.getAccountRole().toString().trim().isEmpty()) {
            String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

            if (Objects.equals(user.getAccountRole().toString(), "ADMINISTRADOR") && !Objects.equals(role, "[ROLE_ADMINISTRADOR]")) {
                throw new AccessDeniedException("Acesso negado. Somente ADMINISTRADOR pode cadastrar ADMINISTRADOR");
            }

            if (Objects.equals(user.getAccountRole().toString(), "COLABORADOR") && (Objects.equals(role, "[ROLE_CLIENTE]") | Objects.equals(role, "[ROLE_ANONYMOUS]"))) {
                throw new AccessDeniedException("Acesso negado. Somente ADMINISTRADOR ou COLABORADOR pode cadastrar COLABORADOR");
            }
            findAccount.get().setAccountRole(user.getAccountRole());
        }

        if (user.getNewPassword() != null && !user.getNewPassword().trim().isEmpty()) {
            String encoder = new BCryptPasswordEncoder().encode(user.getNewPassword());
            findAccount.get().setAccountPassword(encoder);
        }

        Account save = accountRepository.save(findAccount.get());
        return Optional.of(new AccountCreateDto(save));
    }

    public Boolean delete(UUID id) throws AccessDeniedException {
        Optional<Account> findAccount = accountRepository.findById(id);
        if (findAccount.isEmpty()) {
            return false;
        }

        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        if (Objects.equals(findAccount.get().getAccountRole().toString(), "ADMINISTRADOR") && !Objects.equals(role, "[ROLE_ADMINISTRADOR]")) {
            throw new AccessDeniedException("Acesso negado. Somente ADMINISTRADOR pode deletar ADMINISTRADOR");
        }

        if (Objects.equals(findAccount.get().getAccountRole().toString(), "COLABORADOR") && (Objects.equals(role, "[ROLE_CLIENTE]") | Objects.equals(role, "[ROLE_ANONYMOUS]"))) {
            throw new AccessDeniedException("Acesso negado. Somente ADMINISTRADOR ou COLABORADOR pode deletar COLABORADOR");
        }

        accountRepository.deleteById(id);
        return true;
    }

}
