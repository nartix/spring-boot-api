package com.ferozfaiz.common.tree.materializedpath;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author Feroz Faiz
 */
@NoRepositoryBean
public interface MaterializedPathRepository<T extends MaterializedPathNode<T>, ID>
        extends JpaRepository<T, ID> {
    Optional<T> findByPath(String path);

    List<T> findByPathStartingWith(String prefix);

    Optional<T> findTopByDepthOrderByPathDesc(Integer depth);

    Optional<T> findTopByPathStartingWithAndDepthOrderByPathDesc(String prefix, Integer depth);

    void deleteByPathStartingWith(String path);

//    @Query("UPDATE #{#entityName} n SET " +
//            "n.path = CONCAT(:newPath, SUBSTRING(n.path, :oldPathLength + 1)), " +
//            "n.depth = n.depth + :depthDiff " +
//            "WHERE n.path LIKE CONCAT(:oldPath, '%')")

    @Modifying
    @Query("""
            UPDATE #{#entityName} n SET 
            n.path = CONCAT(:newPath, SUBSTRING(n.path, :oldPathLength + 1)), 
            n.depth = n.depth + :depthDiff 
            WHERE n.path LIKE CONCAT(:oldPath, '%')
            """)
    void bulkUpdatePathsAndDepth(@Param("oldPath") String oldPath,
                                 @Param("newPath") String newPath,
                                 @Param("oldPathLength") int oldPathLength,
                                 @Param("depthDiff") int depthDiff,
                                 @Param("stepLength") int stepLength);

    @Modifying
    @Query("""
            UPDATE #{#entityName} n 
            SET n.numChild = CASE WHEN n.numChild > 0 THEN n.numChild - 1 ELSE 0 END
            WHERE n.path = :path
            """)
    void decrementNumChildByPath(@Param("path") String path);

    @Modifying
    @Query("UPDATE #{#entityName} n SET n.numChild = n.numChild + 1 WHERE n.path = :path")
    void incrementNumChildByPath(@Param("path") String path);

}