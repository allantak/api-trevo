package br.com.jacto.trevo.controller.image.form;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ImageUpdateForm {

    @NotNull(message = "Obrigatório o ID do produto")
    private UUID productId;

    @NotNull(message = "Obrigatório o ID da imagem")
    private UUID imageId;

    @NotNull(message = "Obrigatório o arquivo de imagem")
    private MultipartFile img;


    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public MultipartFile getImg() {
        return img;
    }

    public void setImg(MultipartFile img) {
        this.img = img;
    }
}
