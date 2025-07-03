package com.ferozfaiz.security.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "users")
@Validated
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {User.USER_ROLES_ROLE})
    Optional<User> findByUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {User.USER_ROLES_ROLE})
    Optional<User> findByUsernameIgnoreCase(@Param("username") String username);

    @EntityGraph(attributePaths = {User.USER_ROLES_ROLE})
    Optional<User> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = {User.USER_ROLES_ROLE})
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    @EntityGraph(attributePaths = {User.USER_ROLES_ROLE})
    Optional<User> findById(@Param("id") Long id);

    @EntityGraph(attributePaths = {User.USER_ROLES_ROLE})
    List<User> findAll();

    @EntityGraph(attributePaths = {User.USER_ROLES_ROLE})
    boolean existsByUsernameIgnoreCase(@Param("username") String username);
}