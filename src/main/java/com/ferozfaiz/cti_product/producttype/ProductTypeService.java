package com.ferozfaiz.cti_product.producttype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class ProductTypeService {
    private final ProductTypeRepository repository;

    @Autowired
    public ProductTypeService(ProductTypeRepository repository) {
        this.repository = repository;
    }

    public ProductType save(ProductType productType) {
        return repository.save(productType);
    }

    public Optional<ProductType> findById(Long id) {
        return repository.findById(id);
    }

    public List<ProductType> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

