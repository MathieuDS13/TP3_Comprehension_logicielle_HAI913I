package graphs;

import utility.DrawableNode;

import java.util.*;

public class DendroClusterNode extends DendroNode {

    public DendroClusterNode(DendroNode left, DendroNode right, DendroGraph graph) {
        super(graph);
        this.left = left;
        this.right = right;
    }

    @Override
    public double getCoupling(DendroNode other) {
        return (left.getCoupling(other) + right.getCoupling(other)) / 2;
    }

    @Override
    public List<DendroLeafNode> getLeaves() {
        List<DendroLeafNode> leaves = new ArrayList<>();
        leaves.addAll(left.getLeaves());
        leaves.addAll(right.getLeaves());
        return leaves;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(left.toString());
        builder.append(" | ");
        builder.append(right.toString());
        return builder.toString();
    }

    @Override
    public DrawableNode getLeft() {
        return left;
    }

    @Override
    public DrawableNode getRight() {
        return right;
    }

    @Override
    public String getText() {
        return "";
    }
}
