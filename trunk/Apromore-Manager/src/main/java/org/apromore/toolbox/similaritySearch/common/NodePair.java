package org.apromore.toolbox.similaritySearch.common;

import org.apromore.graph.canonical.CPFNode;

public class NodePair {

    CPFNode left;
    CPFNode right;
    boolean visited = false;
    double weight;
    public double ed;
    public double sem;
    public double syn;
    public double struct;
    public double parent;


    public NodePair(CPFNode left, CPFNode right) {
        this.left = left;
        this.right = right;
    }

    public NodePair(CPFNode left, CPFNode right, double weight) {
        this.left = left;
        this.right = right;
        this.weight = weight;
    }

    public NodePair(CPFNode first, CPFNode second, double weight, double ed, double sem, double syn, double struct, double parent) {
        left = first;
        right = second;
        this.weight = weight;
        this.ed = ed;
        this.sem = sem;
        this.syn = syn;
        this.struct = struct;
        this.parent = parent;
    }


    public CPFNode getLeft() {
        return left;
    }

    public CPFNode getRight() {
        return right;
    }

    public double getWeight() {
        return weight;
    }

}
