package com.ferozfaiz.cti_product.manufacturer;

import jakarta.persistence.*;

@Entity
@Table(name = "cti_manufacturer", indexes = {
        @Index(name = "idx_manufacturer_name", columnList = "name")
})
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // Uncomment if locale is needed in the future
    // @Column(name = "locale", length = 50)
    // private String locale;

    public Manufacturer() {}

    public Manufacturer(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
