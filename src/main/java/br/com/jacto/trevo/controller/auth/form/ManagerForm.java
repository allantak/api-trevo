package br.com.jacto.trevo.controller.auth.form;

import javax.validation.constraints.NotBlank;

public class ManagerForm {

    @NotBlank(message = "Obrigatório o username")
    private String username;

    @NotBlank(message = "Obrigatório o password")
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
