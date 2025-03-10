package com.ferozfaiz.common.tree.materializedpath;

import com.ferozfaiz.common.tree.util.PathUtil;
import jakarta.transaction.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Feroz Faiz
 */
public abstract class MaterializedPathService<T extends MaterializedPathNode<T>, ID> {
    private MaterializedPathRepository<T, ID> repository;
    private PathUtil pathUtil;

    // Primary constructor that takes the repository along with other dependencies.
    public MaterializedPathService(MaterializedPathRepository<T, ID> repository, PathUtil pathUtil) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        if (pathUtil == null) {
            throw new IllegalArgumentException("PathUtil cannot be null");
        }
        this.repository = repository;
        this.pathUtil = pathUtil;
    }

    public MaterializedPathService(MaterializedPathRepository<T, ID> repository) {
        this(repository, new PathUtil());
    }

    public PathUtil getPathUtil() {
        return pathUtil;
    }

    @Transactional
    public T addRoot(T node) {
        Objects.requireNonNull(node, "addRoot error: Node cannot be null. A valid node is required.");

        T lastNode = repository.findTopByDepthOrderByPathDesc(1).orElse(null);

        String newPath;
        if (lastNode == null) {
            newPath = pathUtil.getPath(null, 1, 1);
        } else {
            long newStep = pathUtil.strToInt(lastNode.getPath()) + 1;
            if (newStep > pathUtil.getMaxStepLength()) {
                throw new IllegalArgumentException("Path length exceeded: current path " + lastNode.getPath() + ", maximum allowed path length " + pathUtil.getMaxStepLength());
            }
            newPath = pathUtil.getPath(lastNode.getPath(), 1, newStep);
        }

        node.setPath(newPath);
        node.setDepth(1);
        node.setNumChild(0);
        repository.save(node);

        return node;
    }

    @Transactional
    public T addChild(T parent, T child) {
        Objects.requireNonNull(parent, "addChild error: Parent node cannot be null. A valid parent is required.");
        Objects.requireNonNull(child, "addChild error: Child node cannot be null. A valid child is required.");

        String parentPath = parent.getPath();
        int childDepth = parent.getDepth() + 1;

        // Get the last child path as an Optional
        Optional<String> optionalLastChildPath = repository
                .findTopByPathStartingWithAndDepthOrderByPathDesc(parentPath, childDepth)
                .map(MaterializedPathNode::getPath);

        // Compute new step using the Optional
        long newStep = optionalLastChildPath
                .map(lastChildPath -> {
                    String lastDescendantPath = pathUtil.getPathByDepth(lastChildPath, childDepth);
                    return pathUtil.strToInt(lastDescendantPath) + 1;
                })
                .orElse(1L);

        if (newStep > pathUtil.getMaxStepLength()) {
            throw new IllegalArgumentException("Path length exceeded: current path " + parentPath + ", maximum allowed path length " + pathUtil.getMaxStepLength());
        }

        // Create the new path and update the child node
        String newPath = pathUtil.getPath(parentPath, childDepth, newStep);
        child.setPath(newPath);
        child.setDepth(childDepth);
        child.setNumChild(0);
        repository.save(child);

        // Update the parent node
        parent.setNumChild(parent.getNumChild() + 1);
        repository.save(parent);

        return child;
    }

    @Transactional
    public T addSibling(T node, T sibling) {
        Objects.requireNonNull(node, "addSibling error: Reference node cannot be null, a valid node is required.");
        Objects.requireNonNull(sibling, "addSibling error: Sibling node cannot be null, a valid sibling is required.");

        if (node.getDepth() == 1) {
            return addRoot(sibling);
        }
        String parentPath = pathUtil.getBasePath(node.getPath(), node.getDepth() - 1);
        T parent = repository.findByPath(parentPath)
                .orElseThrow(() -> new IllegalArgumentException("addSibling error: Parent not found for node with path "
                        + node.getPath() + ". Computed parent path: " + parentPath));
        return addChild(parent, sibling);
    }

    /**
     * Deletes the given node and all its descendants.
     */
    @Transactional
    public void delete(T node) {
        Objects.requireNonNull(node, "delete error: Node cannot be null. A valid node is required.");

        repository.deleteByPathStartingWith(node.getPath());

        if (node.getDepth() == 1) {
            return;
        }

        String parentPath = pathUtil.getBasePath(node.getPath(), node.getDepth() - 1);
        T parent = repository.findByPath(parentPath)
                .orElseThrow(() -> new IllegalArgumentException("delete error: Parent not found for node with path "
                        + node.getPath() + ". Computed parent path: " + parentPath));
        if (parent.getNumChild() > 0) {
            parent.setNumChild(parent.getNumChild() - 1);
            repository.save(parent);
        }
    }

    // working but not efficient
