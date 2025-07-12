package com.ferozfaiz.cti_product.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PriceHistoryService {
    private final PriceHistoryRepository repository;

    @Autowired
    public PriceHistoryService(PriceHistoryRepository repository) {
        this.repository = repository;
    }

    public PriceHistory save(PriceHistory priceHistory) {
        return repository.save(priceHistory);
    }

    public Optional<PriceHistory> findById(Long id) {
        return repository.findById(id);
    }

    public List<PriceHistory> findAll() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

