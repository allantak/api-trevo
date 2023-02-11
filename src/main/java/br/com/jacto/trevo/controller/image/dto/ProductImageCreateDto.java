package br.com.jacto.trevo.controller.image.dto;

import br.com.jacto.trevo.model.product.Image;

import java.util.UUID;

public class ProductImageCreateDto {

    public ProductImageCreateDto(Image img){
        this.imageId = img.getImageId();
    }
    private final UUID imageId;

    public UUID getImageId() {
        return imageId;
    }
}
