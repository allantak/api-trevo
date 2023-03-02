package br.com.jacto.trevo.controller.auth.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ManagerUpdateForm {
    @NotNull(message = "Obrigatório o ID do gerente")
    private UUID managerId;

    private String username;

    @NotBlank(message = "Obrigatório a senha cadastrada")
    private String password;

    @NotBlank(message = "Obrigatório a senha atualizada")
    private String newPassword;

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String email) {
        this.username = email;
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
