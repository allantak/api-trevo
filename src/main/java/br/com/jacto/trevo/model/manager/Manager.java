package br.com.jacto.trevo.model.manager;

import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Product;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
public class Manager implements UserDetails {
    public Manager() {
    }

    public Manager(String username, String managerPassword) {
        this.username = username;
        this.managerPassword = managerPassword;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID managerId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String managerPassword;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return managerPassword;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
