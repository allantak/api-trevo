package br.com.jacto.trevo.model.product;

import br.com.jacto.trevo.model.order.OrderItem;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity()
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column
    private String areaSize;

    @Column
    private String description;

    @Column
    private Date createAt;

    @Column(nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "product")
    private List<Culture> cultures;

    @OneToMany(mappedBy = "product")
    private List<Image> imgs;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orders;


}
