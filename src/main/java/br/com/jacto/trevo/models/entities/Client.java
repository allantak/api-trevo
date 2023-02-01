package br.com.jacto.trevo.models.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID client_id;

    @Column(nullable = false)
    private String client_name;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column(nullable = false, unique = true)
    private String email;



    public String getClient_name() {
        return client_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UUID getClient_id() {
        return client_id;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
