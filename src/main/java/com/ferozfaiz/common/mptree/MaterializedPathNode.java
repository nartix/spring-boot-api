//package com.ferozfaiz.common.mptree;
//
//
//import jakarta.persistence.*;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Feroz Faiz
// */
//
//@MappedSuperclass
//public abstract class MaterializedPathNode<T extends MaterializedPathNode<T>> {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String path;
//
//    @Column(nullable = false)
//    private Integer depth;
//
//    @Column(nullable = false)
//    private Integer numChildren = 0;
//
//    // Abstract method to be implemented by subclasses to specify the ordering of nodes
//    protected abstract List<String> getNodeOrder();
//
//    // Getters and setters...
//
//    // Method to add a root node
//    public static <T extends MaterializedPathNode<T>> T addRoot(EntityManager em, T node) {
//        node.setPath(node.generatePath(null, 0));
//        node.setDepth(0);
//        em.persist(node);
//        return node;
//    }
//
//    // Method to add a child node
//    public T addChild(EntityManager em, T child) {
//        child.setPath(child.generatePath(this.getPath(), this.getNumChildren()));
//        child.setDepth(this.getDepth() + 1);
//        em.persist(child);
//        this.setNumChildren(this.getNumChildren() + 1);
//        em.merge(this);
//        return child;
//    }
//
//    // Method to add a sibling node
//    public T addSibling(EntityManager em, T sibling) {
//        String parentPath = this.getParentPath();
//        int siblingIndex = Integer.parseInt(this.getPath().substring(parentPath.length())) + 1;
//        sibling.setPath(sibling.generatePath(parentPath, siblingIndex));
//        sibling.setDepth(this.getDepth());
//        em.persist(sibling);
//        T parent = this.getParent(em);
//        if (parent != null) {
//            parent.setNumChildren(parent.getNumChildren() + 1);
//            em.merge(parent);
//        }
//        return sibling;
//    }
//
//    // Helper method to generate the path
//    private String generatePath(String parentPath, int index) {
//        String step = String.format("%04d", index);
//        return (parentPath == null ? "" : parentPath) + step;
//    }
//
//    // Helper method to get the parent path
//    private String getParentPath() {
//        return this.path.substring(0, this.path.length() - 4);
//    }
//
//    // Helper method to retrieve the parent node
//    private T getParent(EntityManager em) {
//        String parentPath = getParentPath();
//        if (parentPath.isEmpty()) {
//            return null; // This is a root node
//        }
//        TypedQuery<T> query = (TypedQuery<T>) em.createQuery(
//                "SELECT n FROM " + this.getClass().getSimpleName() + " n WHERE n.path = :path"
//        );
//        query.setParameter("path", parentPath);
//        return query.getSingleResult();
//    }
//
//    // Method to retrieve the tree structure
//    public static <T extends MaterializedPathNode<T>> List<T> getTree(EntityManager em, Class<T> clazz) {
//        TypedQuery<T> query = em.createQuery(
//                "SELECT n FROM " + clazz.getSimpleName() + " n ORDER BY n.path", clazz
//        );
//        return query.getResultList();
//    }
//
//    // Method to retrieve an annotated list of nodes
//    public static <T extends MaterializedPathNode<T>> List<AnnotatedNode<T>> getAnnotatedList(EntityManager em, Class<T> clazz) {
//        List<T> nodes = getTree(em, clazz);
//        List<AnnotatedNode<T>> annotatedList = new ArrayList<>();
//        int[] levelCounter = new int[10]; // Adjust size as needed
//        for (T node : nodes) {
//            int level = node.getDepth();
//            levelCounter[level]++;
//            List<Integer> close = new ArrayList<>();
//            for (int i = level + 1; i < levelCounter.length; i++) {
//                if (levelCounter[i] > 0) {
//                    close.add(i);
//                    levelCounter[i] = 0;
//                }
//            }
//            annotatedList.add(new AnnotatedNode<>(node, level, close));
//        }
//        return annotatedList;
//    }
//
//    // Inner class to represent an annotated node
//    public static class AnnotatedNode<T> {
//        private final T node;
//        private final int level;
//        private final List<Integer> close;
//
//        public AnnotatedNode(T node, int level, List<Integer> close) {
//            this.node = node;
//            this.level = level;
//            this.close = close;
//        }
//
//
//        // Getters...
//    }
//}
