package com.ferozfaiz.product.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Feroz Faiz
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findBySlug(String slug);

    List<Product> findByNameContaining(String nameFragment);

    List<Product> findByIsActiveTrue();

    List<Product> findByBasePriceBetween(BigDecimal min, BigDecimal max);
}
