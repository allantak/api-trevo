package br.com.jacto.trevo.controller.product.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ProductCultureForm {

    @NotNull(message = "Obrigatório o ID do produto")
    private UUID productId;

    @NotNull(message = "Obrigatório o ID da cultura")
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
