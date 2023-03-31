package br.com.jacto.trevo.model.order;

import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class OrderItem {

    public OrderItem() {
    }

    public OrderItem(Integer quantity, Account account, Product product) {
        this.quantity = quantity;
        this.account = account;
        this.product = product;
    }

    @Id
    @GeneratedValue()
    private UUID orderItemId;

    @Column(nullable = false)
    private Integer quantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "accountId", referencedColumnName = "accountId")
    private Account account;

    public Integer getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
