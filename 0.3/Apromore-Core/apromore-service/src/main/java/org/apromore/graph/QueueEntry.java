package org.apromore.graph;

import org.jbpt.hypergraph.abs.IVertex;

public class QueueEntry implements Comparable<QueueEntry> {

    private IVertex vertex;
    private String label;
	
	public QueueEntry(IVertex vertex, String label) {
		this.vertex = vertex;
		this.label = label;
	}

	public IVertex getVertex() {
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
