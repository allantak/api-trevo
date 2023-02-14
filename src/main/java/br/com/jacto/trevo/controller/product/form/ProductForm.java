package br.com.jacto.trevo.controller.product.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class ProductForm {
    @NotBlank
    private String productName;

    @NotNull
    private Boolean status;

    @NotBlank
    private String description;

    @NotNull @NotEmpty
    private List<String> cultures;

    private Double areaSize;

    private LocalDate createAt;

    public List<String> getCultures() {
        return cultures;
    }

    public LocalDate getCreateAt() {
        return createAt;
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


    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }
}
