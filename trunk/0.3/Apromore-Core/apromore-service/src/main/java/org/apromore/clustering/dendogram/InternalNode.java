package org.apromore.clustering.dendogram;

import java.util.LinkedList;

public class InternalNode extends LeafNode {
    private Node first, second;
    private double proximity;

    public InternalNode(int index, Node first, Node second, double proximity) {
        super(index);
        this.first = first;
        this.second = second;
        this.proximity = proximity;
        this.children = new LinkedList<Integer>(first.getChildren());
        this.children.addAll(second.getChildren());
    }

    public Node getFirst() {
        return first;
    }

    public Node getSecond() {
        return second;
    }

    public double getProximity() {
        return proximity;
    }

    public String toString() {
        return String.format("Internal: %f ", proximity) + children;
    }
}
