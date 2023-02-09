package br.com.jacto.trevo.controller.order.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class OrderItemUpdateForm {

    @NotNull
    private UUID orderItemId;
    @NotNull
    private UUID clientId;

    private String productName;

    private Integer quantity;

    public UUID getClientId() {
        return clientId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }
}
