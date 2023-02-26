package br.com.jacto.trevo.controller.client.form;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ClientUpdateForm {

    @NotNull(message = "Obrigatório o ID do cliente")
    private UUID clientId;

    private String clientName;

    @Email(message = "Formato do email é inválido")
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

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
