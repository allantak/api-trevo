package br.com.jacto.trevo.controller.order.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class OrderItemForm {

    @NotNull(message = "Obrigatório o ID da conta")
    private UUID accountId;

    @NotBlank(message = "Obrigatório o nome do produto")
    private String productName;

    @NotNull(message = "Obrigatório o quantidade do produto")
    private Integer quantity;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
