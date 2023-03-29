package br.com.jacto.trevo.controller.auth.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class AccountLoginForm {
    @NotBlank(message = "Obrigatório o email")
    @Email(message = "Formato do email é inválido")
    private String email;

    @NotBlank(message = "Obrigatório o password")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
