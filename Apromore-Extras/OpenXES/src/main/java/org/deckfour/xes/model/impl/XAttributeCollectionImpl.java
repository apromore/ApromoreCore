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
 * Copyright (c) 2014 Christian W. Guenther (christian@deckfour.org)
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

import java.util.Collection;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeCollection;

/**
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public abstract class XAttributeCollectionImpl extends XAttributeLiteralImpl
		implements XAttributeCollection {

	/**
     * 
     */
	private static final long serialVersionUID = 4322597532345796274L;

	protected Collection<XAttribute> collection;

	/**
	 * @param key
	 */
	public XAttributeCollectionImpl(String key) {
		super(key, "", null);
	}

	/**
	 * @param key
	 * @param extension
	 */
	public XAttributeCollectionImpl(String key, XExtension extension) {
		super(key, "", extension);
	}



	public void addToCollection(XAttribute attribute) {
		if (collection != null) {
			collection.add(attribute);
		} else {
			throw new NullPointerException("Cannot add attribute to collection that is null");
		}
	}

	public void removeFromCollection(XAttribute attribute) {
		if (collection != null) {
			collection.remove(attribute);
		}
	}

	public Collection<XAttribute> getCollection() {
		return collection != null ? collection : getAttributes().values();
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		String sep = "[";
		for (XAttribute attribute : getCollection()) {
			buf.append(sep);
			sep = ",";
			buf.append(attribute.getKey());
			buf.append(":");
			buf.append(attribute.toString());
		}
		if (buf.length() == 0) {
			buf.append("[");
		}
		buf.append("]");
		return buf.toString();
	}

	@Override
	public Object clone() {
		XAttributeCollectionImpl clone = (XAttributeCollectionImpl) super
				.clone();
		// The collection is cloned in the child class (XAttributeListImpl and
		// XAttributeContainerImpl)
		return clone;
	}
}
