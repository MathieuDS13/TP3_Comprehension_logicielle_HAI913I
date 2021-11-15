package graphs;

import utility.DrawableNode;

import java.util.Arrays;
import java.util.List;

public class DendroLeafNode extends DendroNode {

    String className;

    public DendroLeafNode(String className, DendroGraph graph) {
        super(graph);
        this.className = className;
        this.left = null;
        this.right = null;
    }

    @Override
    public double getCoupling(DendroNode other) {
        double acc = 0;
        List<DendroLeafNode> nodes = other.getLeaves();
        for (DendroLeafNode leaf :
                nodes) {
            acc += couplingGraph.getCoupling(className, leaf.className);
        }
        return acc / nodes.size();
    }

    @Override
    public List<DendroLeafNode> getLeaves() {
        List<DendroLeafNode> leaves = Arrays.asList(this);
        return leaves;
    }

    public String toString() {
        return className;
    }

    @Override
    public DrawableNode getLeft() {
        return null;
    }

    @Override
    public DrawableNode getRight() {
        return null;
    }

    @Override
    public String getText() {
        return className;
    }
}
