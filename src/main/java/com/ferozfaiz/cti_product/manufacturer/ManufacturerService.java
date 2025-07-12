package com.ferozfaiz.cti_product.manufacturer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class ManufacturerService {
    private final ManufacturerRepository repository;

    @Autowired
    public ManufacturerService(ManufacturerRepository repository) {
        this.repository = repository;
    }

    public Manufacturer save(Manufacturer manufacturer) {
        return repository.save(manufacturer);
    }

    public Optional<Manufacturer> findById(Long id) {
        return repository.findById(id);
    }

    public List<Manufacturer> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

