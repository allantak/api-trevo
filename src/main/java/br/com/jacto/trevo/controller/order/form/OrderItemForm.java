package br.com.jacto.trevo.controller.order.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class OrderItemForm {

    @NotNull
    private UUID clientId;

    @NotBlank
    private String productName;

    @NotNull
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

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
