package com.ferozfaiz.cti_product.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("CTICategoryService")
public class CategoryService extends MaterializedPathService<Category, Long> {
    @Autowired
    public CategoryService(CategoryRepository repository) {
        super(repository);
    }
}

