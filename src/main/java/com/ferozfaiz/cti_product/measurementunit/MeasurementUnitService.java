package com.ferozfaiz.cti_product.measurementunit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class MeasurementUnitService {
    private final MeasurementUnitRepository repository;

    @Autowired
    public MeasurementUnitService(MeasurementUnitRepository repository) {
        this.repository = repository;
    }

    public MeasurementUnit save(MeasurementUnit measurementUnit) {
        return repository.save(measurementUnit);
    }

    public Optional<MeasurementUnit> findById(Long id) {
        return repository.findById(id);
    }

    public List<MeasurementUnit> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

