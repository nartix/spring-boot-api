package com.ferozfaiz.security.session;

import com.ferozfaiz.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "sessions", path = "sessions")
public interface SpringSessionRepository extends JpaRepository<SpringSession, String> {
    Optional<SpringSession> findBySessionId(@Param("sessionId") String sessionId);

    // todo: only allow DELETE requests
    @Modifying
    @Transactional
    @Query("DELETE FROM SpringSession s WHERE s.sessionId = :sessionId")
    @RestResource(path = "deleteBySessionId", rel = "deleteBySessionId", exported = true)
    void deleteBySessionId(@Param("sessionId") String sessionId);

}
