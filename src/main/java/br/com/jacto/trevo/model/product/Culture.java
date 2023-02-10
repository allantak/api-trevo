package br.com.jacto.trevo.model.product;

import jakarta.persistence.*;

import java.util.UUID;

@Entity()
public class Culture {
    public Culture(){}

    public Culture(String cultureName, Product product){
        this.cultureName = cultureName;
        this.product = product;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cultureId;

    @Column(nullable = false)
    private String cultureName;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;


    public UUID getCultureId() {
        return cultureId;
    }

    public String getCultureName() {
        return cultureName;
    }

    public Product getProduct() {
        return product;
    }

    public void setCultureName(String cultureName) {
        this.cultureName = cultureName;
    }
}
