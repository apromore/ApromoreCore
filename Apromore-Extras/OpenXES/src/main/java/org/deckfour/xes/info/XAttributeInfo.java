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
package org.deckfour.xes.info;

import java.util.Collection;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;

/**
 * This interface defines an attribute information registry.
 * Instances of this interface can be used to store aggregate
 * information about the classes of attributes contained in 
 * a specific attributable type.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public interface XAttributeInfo {

	/**
	 * Provides access to prototypes of all registered attributes.
	 * 
	 * @return A collection of attribute prototypes.
	 */
	public abstract Collection<XAttribute> getAttributes();

	/**
	 * Provides access to prototypes of all registered attributes' keys.
	 * 
	 * @return A collection of attribute keys.
	 */
	public abstract Collection<String> getAttributeKeys();

	/**
	 * Returns the total frequency, i.e. number of occurrences,
	 * for the requested attribute.
	 * 
	 * @param key Key of an attribute.
	 * @return Total frequency of that attribute as registered.
	 */
	public abstract int getFrequency(String key);

	/**
	 * Returns the total frequency, i.e. number of occurrences,
	 * for the requested attribute.
	 * 
	 * @param attribute An attribute.
	 * @return Total frequency of that attribute as registered.
	 */
	public abstract int getFrequency(XAttribute attribute);

	/**
	 * Returns the relative frequency, i.e. between 0 and 1,
	 * for the requested attribute.
	 * 
	 * @param key Key of an attribute.
	 * @return Relative frequency of that attribute as registered.
	 */
	public abstract double getRelativeFrequency(String key);

	/**
	 * Returns the relative frequency, i.e. between 0 and 1,
	 * for the requested attribute.
	 * 
	 * @param attribute An attribute.
	 * @return Relative frequency of that attribute as registered.
	 */
	public abstract double getRelativeFrequency(XAttribute attribute);

	/**
	 * For a given type, returns prototypes of all registered
	 * attributes with that type.
	 * 
	 * @param type Requested attribute type (type-specific attribute interface class).
	 * @return A collection of attribute prototypes registered for that type.
	 */
	public abstract Collection<XAttribute> getAttributesForType(Class<? extends XAttribute> type);

	/**
	 * For a given type, returns the keys of all registered
	 * attributes with that type.
	 * 
	 * @param type Requested attribute type (type-specific attribute interface class).
	 * @return A collection of attribute keys registered for that type.
	 */
	public abstract Collection<String> getKeysForType(Class<? extends XAttribute> type);

	/**
	 * For a given extension, returns prototypes of all registered
	 * attributes defined by that extension.
	 * 
	 * @param extension Requested attribute extension.
	 * @return A collection of attribute prototypes registered for that extension.
	 */
	public abstract Collection<XAttribute> getAttributesForExtension(
			XExtension extension);

	/**
	 * For a given extension, returns the keys of all registered
	 * attributes defined by that extension.
	 * 
	 * @param extension Requested attribute extension.
	 * @return A collection of attribute keys registered for that extension.
	 */
	public abstract Collection<String> getKeysForExtension(XExtension extension);

	/**
	 * Returns prototypes of all registered attributes defined by no extension.
	 * 
	 * @return A collection of attribute prototypes registered for no extension.
	 */
	public abstract Collection<XAttribute> getAttributesWithoutExtension();

	/**
	 * Returns keys of all registered attributes defined by no extension.
	 * 
	 * @return A collection of attribute keys registered for no extension.
	 */
	public abstract Collection<String> getKeysWithoutExtension();

}