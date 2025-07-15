package com.ferozfaiz.cti_product.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CTICategoryService extends MaterializedPathService<CTICategory, Long> {
    @Autowired
    public CTICategoryService(CTICategoryRepository repository) {
        super(repository);
    }
}

