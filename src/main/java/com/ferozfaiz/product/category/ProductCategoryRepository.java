package com.ferozfaiz.product.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Feroz Faiz
 */
@Repository
public interface ProductCategoryRepository extends MaterializedPathRepository<ProductCategory, Long> {
}
