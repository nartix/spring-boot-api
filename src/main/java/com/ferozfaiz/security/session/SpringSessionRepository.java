package com.ferozfaiz.security.session;

import com.ferozfaiz.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "sessions", path = "sessions")
public interface SpringSessionRepository extends JpaRepository<SpringSession, String> {
    Optional<SpringSession> findBySessionId(@Param("sessionId") String sessionId);
}
