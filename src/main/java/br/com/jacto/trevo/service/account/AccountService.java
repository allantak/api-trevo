package br.com.jacto.trevo.service.account;

import br.com.jacto.trevo.controller.auth.dto.AccountCreateDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDetailDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDto;
import br.com.jacto.trevo.controller.auth.form.AccountLoginForm;
import br.com.jacto.trevo.controller.auth.form.AccountRegisterForm;
import br.com.jacto.trevo.controller.auth.form.AccountUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByEmail(username);
    }

    public List<AccountDto> getAll() {
        List<AccountDto> test = accountRepository.findAll().stream().map(AccountDto::new).toList();
        System.out.println(test.size());
        System.out.println(test.get(0).getAccountId());
        return test;
    }

    public Optional<AccountDetailDto> findAccount(UUID userId) {
        return accountRepository.findById(userId).map(AccountDetailDto::new);
    }

    public Authentication auth(AccountLoginForm user) {
        return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
    }

    public AccountDto createAccount(AccountRegisterForm user) {
        String encoder = new BCryptPasswordEncoder().encode(user.getPassword());
        Account save = new Account(user.getEmail(), encoder, user.getAccountName(), user.getAccountRole());
        save.setCreateAt(LocalDateTime.now());
        Account convert = accountRepository.save(save);
        return new AccountDto(convert);
    }

    public Optional<AccountCreateDto> updateAccount(AccountUpdateForm user) {
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
            findAccount.get().setAccountRole(user.getAccountRole());
        }

        if(user.getNewPassword() != null && !user.getNewPassword().trim().isEmpty()){
            String encoder = new BCryptPasswordEncoder().encode(user.getNewPassword());
            findAccount.get().setAccountPassword(encoder);
        }

        Account save = accountRepository.save(findAccount.get());
        return Optional.of(new AccountCreateDto(save));
    }

    public Boolean delete(UUID id) {
        Optional<Account> findAccount = accountRepository.findById(id);
        if (findAccount.isEmpty()) {
            return false;
        }
        accountRepository.deleteById(id);
        return true;
    }

}
