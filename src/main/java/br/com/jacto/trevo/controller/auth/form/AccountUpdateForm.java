package br.com.jacto.trevo.controller.auth.form;

import br.com.jacto.trevo.model.account.Account;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class AccountUpdateForm {
    @NotNull(message = "Obrigatório o ID do gerente")
    private UUID accountId;

    private String email;

    @NotBlank(message = "Obrigatório a senha cadastrada")
    private String password;

    private String newPassword;

    private String accountName;

    private Account.Role accountRole;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
