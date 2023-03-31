package br.com.jacto.trevo.controller.auth.dto;

import br.com.jacto.trevo.model.order.OrderItem;

import java.util.UUID;

public class AccountListOrderDto {

    public AccountListOrderDto(OrderItem order){
        this.orderItemId = order.getOrderItemId();
        this.productId = order.getProduct().getProductId();
        this.quantity = order.getQuantity();
    }

    private final UUID orderItemId;
    private final UUID productId;

    private final Integer quantity;


    public UUID getOrderItemId() {
        return orderItemId;
    }

    public UUID getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
