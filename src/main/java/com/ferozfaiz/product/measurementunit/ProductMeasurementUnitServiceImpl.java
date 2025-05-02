package com.ferozfaiz.product.measurementunit;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Feroz Faiz
 */
@Service
@Transactional
public class ProductMeasurementUnitServiceImpl implements ProductMeasurementUnitService {

    private final ProductMeasurementUnitRepository repo;

    public ProductMeasurementUnitServiceImpl(ProductMeasurementUnitRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductMeasurementUnit create(ProductMeasurementUnit unit) {
        return repo.save(unit);  // INSERT → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductMeasurementUnit> findAll() {
        return repo.findAll();   // SELECT * → O(n)
    }

    @Override
    @Transactional(readOnly = true)
    public ProductMeasurementUnit findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "MeasurementUnit not found with id " + id
                        )
                );
    }

    @Override
    public ProductMeasurementUnit update(Integer id, ProductMeasurementUnit unit) {
        ProductMeasurementUnit existing = findById(id);
        existing.setName(unit.getName());
        existing.setSymbol(unit.getSymbol());
        existing.setConversionFactor(unit.getConversionFactor());
        return repo.save(existing); // UPDATE → O(1)
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete – no measurement unit with id " + id
            );
        }
        repo.deleteById(id);       // DELETE → O(1)
    }
}