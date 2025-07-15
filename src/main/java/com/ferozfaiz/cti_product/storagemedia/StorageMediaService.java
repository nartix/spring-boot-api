package com.ferozfaiz.cti_product.storagemedia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StorageMediaService {
    private final StorageMediaRepository storageMediaRepository;

    @Autowired
    public StorageMediaService(StorageMediaRepository storageMediaRepository) {
        this.storageMediaRepository = storageMediaRepository;
    }

    public Optional<StorageMedia> findById(Long productId) {
        return storageMediaRepository.findById(productId);
    }

    public StorageMedia save(StorageMedia storageMedia) {
        return storageMediaRepository.save(storageMedia);
    }

    public void deleteById(Long productId) {
        storageMediaRepository.deleteById(productId);
    }
}

