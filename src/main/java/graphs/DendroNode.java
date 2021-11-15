package graphs;

import utility.DrawableNode;

import java.util.List;

public abstract class DendroNode extends DrawableNode {

    DendroGraph couplingGraph;
    DendroNode left;
    DendroNode right;

    public DendroNode(DendroGraph couplingGraph) {
        this.couplingGraph = couplingGraph;
    }

    public abstract double getCoupling(DendroNode other);

    public abstract List<DendroLeafNode> getLeaves();

    public abstract String toString();
    //public abstract void appendStringRepresentation(StringBuilder builder);

}
