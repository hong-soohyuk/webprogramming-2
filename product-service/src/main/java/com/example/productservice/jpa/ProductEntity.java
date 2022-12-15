package com.example.productservice.jpa;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="product")
public class ProductEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    private ProductEnum productStatus;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP")
    private Date createdAt;

}
