package graphs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Stack;

public class ServiceGraph {

    DendroGraph graph;
    List<DendroNode> services;

    public ServiceGraph(String projectPath) throws IOException {
        this.graph = new DendroGraph(projectPath);
        create();
    }

    private void create() {
        List<DendroNode> services = new ArrayList<>();
        Stack<DendroNode> stack = new Stack<>();
        stack.push(graph.root);

        while (!stack.isEmpty()) {
            DendroNode parent = stack.pop();
            DendroNode leftChild = parent.left;
            DendroNode rightChild = parent.right;


            if (getInnerCoupling(parent) > getCoupling(leftChild, rightChild)) {
                services.add(parent);
            } else {
                if (leftChild != null) stack.push(leftChild);
                if (rightChild != null) stack.push(rightChild);
            }
        }
        this.services = services;
    }

    private double getInnerCoupling(DendroNode node) {
        if (node.right != null && node.left != null) return node.right.getCoupling(node.left);
        return 0;
    }

    private double getCoupling(DendroNode left, DendroNode right) {
        double acc = 0;
        if (left != null) {
            acc += getInnerCoupling(left);
        }

        if (right != null) {
            acc += getInnerCoupling(right);
        }

        return acc / 4;
    }

    public void logServices() {
        int i = 1;
        for (DendroNode service :
                services) {
            System.out.println("Service " + i + " : " + service.toString());
            i++;
        }
    }
}
