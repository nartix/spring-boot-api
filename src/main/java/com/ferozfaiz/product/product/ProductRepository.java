package com.ferozfaiz.product.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Feroz Faiz
 */

@RepositoryRestResource(
        collectionResourceRel = "products",
        path = "products",
        excerptProjection = ProductProjection.class)
public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<Product> findBySlug(String slug);

    List<Product> findByNameContaining(String nameFragment);

    List<Product> findByIsActiveTrue();

    List<Product> findByBasePriceBetween(BigDecimal min, BigDecimal max);

//    @Override
//    @EntityGraph(attributePaths = {"brand"})
//    List<Product> findAll();

    // This method is used to fetch all products with their brand information
    // using join fetch otherwise it'll create separate query to fetch brand
    // which will cause N+1 problem
    @Override
    @EntityGraph(attributePaths = {"brand"})
    Page<Product> findAll(Pageable pageable);
}
