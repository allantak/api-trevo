package br.com.jacto.trevo.model.account;

import br.com.jacto.trevo.model.product.Product;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
public class Account implements UserDetails {
    public Account() {
    }

    public Account(String email, String password, String accountName, Role accountRole) {
        this.email = email;
        this.accountPassword = password;
        this.accountName = accountName;
        this.accountRole = accountRole;
    }

    public enum Role {
        ADMINISTRADOR,
        COLABORADOR
    }


    @Id
    @GeneratedValue()
    private UUID accountId;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String accountPassword;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role accountRole;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Role getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(Role accountRole) {
        this.accountRole = accountRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + accountRole.toString()));
    }

    @Override
    public String getPassword() {
        return accountPassword;
    }

    public String getUsername() {
        return email;
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
