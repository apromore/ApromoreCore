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

import java.util.Collections;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeCollection;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.util.XAttributeUtils;


/**
 * This class implements the abstract base class for strongly-typed attributes.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public abstract class XAttributeImpl implements XAttribute  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2570374546119649178L;
	
	/**
	 * Key, i.e. unique name, of this attribute. If the attribute is defined in
	 * an extension, its key will be prepended with the extension's defined
	 * prefix string.
	 */
	private final String key;
	/**
	 * The extension defining this attribute. May be <code>null</code>, if this
	 * attribute is not defined by an extension.
	 */
	private final XExtension extension;
	/**
	 * Map of meta-attributes, i.e. attributes of this attribute.
	 */
	private XAttributeMap attributes;

	/**
	 * Creates a new, empty attribute.
	 * 
	 * @param key
	 *            The key, i.e. unique name identifier, of this attribute.
	 */
	protected XAttributeImpl(String key) {
		this(key, null);
	}

	/**
	 * Creates a new attribute.
	 * 
	 * @param key
	 *            The key, i.e. unique name identifier, of this attribute.
	 * @param extension
	 *            The extension used for defining this attribute.
	 */
	protected XAttributeImpl(String key, XExtension extension) {
		this.key = key;
		this.extension = extension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.services.XAttribute#getKey()
	 */
	public String getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.services.XAttribute#getExtension()
	 */
	public XExtension getExtension() {
		return extension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.services.XAttribute#getAttributes()
	 */
	public XAttributeMap getAttributes() {
		// This is not thread-safe, but we don't give any thread safety guarantee anyway
		if (attributes == null) {
			this.attributes = new XAttributeMapLazyImpl<XAttributeMapImpl>(
					XAttributeMapImpl.class); // uses lazy implementation by default
		}
		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.model.services.XAttribute#setAttributes(org.deckfour.xes
	 * .model.XAttributeMap)
	 */
	public void setAttributes(XAttributeMap attributes) {
		this.attributes = attributes;
	}
	
	/* (non-Javadoc)
	 * @see XAttributable#hasAttributes()
	 */
	@Override
	public boolean hasAttributes() {
		return attributes != null && !attributes.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.services.XAttribute#getExtensions()
	 */
	public Set<XExtension> getExtensions() {
		if (attributes != null) {
			return XAttributeUtils.extractExtensions(getAttributes());	
		} else {
			return Collections.emptySet();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.services.XAttribute#clone()
	 */
	public Object clone() {
		XAttributeImpl clone = null;
		try {
			clone = (XAttributeImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		if (attributes != null) {
			clone.attributes = (XAttributeMap) getAttributes().clone();
		}
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof XAttribute) {
			XAttribute other = (XAttribute) obj;
			return other.getKey().equals(key);
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return key.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(XAttribute o) {
		return key.compareTo(o.getKey());
	}

	/*
	 * Runs the given visitor for the given parent on this attribute.
	 * 
	 * (non-Javadoc)
	 * @see XAttribute#accept(XVisitor, XAttributable)
	 */
	public void accept(XVisitor visitor, XAttributable parent) {
		/*
		 * First call.
		 */
		visitor.visitAttributePre(this, parent);
		if (this instanceof XAttributeCollection) {
			/*
			 * Visit the (meta) attributes using the order a specified by the collection.
			 */
			for (XAttribute attribute: ((XAttributeCollection) this).getCollection()) {
				attribute.accept(visitor, this);
			}
		} else {
			/*
			 * Visit the (meta) attributes.
			 */
			if (attributes != null) {
				for (XAttribute attribute: getAttributes().values()) {
					attribute.accept(visitor, this);
				}
			}
		}
		/*
		 * Last call.
		 */
		visitor.visitAttributePost(this, parent);
	}
}
