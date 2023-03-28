package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.product.Product;

import java.util.List;

public class ProductOrderDto {

    public ProductOrderDto(Product product) {
        this.productName = product.getProductName();
        this.description = product.getDescription();
        this.status = product.isStatus();
        this.orders = product.getOrders().stream().map(ProductOrderClientDto::new).toList();

    }

    private final String productName;
    private final Product.Status status;
    private final String description;

    private final List<ProductOrderClientDto> orders;

    public String getProductName() {
        return productName;
    }

    public Product.Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public List<ProductOrderClientDto> getOrders() {
        return orders;
    }

}
