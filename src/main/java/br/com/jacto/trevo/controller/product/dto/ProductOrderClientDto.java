package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.order.OrderItem;

import java.util.UUID;

public class ProductOrderClientDto {

    public ProductOrderClientDto(OrderItem order) {
        this.orderItemId = order.getOrderItemId();
        this.clientId = order.getClient().getId();
        this.email = order.getClient().getEmail();
        this.clientName = order.getClient().getClientName();
        this.quantity = order.getQuantity();

    }

    private final UUID orderItemId;
    private final UUID clientId;
    private final String email;
    private final String clientName;
    private final Integer quantity;

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getEmail() {
        return email;
    }

    public String getClientName() {
        return clientName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
