package br.com.jacto.trevo.model.client;

import br.com.jacto.trevo.model.order.OrderItem;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Client {

    public Client() {
    }

    public Client(String name, String email, String phone) {
        setClientName(name);
        setEmail(email);
        setPhone(phone);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID clientId;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orders;

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    public String getClientName() {
        return clientName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UUID getId() {
        return clientId;
    }

    public List<OrderItem> getOrders() {
        return orders;
    }
}
