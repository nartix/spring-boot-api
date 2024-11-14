package com.ferozfaiz.security.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "session-attributes", path = "session-attributes")
public interface SpringSessionAttributeRepository extends JpaRepository<SpringSessionAttribute, SpringSessionAttributeId> {
}
