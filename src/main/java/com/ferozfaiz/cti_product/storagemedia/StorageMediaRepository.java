package com.ferozfaiz.cti_product.storagemedia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageMediaRepository extends JpaRepository<StorageMedia, Long> {
    // Custom queries if needed
}

