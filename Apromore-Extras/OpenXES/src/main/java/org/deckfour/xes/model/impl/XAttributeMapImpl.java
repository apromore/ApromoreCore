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
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import java.util.Map;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;

/**
 * Memory-based implementation of the XAttributeMap interface.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XAttributeMapImpl extends UnifiedMap<String, XAttribute> implements
		XAttributeMap {

	/**
	 * serial version UID.
	 */
	private static final long serialVersionUID = 2701256420845748051L;

	/**
	 * Creates a new attribute map.
	 */
	public XAttributeMapImpl() {
		this(0);
	}

	/**
	 * Creates a new attribute map.
	 * 
	 * @param size
	 *            Initial size of the map.
	 */
	public XAttributeMapImpl(int size) {
		super(size);
	}

	/**
	 * Creates a new attribute map.
	 * 
	 * @param template
	 *            Copy the contents of this attribute map to the new attrribute
	 *            map.
	 */
	public XAttributeMapImpl(Map<String, XAttribute> template) {
		super(template.size());
		for (String key : template.keySet()) {
			put(key, template.get(key));
		}
	}

	/**
	 * Creates a clone, i.e. deep copy, of this attribute map.
	 */
	@Override
	public UnifiedMap<String, XAttribute> clone() {
		XAttributeMapImpl clone = new XAttributeMapImpl(size());
		for (XAttribute value : values()) {
			clone.put(value.getKey(), (XAttribute) value.clone());
		}
		return clone;
	}

}
