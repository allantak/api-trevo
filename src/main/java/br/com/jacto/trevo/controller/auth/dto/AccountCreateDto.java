package br.com.jacto.trevo.controller.auth.dto;

import br.com.jacto.trevo.model.account.Account;

import java.util.UUID;

public class AccountCreateDto {

    public AccountCreateDto(Account account) {
        this.accountId = account.getAccountId();
        this.email = account.getUsername();
    }

    private UUID accountId;
    private String email;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
