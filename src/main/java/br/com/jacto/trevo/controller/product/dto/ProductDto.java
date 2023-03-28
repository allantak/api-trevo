package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.product.Product;

import java.util.List;
import java.util.UUID;

public class ProductDto {

    public ProductDto(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.status = product.isStatus();
        this.cultures = product.getCultures().stream().map(ProductCultureDto::new).toList();
    }

    private final UUID productId;

    private final String productName;

    private final Product.Status status;

    private final List<ProductCultureDto> cultures;

    public String getProductName() {
        return productName;
    }

    public Product.Status getStatus() {
        return status;
    }


    public UUID getProductId() {
        return productId;
    }

    public List<ProductCultureDto> getCultures() {
        return cultures;
    }
}
