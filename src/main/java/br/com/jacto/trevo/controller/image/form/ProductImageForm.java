package br.com.jacto.trevo.controller.image.form;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class ProductImageForm {
    @NotNull
    private UUID productId;
    @NotNull
    private MultipartFile image;

    public MultipartFile getImage() {
        return image;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }


}
