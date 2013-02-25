package org.apromore.clustering.algorithm.hac.dendogram;

import java.util.LinkedList;
import java.util.List;

public class LeafNode implements Node {

    private Integer index;
    private Integer value;

    protected List<Integer> children;

    public LeafNode(int index) {
        this.index = index;
        this.children = new LinkedList<Integer>();
        this.value = null;
    }

    public LeafNode(int index, Integer value) {
        this.index = index;
        this.children = new LinkedList<Integer>();
        this.children.add(value);
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public Node getFirst() {
        return null;
    }

    public Node getSecond() {
        return null;
    }

    public String toString() {
        return String.format("Leaf (%s): %s", value, children);
    }

    public List<Integer> getChildren() {
        return children;
    }
}
