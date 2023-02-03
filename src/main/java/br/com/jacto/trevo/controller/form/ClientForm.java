package br.com.jacto.trevo.controller.form;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ClientForm {

    @NotNull @NotEmpty
    private String clientName;

    @NotNull @NotEmpty
    private String email;

    @NotNull @NotEmpty
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
