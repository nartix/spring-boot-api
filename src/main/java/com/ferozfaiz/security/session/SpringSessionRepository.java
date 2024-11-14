package com.ferozfaiz.security.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "sessions", path = "sessions")
public interface SpringSessionRepository extends JpaRepository<SpringSession, String> {
}
