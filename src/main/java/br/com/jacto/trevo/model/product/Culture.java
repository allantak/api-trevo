package br.com.jacto.trevo.model.product;

import javax.persistence.*;

import java.util.UUID;

@Entity()
public class Culture {
    public Culture() {
    }

    public Culture(String cultureName, Product product) {
        this.cultureName = cultureName;
        this.product = product;
    }

    @Id
    @GeneratedValue()
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

    public void setCultureId(UUID cultureId) {
        this.cultureId = cultureId;
    }
}
