package com.ferozfaiz.cti_product.harddrive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HardDriveService {
    private final HardDriveRepository hardDriveRepository;

    @Autowired
    public HardDriveService(HardDriveRepository hardDriveRepository) {
        this.hardDriveRepository = hardDriveRepository;
    }

    public List<HardDrive> findAll() {
        return hardDriveRepository.findAll();
    }

    public Optional<HardDrive> findById(Long id) {
        return hardDriveRepository.findById(id);
    }

    public HardDrive save(HardDrive hardDrive) {
        return hardDriveRepository.save(hardDrive);
    }

    public void deleteById(Long id) {
        hardDriveRepository.deleteById(id);
    }
}

