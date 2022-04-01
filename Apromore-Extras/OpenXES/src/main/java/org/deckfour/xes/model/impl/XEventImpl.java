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
package org.deckfour.xes.model.impl;

import java.util.Set;

import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.util.XAttributeUtils;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;

/**
 * Implementation for the XEvent interface.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XEventImpl implements XEvent {

	/**
	 * ID of this event.
	 */
	private XID id;

	/**
	 * Map of attributes for this event.
	 */
	private XAttributeMap attributes;

	/**
	 * Creates a new event.
	 */
	public XEventImpl() {
		this(XIDFactory.instance().createId(), new XAttributeMapImpl());
	}

	/**
	 * Creates a new event with a given ID.
	 * 
	 * @param id
	 *            the id for this event
	 */
	public XEventImpl(XID id) {
		this(id, new XAttributeMapImpl());
	}

	/**
	 * Creates a new event.
	 * 
	 * @param attributes
	 *            Map of attribute for the event.
	 */
	public XEventImpl(XAttributeMap attributes) {
		this(XIDFactory.instance().createId(), attributes);
	}

	/**
	 * Creates a new event with the given id and attributed
	 * 
	 * @param id
	 *            the id for this event
	 * @param attributes
	 *            Map of attribute for the event.
	 */
	public XEventImpl(XID id, XAttributeMap attributes) {
		this.id = id;
		this.attributes = attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see XAttributable#getAttributes()
	 */
	public XAttributeMap getAttributes() {
		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see XAttributable#setAttributes(java.util.Map)
	 */
	public void setAttributes(XAttributeMap attributes) {
		this.attributes = attributes;
	}

	/* (non-Javadoc)
	 * @see XAttributable#hasAttributes()
	 */
	@Override
	public boolean hasAttributes() {
		return !attributes.isEmpty();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see XAttributable#getExtensions()
	 */
	public Set<XExtension> getExtensions() {
		return XAttributeUtils.extractExtensions(attributes);
	}

	/**
	 * Clones this event, i.e. creates a deep copy, but with a new ID, so equals
	 * does not hold between this and the clone
	 */
	public Object clone() {
		XEventImpl clone;
		try {
			clone = (XEventImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		clone.id = XIDFactory.instance().createId();
		clone.attributes = (XAttributeMap) attributes.clone();
		return clone;
	}

	/**
	 * Tests for equality of IDs
	 */
	public boolean equals(Object o) {
		if (o instanceof XEventImpl) {
			return ((XEventImpl) o).id.equals(id);
		} else {
			return false;
		}
	}

	/**
	 * Returns the hashCode of the id
	 */
	public int hashCode() {
		return id.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see XEvent#getID()
	 */
	public XID getID() {
		return id;
	}

	/**
	 * Sets the ID. Should only be used for deserialization purposes
	 * 
	 * @param id
	 *            the new id.
	 */
	public void setID(XID id) {
		this.id = id;
	}

	/*
	 * Runs the given visitor for the given trace on this event.
	 * 
	 * (non-Javadoc)
	 * @see XEvent#accept(XVisitor, XTrace)
	 */
	public void accept(XVisitor visitor, XTrace trace) {
		/*
		 * First call.
		 */
		visitor.visitEventPre(this, trace);
		/*
		 * Visit the attributes.
		 */
		for (XAttribute attribute: attributes.values()) {
			attribute.accept(visitor, this);
		}
		/*
		 * Last call.
		 */
		visitor.visitEventPost(this, trace);
	}

}
