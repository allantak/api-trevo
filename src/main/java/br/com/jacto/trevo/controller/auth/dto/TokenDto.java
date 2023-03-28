package br.com.jacto.trevo.controller.auth.dto;

import java.util.UUID;

public class TokenDto {
    public TokenDto(UUID accountId, String token) {
        this.accountId = accountId;
        this.token = token;
    }

    private final UUID accountId;
    private final String token;

    public String getToken() {
        return token;
    }

    public UUID getAccountId() {
        return accountId;
    }
}
