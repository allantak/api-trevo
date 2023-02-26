package br.com.jacto.trevo.controller.product.form;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ProductCultureDeleteForm {
    @NotNull(message = "Obrigatório o ID da cultura")
    private UUID cultureId;

    @NotNull(message = "Obrigatório o ID do produto")
    private UUID productId;


    public UUID getCultureId() {
        return cultureId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setCultureId(UUID cultureId) {
        this.cultureId = cultureId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
