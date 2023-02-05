package br.com.jacto.trevo.model.product;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageId;

    @Lob
    @Column(name = "img")
    private byte[] img;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

}
