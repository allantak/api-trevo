package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.product.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductDetailDto {

    public ProductDetailDto(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.description = product.getDescription();
        this.createAt = product.getCreateAt();
        this.status = product.isStatus();
        this.cultures = product.getCultures().stream().map(ProductCultureDto::new).toList();
    }

    private final UUID productId;

    private final String productName;

    private final String description;

    private final Product.Status status;

    private final LocalDateTime createAt;


    private final List<ProductCultureDto> cultures;

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public Product.Status getStatus() {
        return status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public List<ProductCultureDto> getCultures() {
        return cultures;
    }
}
