package br.com.jacto.trevo.controller.auth.dto;

import br.com.jacto.trevo.model.account.Account;

import java.util.UUID;

public class AccountDto {
    public AccountDto(Account account) {
        this.accountId = account.getAccountId();
        this.email = account.getUsername();
    }

    private final UUID accountId;

    private final String email;

    public UUID getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }
}
