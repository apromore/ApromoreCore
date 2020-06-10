/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.processmining.models.graphbased;

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
