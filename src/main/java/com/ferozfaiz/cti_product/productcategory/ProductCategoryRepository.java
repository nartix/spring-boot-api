package com.ferozfaiz.cti_product.productcategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("CTIProductCategoryRepository")
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}

