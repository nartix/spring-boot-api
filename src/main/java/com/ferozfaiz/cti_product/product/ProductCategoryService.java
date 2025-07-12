package com.ferozfaiz.cti_product.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository repository;

    @Autowired
    public ProductCategoryService(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    public ProductCategory save(ProductCategory productCategory) {
        return repository.save(productCategory);
    }

    public Optional<ProductCategory> findById(Long id) {
        return repository.findById(id);
    }

    public List<ProductCategory> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

