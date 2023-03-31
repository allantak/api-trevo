package br.com.jacto.trevo.controller.order.form;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class OrderItemUpdateForm {

    @NotNull(message = "Obrigatório o ID do pedido")
    private UUID orderItemId;

    @NotNull(message = "Obrigatório o ID do cliente")
    private UUID accountId;

    private String productName;

    private Integer quantity;

    public UUID getAccountId() {
        return accountId;
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

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setAccountId(UUID clientId) {
        this.accountId = clientId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
