package com.ferozfaiz.cti_product.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class BrandService {
    private final BrandRepository repository;

    @Autowired
    public BrandService(BrandRepository repository) {
        this.repository = repository;
    }

    public Brand save(Brand brand) {
        return repository.save(brand);
    }

    public Optional<Brand> findById(Long id) {
        return repository.findById(id);
    }

    public List<Brand> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

