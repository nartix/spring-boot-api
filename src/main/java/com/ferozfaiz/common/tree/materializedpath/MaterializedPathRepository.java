package com.ferozfaiz.common.tree.materializedpath;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * @author Feroz Faiz
 */
@NoRepositoryBean
public interface MaterializedPathRepository <T extends MaterializedPathNode<T>, ID>
        extends JpaRepository<T, ID> {
    List<T> findByPathStartingWith(String prefix);

    // Find the top node by depth and order by path in descending order
    Optional<T> findTopByDepthOrderByPathDesc(Integer depth);
}