package com.ferozfaiz.cti_product.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("CtiProductRepository")
public interface ProductRepository extends JpaRepository<Product, Long> {
}

