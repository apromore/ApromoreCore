package org.processmining.models.graphbased;

import java.io.Serializable;
import java.util.UUID;

public class NodeID implements Comparable<NodeID>, Serializable {

	private static final long serialVersionUID = -6457455085857447745L;

	private final UUID id = UUID.randomUUID();

	public int compareTo(NodeID node) {
		return id.compareTo(node.id);
	}

	public String toString() {
		return "node " + id;
	}

	public boolean equals(Object o) {
		if (!(o instanceof NodeID)) {
			return false;
		}
		NodeID nodeID = (NodeID) o;
		return nodeID.id.equals(id);
	}

	public int hashCode() {
		return id.hashCode();
	}

}
