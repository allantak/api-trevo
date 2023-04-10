package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.product.Product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductDetailDto {

    public ProductDetailDto(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.description = product.getDescription();
        this.areaSize = product.getAreaSize();
        this.price = product.getPrice();
        this.createAt = product.getCreateAt();
        this.status = product.isStatus();
        this.category = product.getCategory();
        this.cultures = product.getCultures().stream().map(ProductCultureDto::new).collect(Collectors.toList());
    }

    private final UUID productId;

    private final String productName;

    private final String description;

    private final Product.Status status;

    private final Product.Category category;

    private final LocalDateTime createAt;

    private final Double areaSize;

    private final Double price;

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

    public Product.Category getCategory() {
        return category;
    }

    public Double getAreaSize() {
        return areaSize;
    }

    public Double getPrice() {
        return price;
    }
}
