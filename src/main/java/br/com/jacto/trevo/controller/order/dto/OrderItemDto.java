package br.com.jacto.trevo.controller.order.dto;

import br.com.jacto.trevo.model.order.OrderItem;

import java.util.UUID;

public class OrderItemDto {

    public OrderItemDto(OrderItem order) {

        this.orderItemId = order.getOrderItemId();
        this.email = order.getAccount().getEmail();
        this.productName = order.getProduct().getProductName();
        this.quantity = order.getQuantity();
    }

    private final UUID orderItemId;
    private final String productName;
    private final String email;
    private final Integer quantity;

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public String getProductName() {
        return productName;
    }

    public String getEmail() {
        return email;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