//    @Transactional
//    public void move(T node, T newParent) {
//        Objects.requireNonNull(node, "move error: Node cannot be null. A valid node is required.");
//        Objects.requireNonNull(newParent, "move error: New parent cannot be null. A valid new parent is required.");
//
//        if (pathUtil.isAncestor(node.getPath(), newParent.getPath())) {
//            throw new IllegalArgumentException("move error: Cannot move node with path " + node.getPath() + " to its descendant with path " + newParent.getPath() + ".");
//        }
//
//        String oldPath = node.getPath();
//        String newParentPath = newParent.getPath();
//        int newDepth = newParent.getDepth() + 1;
//
//        // Compute new path for the node under the new parent
//        Optional<T> lastSibling = repository.findTopByPathStartingWithAndDepthOrderByPathDesc(newParentPath, newDepth);
//        long newStep = lastSibling
//                .map(sibling -> pathUtil.strToInt(pathUtil.getPathByDepth(sibling.getPath(), newDepth)) + 1)
//                .orElse(1L);
//
//        String newPath = pathUtil.getPath(newParentPath, newDepth, newStep);
//        int pathDiff = newPath.length() - oldPath.length();
//
//        // Fetch all descendants of the node being moved
//        List<T> descendants = repository.findByPathStartingWith(oldPath);
//
//        // Update path and depth of node and descendants
//        for (T descendant : descendants) {
//            String descendantOldPath = descendant.getPath();
//            String descendantNewPath = newPath + descendantOldPath.substring(oldPath.length());
//
//            descendant.setPath(descendantNewPath);
//            descendant.setDepth(newDepth + ((descendantOldPath.length() - oldPath.length()) / pathUtil.getStepLength()));
//            repository.save(descendant);
//        }
//
//        // Update old parent's numChild
//        if (node.getDepth() > 1) {
//            String oldParentPath = pathUtil.getBasePath(oldPath, node.getDepth() - 1);
//            repository.findByPath(oldParentPath).ifPresent(oldParent -> {
//                oldParent.setNumChild(Math.max(0, oldParent.getNumChild() - 1));
//                repository.save(oldParent);
//            });
//        }
//
//        // Update new parent's numChild
//        newParent.setNumChild(newParent.getNumChild() + 1);
//        repository.save(newParent);
//    }

    /**
     * Moves the given node to a new parent node.
     * DataIntegrityViolationException is thrown if path length exceeds the maximum allowed path length.
     */
    @Transactional
    public void move(T node, T newParent) {
        Objects.requireNonNull(node, "move error: Node cannot be null. A valid node is required.");
        Objects.requireNonNull(newParent, "move error: New parent cannot be null. A valid new parent is required.");

        if (pathUtil.isAncestor(node.getPath(), newParent.getPath())) {
            throw new IllegalArgumentException("move error: Cannot move node with path " + node.getPath() + " to its descendant with path " + newParent.getPath() + ".");
        }

        String oldPath = node.getPath();
        String newParentPath = newParent.getPath();
        int newDepth = newParent.getDepth() + 1;

        // Compute new path for the node
        Optional<T> lastSibling = repository.findTopByPathStartingWithAndDepthOrderByPathDesc(newParentPath, newDepth);
        long newStep = lastSibling
                .map(sibling -> pathUtil.strToInt(pathUtil.getPathByDepth(sibling.getPath(), newDepth)) + 1)
                .orElse(1L);

        String newPath = pathUtil.getPath(newParentPath, newDepth, newStep);

        // Compute difference in length to calculate new depth
        int depthDiff = newDepth - node.getDepth();
        int oldPathLength = oldPath.length();

        // Perform bulk update for paths and depths in a single query
        repository.bulkUpdatePathsAndDepth(oldPath, newPath, oldPathLength, depthDiff, pathUtil.getStepLength());

        // Update numChild counts of parents
        updateParentChildCountsAfterMove(node, newParent);
    }

    // Helper to update numChild counts
    private void updateParentChildCountsAfterMove(T node, T newParent) {
        // Decrement old parent numChild
        if (node.getDepth() > 1) {
            String oldParentPath = pathUtil.getBasePath(node.getPath(), node.getDepth() - 1);
            repository.decrementNumChildByPath(oldParentPath);
        }

        // Increment new parent numChild
        repository.incrementNumChildByPath(newParent.getPath());
    }

    public int getPathColumnLength() {
        return MaterializedPathNode.getPathColumnLength();
    }

}
