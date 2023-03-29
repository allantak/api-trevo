package br.com.jacto.trevo.controller.product.form;

import br.com.jacto.trevo.model.product.Product;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ProductForm {

    @NotNull(message = "Obrigatório id do Usuario")
    private UUID accountId;

    @NotBlank(message = "Obrigatório o nome do produto")
    private String productName;

    @NotNull(message = "Obrigatório o status do produto")
    private Product.Status status;

    @NotNull(message = "Obrigatório a categoria do produto")
    private Product.Category category;

    @NotBlank(message = "Obrigatório a descricao do produto")
    private String description;

    @NotNull(message = "Obrigatório a lista de cultura")
    @NotEmpty
    private List<String> cultures;

    private Double areaSize;

    private Double price;

    public UUID getAccountId() {
        return accountId;
    }

    public List<String> getCultures() {
        return cultures;
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

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStatus(Product.Status status) {
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

    public Product.Category getCategory() {
        return category;
    }

    public void setCategory(Product.Category category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
