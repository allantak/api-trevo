package br.com.jacto.trevo.controller.image.dto;

import br.com.jacto.trevo.model.product.Product;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductImageDto {

    public ProductImageDto(Product product) {
        this.productId = product.getProductId();
        this.imgs = product.getImgs().stream().map(ProductImageListDto::new).collect(Collectors.toList());
    }

    private final UUID productId;
    private final List<ProductImageListDto> imgs;

    public UUID getProductId() {
        return productId;
    }

    public List<ProductImageListDto> getImgs() {
        return imgs;
    }
}
