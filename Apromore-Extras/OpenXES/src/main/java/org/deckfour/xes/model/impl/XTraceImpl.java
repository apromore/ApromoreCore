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

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.util.XAttributeUtils;

/**
 * Memory-based implementation for the XTrace interface.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XTraceImpl extends ArrayList<XEvent> implements XTrace {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 843122019760036963L;

	/**
	 * Map of attributes for this trace.
	 */
	private XAttributeMap attributes;

	public XTraceImpl() {}

	/**
	 * Creates a new trace.
	 * 
	 * @param attributeMap
	 *            Attribute map used to store this trace's attributes.
	 */
	public XTraceImpl(XAttributeMap attributeMap) {
		this.attributes = attributeMap;
	}
	
	/**
	 * Create a new trace with specified capacity
	 * 
	 * @param attributeMap
	 * @param initialCapacity
	 */
	private XTraceImpl(XAttributeMap attributeMap, int initialCapacity) {
		super(initialCapacity);
		this.attributes = attributeMap;
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
	 * @see XAttributable#getExtensions()
	 */
	public Set<XExtension> getExtensions() {
		return XAttributeUtils.extractExtensions(attributes);
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

	/**
	 * Creates a clone, i.e. deep copy, of this trace.
	 */
	public Object clone() {
		XTraceImpl clone = new XTraceImpl((XAttributeMap) attributes.clone(), size());
		for (XEvent event : this) {
			clone.add((XEvent) event.clone());
		}
		return clone;
	}

	public synchronized int insertOrdered(XEvent event) {
		if (this.size() == 0) {
			// append if list is empty
			add(event);
			return 0;
		}
		XAttribute insTsAttr = event.getAttributes().get(
				XTimeExtension.KEY_TIMESTAMP);
		if (insTsAttr == null) {
			// append if event has no timestamp
			add(event);
			return (size() - 1);
		}
		Date insTs = ((XAttributeTimestamp) insTsAttr).getValue();
		for (int i = (size() - 1); i >= 0; i--) {
			XAttribute refTsAttr = get(i).getAttributes().get(
					XTimeExtension.KEY_TIMESTAMP);
			if (refTsAttr == null) {
				// trace contains events w/o timestamps, append.
				add(event);
				return (size() - 1);
			}
			Date refTs = ((XAttributeTimestamp) refTsAttr).getValue();
			if (insTs.before(refTs) == false) {
				// insert position reached
				add(i + 1, event);
				return (i + 1);
			}
		}
		// beginning reached, insert at head
		add(0, event);
		return 0;
	}

	/*
	 * Runs the given visitor for the given log on this trace.
	 * 
	 * (non-Javadoc)
	 * @see XTrace#accept(XVisitor, XLog)
	 */
	public void accept(XVisitor visitor, XLog log) {
		/*
		 * First call.
		 */
		visitor.visitTracePre(this, log);
		/*
		 * Visit the attributes.
		 */
		for (XAttribute attribute: attributes.values()) {
			attribute.accept(visitor, this);
		}
		/*
		 * Visit the events.
		 */
		for (XEvent event: this) {
			event.accept(visitor, this);
		}
		/*
		 * Last call.
		 */
		visitor.visitTracePost(this, log);
	}
}
