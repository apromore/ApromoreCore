package org.processmining.models.graphbased;

import java.io.Serializable;
import java.util.UUID;

public class EdgeID implements Comparable<EdgeID>, Serializable {

	private static final long serialVersionUID = -6457455085857447745L;

	private final UUID id = UUID.randomUUID();

	public int compareTo(EdgeID edge) {
		return id.compareTo(edge.id);
	}

	public String toString() {
		return "node " + id;
	}

	public boolean equals(Object o) {
		if (!(o instanceof EdgeID)) {
			return false;
		}
		EdgeID edgeID = (EdgeID) o;
		return edgeID.id.equals(id);
	}

	public int hashCode() {
		return id.hashCode();
	}

}
