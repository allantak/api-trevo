package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.product.Product;

public class ProductDto {

    public ProductDto(Product product) {
        this.productName = product.getProductName();
        this.status = product.isStatus();
        this.description = product.getDescription();
    }

    private final String productName;

    private final Boolean status;

    private final String description;


    public String getProductName() {
        return productName;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
