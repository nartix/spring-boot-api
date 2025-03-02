package com.ferozfaiz.common.mptree;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Feroz Faiz
 */

@NoRepositoryBean
public interface MPNodeRepository<T extends MPNode<T>, ID extends Serializable>
        extends JpaRepository<T, ID> {
    List<T> findByPathStartingWith(String prefix);
}
