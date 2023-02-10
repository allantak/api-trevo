package br.com.jacto.trevo.controller.product.form;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ProductCultureDeleteForm {
    @NotNull
    private UUID cultureId;

    @NotNull
    private UUID productId;


    public UUID getCultureId() {
        return cultureId;
    }

    public UUID getProductId() {
        return productId;
    }
}
