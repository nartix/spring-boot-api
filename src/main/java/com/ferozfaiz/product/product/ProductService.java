package com.ferozfaiz.product.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductService {

    Product create(Product product);

    List<Product> findAll();

    Page<ProductDto> findAll(ProductFilter filter, Pageable pg);

    Product findById(Integer id);

    Product update(Integer id, Product product);

    void delete(Integer id);

    Product findBySlug(String slug);

    List<Product> searchByName(String fragment);

    List<Product> findActiveProducts();

    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
}