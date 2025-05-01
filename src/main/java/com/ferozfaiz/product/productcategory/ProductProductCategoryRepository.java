package com.ferozfaiz.product.productcategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductProductCategoryRepository
        extends JpaRepository<ProductProductCategory, Integer> {

    /**
     * Fast lookup of all categories for a given product (uses composite index).
     */
    List<ProductProductCategory> findByProductId(Integer productId);

    /**
     * Fast lookup of all products in a given category (uses composite index).
     */
    List<ProductProductCategory> findByCategoryId(Integer categoryId);
}