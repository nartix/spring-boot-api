package com.ferozfaiz.product.measurementunit;

import com.ferozfaiz.product.brand.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Feroz Faiz
 */
public interface ProductBrandRepository
        extends JpaRepository<ProductBrand, Integer> {

    /**
     * Uses the product_brand_name_idx for fast name-based queries.
     */
    Optional<ProductBrand> findByName(String name);
}
