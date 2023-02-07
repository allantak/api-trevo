package br.com.jacto.trevo.controller.client.form;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ClientUpdateForm {

    @NotNull
    private UUID clientId;

    private String clientName;

    private String email;

    private String phone;

    public UUID getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

}
