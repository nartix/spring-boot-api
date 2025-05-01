package com.ferozfaiz.product.manufacturer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Feroz Faiz
 */
public interface ProductManufacturerRepository
        extends JpaRepository<ProductManufacturer, Integer> {

    Optional<ProductManufacturer> findByName(String name);
}