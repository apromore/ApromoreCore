/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

public class EdgeID implements Comparable<EdgeID>, Serializable {

	private static final long serialVersionUID = -6457455085857447745L;

	private String id = "";

	public EdgeID() {
		this.id = UUID.randomUUID().toString();
	}

	public EdgeID(String id) {
		if (id == null || id.isEmpty()) throw new IllegalArgumentException("ID can't be null or empty");
		this.id = id;
	}

	@Override
    public int compareTo(EdgeID edge) {
		return id.compareTo(edge.id);
	}
	@Override
    public String toString() {
		return id;
	}

	@Override
    public boolean equals(Object o) {
		if (!(o instanceof EdgeID)) {
			return false;
		}
		EdgeID edgeID = (EdgeID) o;
		return edgeID.id.equals(id);
	}

	@Override
    public int hashCode() {
		return id.hashCode();
	}

}
