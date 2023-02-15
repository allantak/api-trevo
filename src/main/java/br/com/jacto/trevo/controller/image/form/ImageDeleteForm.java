package br.com.jacto.trevo.controller.image.form;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ImageDeleteForm {

    @NotNull
    private UUID imageId;

    @NotNull
    private UUID productId;

    public UUID getImageId() {
        return imageId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
