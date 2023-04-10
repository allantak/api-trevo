package br.com.jacto.trevo.model.product;

import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.order.OrderItem;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity()
public class Product {

    public Product() {
    }

    public Product(String productName, Status status, Category category, String description, Double areaSize, Double price, LocalDateTime createAt, Account manager) {
        setProductName(productName);
        setStatus(status);
        setCategory(category);
        setDescription(description);
        setAreaSize(areaSize);
        setPrice(price);
        setCreateAt(createAt);
        setManager(manager);
    }

    public enum Status {
        DISPONIVEL,
        INDISPONIVEL,
        FORA_DE_LINHA

    }

    public enum Category {
        MANUAL,
        ELETRICO,
        COMBUSTIVEL
    }

    @Id
    @GeneratedValue()
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column
    private Double areaSize;

    @Column
    private Double price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

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
    @JoinColumn(name = "accountId", referencedColumnName = "accountId")
    private Account account;


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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public Status isStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public void setManager(Account account) {
        this.account = account;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
