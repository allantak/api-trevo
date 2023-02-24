package br.com.jacto.trevo.model.product;

import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.model.order.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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

    @Column(nullable = false)
    private LocalDate createAt;

    @Column(nullable = false)
    private Boolean status;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Culture> cultures;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> imgs;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orders;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "managerId", referencedColumnName = "managerId")
    private Manager manager;


    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

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

    public List<Culture> getCultures() {
        return cultures;
    }

    public List<Image> getImgs() {
        return imgs;
    }

    public List<OrderItem> getOrders() {
        return orders;
    }

    public void setCultures(List<Culture> cultures) {
        this.cultures = cultures;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setImgs(List<Image> imgs) {
        this.imgs = imgs;
    }
}
