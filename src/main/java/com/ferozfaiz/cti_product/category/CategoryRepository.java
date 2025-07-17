package com.ferozfaiz.cti_product.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathRepository;
import org.springframework.stereotype.Repository;

@Repository("CTICategoryRepository")
public interface CategoryRepository extends MaterializedPathRepository<Category, Long> {
}

