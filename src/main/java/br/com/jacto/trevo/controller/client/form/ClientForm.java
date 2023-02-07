package br.com.jacto.trevo.controller.client.form;

import jakarta.validation.constraints.NotBlank;

public class ClientForm {

    @NotBlank
    private String clientName;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

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
