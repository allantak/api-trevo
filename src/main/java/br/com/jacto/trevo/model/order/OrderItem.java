package br.com.jacto.trevo.model.order;

import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.product.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Optional;
import java.util.UUID;

@Entity
public class OrderItem {

    public OrderItem(){}

    public OrderItem(Integer quantity, Client client, Product product){
        this.quantity = quantity;
        this.client = client;
        this.product = product;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderItemId;

    @Column(nullable = false)
    private Integer quantity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "clientId", referencedColumnName = "clientId")
    private Client client;

    public Integer getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public Client getClient() {
        return client;
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

    public void setClient(Client client) {
        this.client = client;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }
}
