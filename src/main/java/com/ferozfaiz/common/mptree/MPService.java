package com.ferozfaiz.common.mptree;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Feroz Faiz
 *
 */

@Service
@Transactional
public class MPService<T extends MPNode<T>> {

    @PersistenceContext
    private EntityManager em;

    // Define fixed segment length; must match MPNode.steplen
    private final int SEGMENT_LENGTH = 4;

    // Utility: convert an integer to a fixed-width string (padded with zeros)
    private String formatSegment(int number) {
        return String.format("%0" + SEGMENT_LENGTH + "d", number);
    }

    /**
     * Creates a new root node.
     * Generates a path using an initial fixed segment (e.g. "0001"),
     * sets depth to 1 and initializes child count.
     */
    public T addRoot(T node) {
        // Query existing root nodes (depth = 1) to get the highest path value.
        TypedQuery<String> query = em.createQuery(
                "SELECT n.path FROM " + node.getClass().getSimpleName() + " n WHERE n.depth = 1 ORDER BY n.path DESC",
                String.class);
        query.setMaxResults(1);
        List<String> result = query.getResultList();

        String newPath;
        if (result.isEmpty()) {
            newPath = formatSegment(1);
        } else {
            int lastValue = Integer.parseInt(result.get(0));
            newPath = formatSegment(lastValue + 1);
        }
        node.setPath(newPath);
        node.setDepth(1);
        node.setNumChild(0);
        em.persist(node);
        return node;
    }

    /**
     * Adds a new child node to the given parent.
     * Determines the next available child segment by examining parent's children,
     * concatenates parent's path with the new segment, sets depth, updates parent's child count, and persists.
     */
    public T addChild(T parent, T child) {
        String parentPath = parent.getPath();
        int childDepth = parent.getDepth() + 1;

        // Query to get the maximum child path for the given parent
        TypedQuery<String> query = em.createQuery(
                "SELECT n.path FROM " + child.getClass().getSimpleName() + " n WHERE n.path LIKE :pattern AND n.depth = :depth ORDER BY n.path DESC",
                String.class);
        query.setParameter("pattern", parentPath + "%");
        query.setParameter("depth", childDepth);
        query.setMaxResults(1);
        List<String> result = query.getResultList();

        int nextSegment;
        if (result.isEmpty()) {
            nextSegment = 1;
        } else {
            String lastChildPath = result.get(0);
            String lastSegment = lastChildPath.substring(lastChildPath.length() - SEGMENT_LENGTH);
            nextSegment = Integer.parseInt(lastSegment) + 1;
        }
        String childPath = parentPath + formatSegment(nextSegment);
        child.setPath(childPath);
        child.setDepth(childDepth);
        child.setNumChild(0);
        em.persist(child);

        // Update parent's child count
        parent.setNumChild(parent.getNumChild() + 1);
        em.merge(parent);
        return child;
    }

    /**
     * Adds a sibling node to the given node.
     * For non-root nodes, uses the parent's path to calculate the next available segment.
     */
    public T addSibling(T node, T sibling) {
        if (node.getDepth() == 1) {
            // Sibling for a root node is determined similarly to addRoot.
            TypedQuery<String> query = em.createQuery(
                    "SELECT n.path FROM " + node.getClass().getSimpleName() + " n WHERE n.depth = 1 ORDER BY n.path DESC",
                    String.class);
            query.setMaxResults(1);
            List<String> result = query.getResultList();
            String newPath;
            if (result.isEmpty()) {
                newPath = formatSegment(1);
            } else {
                int lastValue = Integer.parseInt(result.get(0));
                newPath = formatSegment(lastValue + 1);
            }
            sibling.setPath(newPath);
            sibling.setDepth(1);
            sibling.setNumChild(0);
            em.persist(sibling);
            return sibling;
        } else {
            // For non-root nodes, sibling has the same parent.
            T parent = (T) node.getParent(em, false);
            return addChild(parent, sibling);
        }
    }

    /**
     * Moves a node (and its subtree) to become a child of newParent.
     * Computes a new path based on newParent's path, updates depth for the node and its descendants,
     * and adjusts parent's child counts. All operations are executed atomically.
     */
    public void move(T node, T newParent) {
        // Prevent moving a node into one of its descendants.
        if (node.getPath().startsWith(newParent.getPath())) {
            throw new IllegalArgumentException("Cannot move a node to its descendant.");
        }
        String oldPath = node.getPath();
        int newDepth = newParent.getDepth() + 1;

        // Determine the next available child segment for the new parent.
        TypedQuery<String> query = em.createQuery(
                "SELECT n.path FROM " + node.getClass().getSimpleName() + " n WHERE n.path LIKE :pattern AND n.depth = :depth ORDER BY n.path DESC",
                String.class);
        query.setParameter("pattern", newParent.getPath() + "%");
        query.setParameter("depth", newDepth);
        query.setMaxResults(1);
        List<String> result = query.getResultList();
        int nextSegment;
        if (result.isEmpty()) {
            nextSegment = 1;
        } else {
            String lastChildPath = result.get(0);
            String lastSegment = lastChildPath.substring(lastChildPath.length() - SEGMENT_LENGTH);
            nextSegment = Integer.parseInt(lastSegment) + 1;
        }
        String newPathPrefix = newParent.getPath() + formatSegment(nextSegment);

        // Retrieve all nodes in the subtree (node and descendants)
        TypedQuery<T> subtreeQuery = em.createQuery(
                "SELECT n FROM " + node.getClass().getSimpleName() + " n WHERE n.path LIKE :pattern",
                (Class<T>) node.getClass());
        subtreeQuery.setParameter("pattern", oldPath + "%");
        List<T> subtree = subtreeQuery.getResultList();

        for (T n : subtree) {
            // Replace old path prefix with new path prefix
            String descendantSuffix = n.getPath().substring(oldPath.length());
            int depthDifference = n.getDepth() - node.getDepth();
            n.setPath(newPathPrefix + descendantSuffix);
            n.setDepth(newDepth + depthDifference);
            em.merge(n);
        }

        // Update parent's child counts
        T oldParent = (T) node.getParent(em, false);
        if (oldParent != null) {
            oldParent.setNumChild(oldParent.getNumChild() - 1);
            em.merge(oldParent);
        }
        newParent.setNumChild(newParent.getNumChild() + 1);
        em.merge(newParent);
    }

    /**
     * Deletes a node and all its descendants from the tree.
     * Also updates the parent's numChild count accordingly.
     */
    public void delete(T node) {
        String pattern = node.getPath() + "%";
        // Bulk delete all nodes whose path starts with the given node's path.
        em.createQuery("DELETE FROM " + node.getClass().getSimpleName() + " n WHERE n.path LIKE :pattern")
                .setParameter("pattern", pattern)
                .executeUpdate();

        // Update parent's child count
        T parent = (T) node.getParent(em, false);
        if (parent != null && parent.getNumChild() > 0) {
            parent.setNumChild(parent.getNumChild() - 1);
            em.merge(parent);
        }
    }
}

