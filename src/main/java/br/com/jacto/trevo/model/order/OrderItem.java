package br.com.jacto.trevo.model.order;

import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.model.product.Product;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderItemId;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "clientId", referencedColumnName = "clientId")
    private Client client;

}
