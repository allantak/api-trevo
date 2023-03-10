package br.com.jacto.trevo.controller.client.dto;


import br.com.jacto.trevo.model.order.OrderItem;

import java.util.UUID;

public class ClientOrderProductDto {
    public ClientOrderProductDto(OrderItem order) {
        this.orderItemId = order.getOrderItemId();
        this.productId = order.getProduct().getProductId();
        this.productName = order.getProduct().getProductName();
        this.quantity = order.getQuantity();
    }

    private final UUID orderItemId;
    private final UUID productId;

    private final String productName;

    private final Integer quantity;

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Number getQuantity() {
        return quantity;
    }

}
