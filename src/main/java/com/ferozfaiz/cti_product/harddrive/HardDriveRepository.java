package com.ferozfaiz.cti_product.harddrive;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HardDriveRepository extends JpaRepository<HardDrive, Long> {
    // Custom queries if needed
}

