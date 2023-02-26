package br.com.jacto.trevo.controller.client.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ClientForm {

    @NotBlank(message = "Obrigatório o nome")
    private String clientName;

    @NotBlank
    @Email(message = "Formato do email é inválido")
    private String email;

    @NotBlank(message = "Obrigatório o telefone")
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
