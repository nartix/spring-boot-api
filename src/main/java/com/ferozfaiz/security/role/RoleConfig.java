package com.ferozfaiz.security.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

@Configuration
@DependsOn({"entityManagerFactory", "liquibase"})
public class RoleConfig {

    @Autowired
    private RoleRepository roleRepository;

    @Bean(name = "roleCommandLineRunner")
    @Order(1)
    CommandLineRunner commandLineRunner() {
        return args -> {
            insertRoleIfNotExists("ROLE_USER");
            insertRoleIfNotExists("ROLE_ADMIN");
        };
    }

    private void insertRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        });
    }
}