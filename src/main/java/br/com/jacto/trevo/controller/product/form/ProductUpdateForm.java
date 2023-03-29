package br.com.jacto.trevo.controller.product.form;

import br.com.jacto.trevo.model.product.Product;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductUpdateForm {
    @NotNull(message = "Obrigatório o ID do produto")
    private UUID productId;

    private String productName;

    private Product.Status status;

    private Product.Category categoy;

    private Double areaSize;

    private Double price;

    private String description;


    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Product.Status getStatus() {
        return status;
    }

    public Double getAreaSize() {
        return areaSize;
    }

    public String getDescription() {
        return description;
    }


    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStatus(Product.Status status) {
        this.status = status;
    }

    public void setAreaSize(Double areaSize) {
        this.areaSize = areaSize;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Product.Category getCategoy() {
        return categoy;
    }

    public void setCategoy(Product.Category categoy) {
        this.categoy = categoy;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
