package br.com.jacto.trevo.controller.product.form;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class ProductUpdateForm {
    @NotNull
    private UUID productId;

    private String productName;

    private Boolean status;

    private Double areaSize;

    private String description;

    private LocalDate createAt;

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Boolean getStatus() {
        return status;
    }

    public Double getAreaSize() {
        return areaSize;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

}
