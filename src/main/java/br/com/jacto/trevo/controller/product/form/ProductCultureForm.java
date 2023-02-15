package br.com.jacto.trevo.controller.product.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ProductCultureForm {

    @NotNull
    private UUID productId;

    @NotNull
    private UUID cultureId;

    @NotBlank
    private String cultureName;

    public String getCultureName() {
        return cultureName;
    }

    public UUID getCultureId() {
        return cultureId;
    }

    public void setProductName(String cultureName) {
        this.cultureName = cultureName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setCultureId(UUID cultureId) {
        this.cultureId = cultureId;
    }

    public void setCultureName(String cultureName) {
        this.cultureName = cultureName;
    }
}
