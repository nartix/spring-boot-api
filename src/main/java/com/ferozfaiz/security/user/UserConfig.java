// UserConfig.java
package com.ferozfaiz.security.user;

import com.ferozfaiz.security.role.Role;
import com.ferozfaiz.security.role.RoleRepository;
import com.ferozfaiz.security.user.roles.UserRoleId;
import com.ferozfaiz.security.user.roles.UserRoles;
import com.ferozfaiz.security.user.roles.UserRolesRepository;
import com.ferozfaiz.security.role.RoleConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

@Configuration
@AutoConfigureAfter(RoleConfig.class)
@DependsOn("entityManagerFactory")
public class UserConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @Bean(name = "userCommandLineRunner")
    CommandLineRunner commandLineRunner() {
        return args -> {
            // Check if the user already exists by email or username
            Optional<User> existingUser = userRepository.findByEmail("feroz@hotmail.ca");
            if (existingUser.isPresent()) {
                System.out.println("User with email 'feroz@hotmail.ca' already exists.");
                return;
            }

            // Find the roles using Optional
            Optional<Role> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
            Optional<Role> userRoleOptional = roleRepository.findByName("ROLE_USER");

            // Check if the roles are present
            Role adminRole = adminRoleOptional.orElseThrow(() ->
                    new IllegalStateException("Role 'ROLE_ADMIN' not found in the database")
            );
            Role userRole = userRoleOptional.orElseThrow(() ->
                    new IllegalStateException("Role 'ROLE_USER' not found in the database")
            );

            // Encode the password
            String encodedPassword = passwordEncoder.encode("password");

            // Create and save the user
            User user = new User(
                    null,
                    "feroz",
                    encodedPassword,
                    "feroz@hotmail.ca",
                    "Feroz",
                    "Faiz",
                    true,
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
            userRepository.save(user);

            // Create and save the user roles for ROLE_ADMIN
            UserRoles adminUserRoles = new UserRoles();
            adminUserRoles.setId(new UserRoleId(user.getId(), adminRole.getId()));
            adminUserRoles.setUser(user);
            adminUserRoles.setRole(adminRole);
            userRolesRepository.save(adminUserRoles);

            // Create and save the user roles for ROLE_USER
            UserRoles userUserRoles = new UserRoles();
            userUserRoles.setId(new UserRoleId(user.getId(), userRole.getId()));
            userUserRoles.setUser(user);
            userUserRoles.setRole(userRole);
            userRolesRepository.save(userUserRoles);
        };
    }
}