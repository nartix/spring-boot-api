package com.ferozfaiz.product.model.manufacturer;

import jakarta.persistence.*;

/**
 * @author Feroz Faiz
 */

@Entity
@Table(
        name = "product_manufacturer",
        indexes = {
                @Index(name = "product_manufacturer_name_idx", columnList = "name")
        }
)
public class ProductManufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String name;

    public ProductManufacturer() {}

    public ProductManufacturer(String name) {
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

