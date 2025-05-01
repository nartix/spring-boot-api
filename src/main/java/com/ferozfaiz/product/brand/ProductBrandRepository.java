package com.ferozfaiz.product.brand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Feroz Faiz
 */
public interface ProductBrandRepository
        extends JpaRepository<ProductBrand, Integer> {

    Optional<ProductBrand> findByName(String name);
}