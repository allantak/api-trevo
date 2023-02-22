package br.com.jacto.trevo.model.product;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Image {

    public Image(){}

    public Image(byte[] imageBytes, Product product) {
        this.img = imageBytes;
        this.product = product;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageId;


    @Column(name = "img")
    private byte[] img;

    @ManyToOne
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;


    public byte[] getImg() {
        return img;
    }

    public UUID getImageId() {
        return imageId;
    }

    public Product getProduct() {
        return product;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
