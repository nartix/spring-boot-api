package com.ferozfaiz.product.attribute;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Feroz Faiz
 */
public interface ProductAttributeRepository
        extends JpaRepository<ProductAttribute, Integer> {

    /**
     * Uses product_attribute_name_idx and the unique constraint.
     */
    Optional<ProductAttribute> findByName(String name);
}