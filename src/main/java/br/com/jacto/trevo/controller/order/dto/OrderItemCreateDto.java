package br.com.jacto.trevo.controller.order.dto;

import br.com.jacto.trevo.model.order.OrderItem;

import java.util.UUID;

public class OrderItemCreateDto {

    public OrderItemCreateDto(OrderItem order) {
        this.orderItemId = order.getOrderItemId();
    }

    private final UUID orderItemId;

    public UUID getOrderItemId() {
        return orderItemId;
    }
}
