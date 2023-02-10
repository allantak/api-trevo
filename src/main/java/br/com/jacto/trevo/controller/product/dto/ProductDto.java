package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.product.Product;

import java.util.List;
import java.util.UUID;

public class ProductDto {

    public ProductDto(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.status = product.isStatus();
        this.description = product.getDescription();
        this.cultures = product.getCultures().stream().map(ProductCultureDto::new).toList();
    }

    private final UUID productId;

    private final String productName;

    private final Boolean status;

    private final String description;

    private final List<ProductCultureDto> cultures;

    public String getProductName() {
        return productName;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public UUID getProductId() {
        return productId;
    }

    public List<ProductCultureDto> getCultures() {
        return cultures;
    }
}
