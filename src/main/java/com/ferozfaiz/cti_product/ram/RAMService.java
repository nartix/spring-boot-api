package com.ferozfaiz.cti_product.ram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RAMService {
    private final RAMRepository ramRepository;

    @Autowired
    public RAMService(RAMRepository ramRepository) {
        this.ramRepository = ramRepository;
    }

    public List<RAM> findAll() {
        return ramRepository.findAll();
    }

    public Optional<RAM> findById(Long id) {
        return ramRepository.findById(id);
    }

    public RAM save(RAM ram) {
        return ramRepository.save(ram);
    }

    public void deleteById(Long id) {
        ramRepository.deleteById(id);
    }
}

