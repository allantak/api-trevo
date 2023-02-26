package br.com.jacto.trevo.controller.image.form;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ImageDeleteForm {

    @NotNull(message = "Obrigatório o ID da imagem")
    private UUID imageId;

    @NotNull(message = "Obrigatório o ID do produto")
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
