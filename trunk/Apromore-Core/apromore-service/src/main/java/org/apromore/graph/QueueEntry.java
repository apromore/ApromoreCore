package org.apromore.graph;

import org.apromore.graph.canonical.INode;

public class QueueEntry implements Comparable<QueueEntry> {

    private INode vertex;
    private String label;

    public QueueEntry(INode vertex, String label) {
        this.vertex = vertex;
        this.label = label;
    }

    public INode getVertex() {
        return vertex;
    }

    public String getLabel() {
        return label;
    }

    public String toString() {
        return "(" + vertex + ", " + label + ")";
    }


    @Override
    public int compareTo(QueueEntry theOther) {
        return this.label.compareTo(theOther.label);
    }

}
