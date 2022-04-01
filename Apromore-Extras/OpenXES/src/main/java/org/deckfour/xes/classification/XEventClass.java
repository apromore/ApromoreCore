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
/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.classification;


/**
 * Implements an event class. An event class is an identity
 * for events, making them comparable. If two events are part
 * of the same class, they are considered to be equal, i.e. to
 * be referring to the same higher-level concept.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XEventClass implements Comparable<XEventClass> {
	
	/**
	 * Unique index of class.
	 */
	protected int index;
	/**
	 * Unique identification string of class.
	 */
	protected String id;
	/**
	 * Size of class, i.e. number of represented instances.
	 */
	protected int size;
	
	/**
	 * Creates a new event class instance.
	 * 
	 * @param id Unique identification string of the class, i.e. its name.
	 * @param index Unique index of this event class.
	 */
	public XEventClass(String id, int index) {
		this.id = id;
		this.index = index;
		this.size = 0;
	}
	
	/**
	 * Retrieves the name, i.e. unique identification string,
	 * of this event class.
	 * 
	 * @return The name of this class, as a unique string.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the index of this event class.
	 * @return Unique index.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Retrieves the size, i.e. the number of events represented by
	 * this event class.
	 * 
	 * @return Size of this class.
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Sets the size of this event class, i.e. the number of represented
	 * instances.
	 * 
	 * @param size Number of events in this class.
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * Increments the size of this class by one, i.e. adds another event 
	 * to the number of represented instances.
	 */
	public void incrementSize() {
		this.size++;
	}

	public boolean equals(Object o) {
		if (this == o) {
            return true;
        }
		if(o instanceof XEventClass) {
			return this.id.equals(((XEventClass)o).id);
		} else {
			return false;
		}
	}
	
	public int hashCode() {
		return this.id.hashCode();
	}
	
	public String toString() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(XEventClass o) {
		return this.id.compareTo(o.getId());
	}

}
