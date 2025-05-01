package com.ferozfaiz.product.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Feroz Faiz
 */
@Service
public class ProductCategoryService extends MaterializedPathService<ProductCategory, Long> {
    @Autowired
    public ProductCategoryService(ProductCategoryRepository repository) {
        super(repository);
    }
}
