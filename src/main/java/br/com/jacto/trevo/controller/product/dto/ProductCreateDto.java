package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.product.Product;

import java.util.UUID;

public class ProductCreateDto {

    public ProductCreateDto(Product product){
        this.productId = product.getProductId();
        this.productName = product.getProductName();
    }

    public final UUID productId;

    public final String productName;

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }
}
