package com.ferozfaiz.security.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn("entityManagerFactory")
public class PermissionConfig {

    @Autowired
    private PermissionRepository permissionRepository;

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            // Define the permissions
            String[] permissionNames = {"READ", "WRITE", "EDIT", "DELETE"};

            // Save the permissions to the database
            for (String permissionName : permissionNames) {
                if (permissionRepository.findByName(permissionName).isEmpty()) {
                    Permission permission = new Permission(permissionName);
                    permissionRepository.save(permission);
                }
            }
        };
    }
}