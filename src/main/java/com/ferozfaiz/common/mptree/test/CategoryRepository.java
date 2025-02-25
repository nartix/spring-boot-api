package com.ferozfaiz.common.mptree.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Feroz Faiz
 */

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}