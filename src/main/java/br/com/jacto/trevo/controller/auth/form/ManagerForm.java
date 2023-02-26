package br.com.jacto.trevo.controller.auth.form;

import br.com.jacto.trevo.model.manager.Manager;

import javax.validation.constraints.NotBlank;

public class ManagerForm {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
