package com.ferozfaiz.product.measurementunit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Feroz Faiz
 */
public interface ProductMeasurementUnitRepository
        extends JpaRepository<ProductMeasurementUnit, Integer> {

    Optional<ProductMeasurementUnit> findByName(String name);

    Optional<ProductMeasurementUnit> findBySymbol(String symbol);
}
