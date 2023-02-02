package br.com.jacto.trevo.model.product;

import jakarta.persistence.*;

import java.util.UUID;

@Entity()
public class Culture {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cultureId;
    @Column(nullable = false)
    private String cultureName;
    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

}
