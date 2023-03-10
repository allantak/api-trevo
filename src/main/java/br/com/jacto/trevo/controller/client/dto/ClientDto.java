package br.com.jacto.trevo.controller.client.dto;

import br.com.jacto.trevo.model.client.Client;

import java.util.UUID;

public class ClientDto {

    public ClientDto(Client client) {
        this.clientId = client.getId();
        this.email = client.getEmail();
    }

    private final UUID clientId;

    private final String email;

    public UUID getClientId() {
        return clientId;
    }

    public String getEmail() {
        return email;
    }

}
