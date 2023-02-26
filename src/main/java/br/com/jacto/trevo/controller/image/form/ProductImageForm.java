package br.com.jacto.trevo.controller.image.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ProductImageForm {
    @NotNull(message = "Obrigatório o ID do produto")
    private UUID productId;
    @NotNull(message = "Obrigatório o arquivo de imagem")
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
