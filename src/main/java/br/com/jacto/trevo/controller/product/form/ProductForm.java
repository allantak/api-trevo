package br.com.jacto.trevo.controller.product.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProductForm {

    @NotNull(message = "Obrigatório id do gerente")
    private UUID managerId;

    @NotBlank(message = "Obrigatório o nome do produto")
    private String productName;

    @NotNull(message = "Obrigatório o status do produto")
    private Boolean status;

    @NotBlank(message = "Obrigatório a descricao do produto")
    private String description;

    @NotNull(message = "Obrigatório a lista de cultura")
    @NotEmpty
    private List<String> cultures;

    private Double areaSize;

    private LocalDate createAt;

    public UUID getManagerId() {
        return managerId;
    }

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

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCultures(List<String> cultures) {
        this.cultures = cultures;
    }

    public void setAreaSize(Double areaSize) {
        this.areaSize = areaSize;
    }


}
