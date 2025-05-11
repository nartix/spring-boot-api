package com.ferozfaiz.product.productattribute;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ferozfaiz.product.attributevalue.ProductAttributeValue;
import com.ferozfaiz.product.product.Product;
import jakarta.persistence.*;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * @author Feroz Faiz
 */
@Entity
@Table(
        name = "product_productattribute",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_product_productattribute_product_attribute_value",
                        columnNames = {"product_id", "attribute_value_id"}
                )
        },
        indexes = {
                @Index(
                        name = "product_productattribute_product_attribute_value_idx",
                        columnList = "product_id, attribute_value_id"
                )
        }
)
public class ProductProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK → product_product.id, ON DELETE CASCADE
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_product_productattribute_product",
                    foreignKeyDefinition =
                            "FOREIGN KEY (product_id) REFERENCES product_product(id) ON DELETE CASCADE"
            )
    )
    @RestResource(exported = false)
    @JsonBackReference
    private Product product;

    // FK → product_attributevalue.id, ON DELETE CASCADE
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "attribute_value_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_product_productattribute_attributevalue",
                    foreignKeyDefinition =
                            "FOREIGN KEY (attribute_value_id) REFERENCES product_attributevalue(id) ON DELETE CASCADE"
            )
    )
    @RestResource(exported = false)
    private ProductAttributeValue attributeValue;

    public ProductProductAttribute() {}

    public ProductProductAttribute(Product product, ProductAttributeValue attributeValue) {
        this.product = product;
        this.attributeValue = attributeValue;
    }

    // Getters & setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductAttributeValue getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(ProductAttributeValue attributeValue) {
        this.attributeValue = attributeValue;
    }

    // equals() & hashCode() on id, toString() if desired
}