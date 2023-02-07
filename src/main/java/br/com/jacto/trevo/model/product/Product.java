package br.com.jacto.trevo.model.product;

import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.model.order.OrderItem;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity()
public class Product {

    public Product() {
    }

    public Product(String productName, Boolean status, String description, Double areaSize, LocalDate createAt) {
        setProductName(productName);
        setStatus(status);
        setDescription(description);
        setAreaSize(areaSize);
        setCreateAt(createAt);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column
    private Double areaSize;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false ,columnDefinition = "DEFAULT CURRENT_DATE")
    private LocalDate createAt;

    @Column(nullable = false)
    private Boolean status;

    @OneToMany(mappedBy = "product")
    private List<Culture> cultures;

    @OneToMany(mappedBy = "product")
    private List<Image> imgs;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orders;

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getAreaSize() {
        return areaSize;
    }

    public void setAreaSize(Double areaSize) {
        this.areaSize = areaSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


}
