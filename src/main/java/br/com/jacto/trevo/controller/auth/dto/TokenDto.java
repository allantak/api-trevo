package br.com.jacto.trevo.controller.auth.dto;

public class TokenDto {
    public TokenDto(String token) {
        this.token = token;
    }

    private String token;

    public String getToken() {
        return token;
    }
}
