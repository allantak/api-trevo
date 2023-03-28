package br.com.jacto.trevo.service.manager;

import br.com.jacto.trevo.controller.auth.dto.AccountCreateDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDto;
import br.com.jacto.trevo.controller.auth.form.AccountForm;
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

    public Authentication auth(AccountForm user) {
        return new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
    }

    public AccountDto createAccount(AccountForm user) {
        String encoder = new BCryptPasswordEncoder().encode(user.getPassword());
        Account save = new Account(user.getEmail(), encoder);
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

        String encoder = new BCryptPasswordEncoder().encode(user.getNewPassword());
        findAccount.get().setAccountPassword(encoder);
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
