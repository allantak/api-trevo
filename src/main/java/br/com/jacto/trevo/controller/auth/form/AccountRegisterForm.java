package br.com.jacto.trevo.controller.auth.form;

import br.com.jacto.trevo.model.account.Account;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AccountRegisterForm {
    @NotBlank(message = "Obrigatório o nome")
    private String accountName;

    @NotBlank(message = "Obrigatório o email")
    @Email(message = "Formato do email é inválido")
    private String email;

    @NotBlank(message = "Obrigatório o password")
    private String password;

    @NotNull(message = "Obrigatório a permissão")
    private Account.Role accountRole;

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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Account.Role getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(Account.Role accountRole) {
        this.accountRole = accountRole;
    }
}
