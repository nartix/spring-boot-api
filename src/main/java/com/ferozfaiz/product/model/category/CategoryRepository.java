package com.ferozfaiz.product.model.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Feroz Faiz
 */
@Repository
public interface CategoryRepository extends MaterializedPathRepository<Category, Long> {
}
