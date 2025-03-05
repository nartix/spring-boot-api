package com.ferozfaiz.common.tree.materializedpath;

import com.ferozfaiz.common.tree.util.PathUtil;

/**
 * @author Feroz Faiz
 */
public abstract class MaterializedPathService<T extends MaterializedPathNode<T>, ID > {
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

    public T addRoot(T node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        T lastNode = repository.findTopByDepthOrderByPathDesc(1).orElse(null);

        String newPath;
        if (lastNode == null) {
            newPath = pathUtil.getPath(null, 1, 1);
        } else {
            long newStep = pathUtil.strToInt(lastNode.getPath()) + 1;
            long maxStep = (long) Math.pow(pathUtil.getNumConv().getRadix(), pathUtil.getStepLength());
            if (newStep > maxStep) {
                throw new IllegalArgumentException("Path length exceeded: current path " + lastNode.getPath() + ", maximum allowed path length " + maxStep);
            }
            newPath = pathUtil.getPath(lastNode.getPath(), 1,  pathUtil.strToInt(lastNode.getPath()) + 1);
        }

        node.setPath(newPath);
        node.setDepth(1);
        node.setNumChild(0);
        repository.save(node);

        return node;
    }

    public T addChild(T parent, T child) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent cannot be null");
        }
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }

        long parentStep = pathUtil.strToInt(parent.getPath());
        long newStep = parentStep + 1;
        long maxStep = (long) Math.pow(pathUtil.getNumConv().getRadix(), pathUtil.getStepLength());
        if (newStep > maxStep) {
            throw new IllegalArgumentException("Path length exceeded: current path " + parent.getPath() + ", maximum allowed path length " + maxStep);
        }

        String newPath = pathUtil.getPath(parent.getPath(), parent.getDepth() + 1, newStep);
        child.setPath(newPath);
        child.setDepth(parent.getDepth() + 1);
        child.setNumChild(0);
        repository.save(child);

        parent.setNumChild(parent.getNumChild() + 1);
        repository.save(parent);

        return child;
    }
}
