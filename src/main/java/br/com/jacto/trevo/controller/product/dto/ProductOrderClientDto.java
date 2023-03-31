package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.order.OrderItem;

import java.util.UUID;

public class ProductOrderClientDto {

    public ProductOrderClientDto(OrderItem order) {
        this.orderItemId = order.getOrderItemId();
        this.accountId = order.getAccount().getAccountId();
        this.email = order.getAccount().getEmail();
        this.accountName = order.getAccount().getAccountName();
        this.quantity = order.getQuantity();
    }

    private final UUID orderItemId;
    private final UUID accountId;
    private final String email;
    private final String accountName;
    private final Integer quantity;

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getAccountName() {
        return accountName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
