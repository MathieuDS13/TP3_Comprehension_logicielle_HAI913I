package graphs;

import loggers.ConsoleLogger;
import loggers.FileLogger;
import loggers.LogRequest;
import loggers.StandardLogRequestLevel;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DendroGraph {

    DendroNode root = null;
    CouplingGraph couplingGraph;
    FileLogger loggerChain;


    public DendroGraph(String projectPath) throws IOException {
        setLoggerChain();
        this.couplingGraph = StaticCouplingGraph.createCouplingGraph(projectPath);
        createDendro();
    }

    protected void setLoggerChain() {
        loggerChain = new FileLogger(StandardLogRequestLevel.DEBUG);
        loggerChain.setNextLogger(new ConsoleLogger(StandardLogRequestLevel.INFO));
    }

    private void createDendro() throws IOException {
        List<DendroNode> input = createNodes();

        while (input.size() > 1) {
            //System.out.println(input.size());
            DendroNode current = getNewNodeFromClosestNodes(input);
            DendroNode toRemove1, toRemove2;
            toRemove1 = current.left;
            toRemove2 = current.right;
            if (toRemove1 != null) input.remove(toRemove1);
            if (toRemove2 != null) input.remove(toRemove2);

            input.add(current);
        }
        if (input.size() == 1) {
            root = input.get(0);
        }
    }

    private DendroNode getNewNodeFromClosestNodes(List<DendroNode> nodes) throws IOException {
        if (nodes.size() == 1) return nodes.get(0);
        if (nodes.size() <= 0) return null;

        DendroNode first = nodes.get(0);
        DendroNode second = nodes.get(1);
        DendroNode current1, current2;
        double maxCoupling = first.getCoupling(second);
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i == j) continue;
                current1 = nodes.get(i);
                current2 = nodes.get(j);
                double newCoupling = current1.getCoupling(current2);
                if (newCoupling > maxCoupling) {
                    maxCoupling = newCoupling;
                    first = current1;
                    second = current2;
                }
            }
        }
        return new DendroClusterNode(first, second, this);
    }

    private List<DendroNode> createNodes() {
        List<DendroNode> nodes = new ArrayList<>();
        for (String className :
                couplingGraph.getClasses()) {
            nodes.add(new DendroLeafNode(className, this));
        }
        return nodes;
    }

    public double getCoupling(String firstClass, String secondClass) {
        return couplingGraph.getCoupling(firstClass, secondClass);
    }

    public String toString() {
        return root.toString();
    }

    public void log() {
        loggerChain.log(new LogRequest(this.toString(),
                StandardLogRequestLevel.DEBUG));
    }

    public String toString2() {
        StringBuilder builder = new StringBuilder();
        List<DendroNode> nodes = new ArrayList<>();
        nodes = createModuleList();
        for (DendroNode node :
                nodes) {
            builder.append("\n");
            //builder.append("([" +node.left.toString() + "] [" + node.right.toString() + "])");
            builder.append("[" + node.toString() + "]");
        }

        return builder.toString();
    }

    private List<DendroNode> createModuleList() {
        List<DendroNode> nodes = new ArrayList<>();
        visitNode(nodes, root);
        return nodes;
    }

    private void visitNode(List<DendroNode> nodes, DendroNode currentNode) {
        if (currentNode != null) {
            nodes.add(currentNode);
            visitNode(nodes, currentNode.right);
            visitNode(nodes, currentNode.left);
        }
    }

    public void print() throws IOException {
        root.printTree(new OutputStreamWriter(System.out));
    }

}
