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
package org.deckfour.xes.model;

import java.util.Set;

import org.deckfour.xes.extension.XExtension;

/**
 * This interface is implemented by all elements of the log hierarchy, which can
 * be equipped with attributes.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public interface XAttributable {

	/**
	 * Retrieves the attributes set for this element.
	 * 
	 * @return A map of attributes.
	 */
	public XAttributeMap getAttributes();

	/**
	 * Sets the map of attributes for this element.
	 * 
	 * @param attributes
	 *            A map of attributes.
	 */
	public void setAttributes(XAttributeMap attributes);

	/**
	 * Checks for the existence of attributes. This method can be a more
	 * efficient way of checking for the existance of attributes than using
	 * {@link #getAttributes()} in certain situations.
	 * 
	 * @return whether this element has any attributes
	 */
	public boolean hasAttributes();

	/**
	 * Retrieves the extensions used by this element, i.e. the extensions used
	 * by all attributes of this element, and the element itself.
	 * 
	 * @return A set of extensions.
	 */
	public Set<XExtension> getExtensions();

}
