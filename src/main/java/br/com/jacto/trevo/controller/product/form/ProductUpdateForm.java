package br.com.jacto.trevo.controller.product.form;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public class ProductUpdateForm {
    @NotNull(message = "Obrigatório o ID do produto")
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

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setAreaSize(Double areaSize) {
        this.areaSize = areaSize;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }
}
