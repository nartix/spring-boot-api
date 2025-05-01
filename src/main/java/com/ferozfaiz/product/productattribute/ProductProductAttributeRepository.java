package com.ferozfaiz.product.productattribute;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductProductAttributeRepository
        extends JpaRepository<ProductProductAttribute, Integer> {

    /**
     * Uses the product_productattribute_product_attribute_value_idx index.
     */
    List<ProductProductAttribute> findByProductId(Integer productId);

    List<ProductProductAttribute> findByAttributeValueId(Integer attributeValueId);
}