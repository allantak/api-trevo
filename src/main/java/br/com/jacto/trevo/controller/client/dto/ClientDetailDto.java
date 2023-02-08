package br.com.jacto.trevo.controller.client.dto;

import br.com.jacto.trevo.model.client.Client;

import java.util.UUID;

public class ClientDetailDto {
    public ClientDetailDto(Client client) {
        this.clientId = client.getId();
        this.clientName = client.getClientName();
        this.email = client.getEmail();
        this.phone = client.getPhone();
    }

    private final UUID clientId;
    private String clientName;
    private String phone;
    private String email;

    public UUID getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
