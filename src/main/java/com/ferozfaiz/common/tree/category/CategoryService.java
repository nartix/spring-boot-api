package com.ferozfaiz.common.tree.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Feroz Faiz
 */
@Service
public class CategoryService extends MaterializedPathService<Category, Long> {

    @Autowired
    public CategoryService(CategoryRepository repository) {
        super(repository);
    }
}
