package br.com.jacto.trevo.controller.auth.dto;

import br.com.jacto.trevo.model.account.Account;

import java.time.LocalDateTime;

public class AccountDetailDto {
    public AccountDetailDto(Account account){
        this.email = account.getEmail();
        this.name = account.getAccountName();
        this.create_at = account.getCreateAt();
        this.role = account.getAccountRole();
    }

    private final String email;
    private final String name;
    private final LocalDateTime create_at;
    private final Account.Role role;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public Account.Role getRole() {
        return role;
    }
}
