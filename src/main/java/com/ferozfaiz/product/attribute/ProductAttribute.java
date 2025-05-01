package com.ferozfaiz.product.attribute;

import jakarta.persistence.*;

/**
 * @author Feroz Faiz
 */
@Entity
@Table(
        name = "product_attribute",
        indexes = {
                @Index(name = "product_attribute_name_idx", columnList = "name")
        }
)
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "data_type", nullable = false, length = 50)
    private String dataType;

    public ProductAttribute() {}

    public ProductAttribute(String name, String dataType) {
        this.name = name;
        this.dataType = dataType;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    // equals/hashCode on id, toString() for logging if desired
}
