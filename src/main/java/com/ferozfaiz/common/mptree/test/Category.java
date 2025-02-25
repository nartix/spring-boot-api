package com.ferozfaiz.common.mptree.test;

import com.ferozfaiz.common.mptree.MPNode;
import jakarta.persistence.*;

/**
 * @author Feroz Faiz
 */

@Entity
@Table(name = "category")
public class Category extends MPNode<Category> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category() {
    }
}
