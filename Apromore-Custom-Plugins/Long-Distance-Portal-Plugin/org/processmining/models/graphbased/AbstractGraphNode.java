package org.processmining.models.graphbased;

public abstract class AbstractGraphNode extends AbstractGraphElement {

	private final NodeID id = new NodeID();

	public AbstractGraphNode() {
		super();
	}

	public int hashCode() {
		return getId().hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof AbstractGraphNode)) {
			return false;
		}
		AbstractGraphNode node = (AbstractGraphNode) o;
		return node.getId().equals(getId());
	}

	public NodeID getId() {
		return id;
	}

}
