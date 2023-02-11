package br.com.jacto.trevo.controller.image.dto;

import br.com.jacto.trevo.model.product.Image;
import br.com.jacto.trevo.model.product.Product;

import java.util.List;
import java.util.UUID;

public class ProductImageDto {

    public ProductImageDto(Product product){
        this.productId = product.getProductId();
        this.imgs = product.getImgs().stream().map(ImageDto::new).toList();

    }

    private final UUID productId;
    private final List<ImageDto> imgs;

    public UUID getProductId() {
        return productId;
    }

    public List<ImageDto> getImgs() {
        return imgs;
    }
}
