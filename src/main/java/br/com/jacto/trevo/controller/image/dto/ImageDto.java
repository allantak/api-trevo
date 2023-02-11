package br.com.jacto.trevo.controller.image.dto;

import br.com.jacto.trevo.model.product.Image;

import java.util.UUID;

public class ImageDto {

    public ImageDto(Image img){
        this.imageId = img.getImageId();
        this.img = img.getImg();
    }

    private final UUID imageId;

    private final byte[] img;

    public UUID getImageId() {
        return imageId;
    }

    public byte[] getImg() {
        return img;
    }
}
