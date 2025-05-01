package com.ferozfaiz.product.attributevalue;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Feroz Faiz
 */
@Service
@Transactional
public class ProductAttributeValueServiceImpl implements ProductAttributeValueService {

    private final ProductAttributeValueRepository repo;

    public ProductAttributeValueServiceImpl(ProductAttributeValueRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductAttributeValue create(ProductAttributeValue value) {
        return repo.save(value);  // INSERT → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeValue> findAll() {
        return repo.findAll();    // SELECT * → O(n)
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAttributeValue findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "AttributeValue not found with id " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeValue> findByAttributeId(Integer attributeId) {
        return repo.findByAttributeId(attributeId); // uses index → O(log n)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeValue> findByMeasurementUnitId(Integer measurementUnitId) {
        return repo.findByMeasurementUnitId(measurementUnitId);
    }

    @Override
    public ProductAttributeValue update(Integer id, ProductAttributeValue value) {
        ProductAttributeValue existing = findById(id);
        existing.setAttribute(value.getAttribute());
        existing.setValueString(value.getValueString());
        existing.setValueNumeric(value.getValueNumeric());
        existing.setValueBoolean(value.getValueBoolean());
        existing.setMeasurementUnit(value.getMeasurementUnit());
        return repo.save(existing); // UPDATE → O(1)
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete – no AttributeValue with id " + id
            );
        }
        repo.deleteById(id);        // DELETE → O(1)
    }
}
