package br.com.jacto.trevo.controller.auth.dto;

import java.util.UUID;

public class TokenDto {
    public TokenDto(UUID managerId, String token) {
        this.managerId = managerId;
        this.token = token;
    }

    private final UUID managerId;
    private final String token;

    public String getToken() {
        return token;
    }

    public UUID getManagerId() {
        return managerId;
    }
}
