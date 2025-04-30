package com.ferozfaiz.product.model.Brand;

import jakarta.persistence.*;

/**
 * @author Feroz Faiz
 */

@Entity
@Table(
        name = "product_brand",
        indexes = {
                @Index(name = "product_brand_name_idx", columnList = "name")
        }
)
public class ProductBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    public ProductBrand() {}

    public ProductBrand(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
