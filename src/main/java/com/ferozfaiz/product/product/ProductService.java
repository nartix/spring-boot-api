package com.ferozfaiz.product.product;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductService {

    Product create(Product product);

    List<Product> findAll();

    Product findById(Integer id);

    Product update(Integer id, Product product);

    void delete(Integer id);

    Product findBySlug(String slug);

    List<Product> searchByName(String fragment);

    List<Product> findActiveProducts();

    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
}