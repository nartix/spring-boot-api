package com.ferozfaiz.common.tree.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathRepository;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

/**
 * @author Feroz Faiz
 */
@Repository
@Validated
public interface CategoryRepository extends MaterializedPathRepository<Category, Long> {
}
