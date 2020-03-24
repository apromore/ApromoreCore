package org.apromore.processmining.models.graphbased.directed.bpmn;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.EdgeID;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;

public abstract class BPMNEdge<S extends BPMNNode, T extends BPMNNode> extends AbstractDirectedGraphEdge<S, T>
		implements ContainableDirectedGraphElement {

	private static final String NO_LABEL = "no label";
	
	private final EdgeID id = new EdgeID();
	private ContainingDirectedGraphNode parent;

	public BPMNEdge(S source, T target) {
		super(source, target);
	}

	public BPMNEdge(S source, T target, ContainingDirectedGraphNode parent) {
		this(source, target);
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	public int compareTo(BPMNEdge<S, T> edge) {
		return edge.id.compareTo(id);
	}

	public int hashCode() {
		// Hashcode not based on source and target, which
		// respects contract that this.equals(o) implies
		// this.hashCode()==o.hashCode()
		return id.hashCode();
	}

	public boolean equals(Object o) {
		if (!(this.getClass().equals(o.getClass()))) {
			return false;
		}
		BPMNEdge<?, ?> edge = (BPMNEdge<?, ?>) o;

		return edge.id.equals(id);
	}

	public ContainingDirectedGraphNode getParent() {
		return parent;
	}
	
	public void setParent(ContainingDirectedGraphNode node) {
		this.parent=node;
	}
	
	public EdgeID getEdgeID() {
		return id;
	}
	
	@Override
	public String getLabel() {
		String label = super.getLabel();
		return NO_LABEL.equals(label)? "" : label;
	}
	
	public void setLabel(String newLabel) {
		getAttributeMap().put(AttributeMap.LABEL, newLabel);
		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
	}
}
