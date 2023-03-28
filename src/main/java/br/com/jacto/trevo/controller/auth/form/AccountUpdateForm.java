package br.com.jacto.trevo.controller.auth.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class AccountUpdateForm {
    @NotNull(message = "Obrigatório o ID do gerente")
    private UUID accountId;

    private String email;

    @NotBlank(message = "Obrigatório a senha cadastrada")
    private String password;

    @NotBlank(message = "Obrigatório a senha atualizada")
    private String newPassword;

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
}
