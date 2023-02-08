package br.com.jacto.trevo.controller.client.dto;

import br.com.jacto.trevo.model.client.Client;

import java.util.List;

public class ClientOrderDto {
    public ClientOrderDto(Client client) {
        this.clientName = client.getClientName();
        this.email = client.getEmail();
        this.phone = client.getPhone();
        this.orders = client.getOrders().stream().map(ClientOrderProductDto::new).toList();
    }

    private final String clientName;

    private final String email;

    private final String phone;

    private final List<ClientOrderProductDto> orders;

    public String getClientName() {
        return clientName;
    }

    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }

    public List<ClientOrderProductDto> getOrders() {
        return orders;
    }

}
