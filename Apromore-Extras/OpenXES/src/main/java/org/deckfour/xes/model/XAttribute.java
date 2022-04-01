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
package org.deckfour.xes.model;

import java.io.Serializable;

import org.deckfour.xes.extension.XExtension;

/**
 * This interface defines attributes used for describing
 * meta-information about event log hierarchy elements.
 * 
 * Attributes have a name (i.e., a key), which is string-based. 
 * The value of an attribute is strongly typed, and can be
 * accessed and modified via sub-interface methods specified
 * by type.
 * 
 * Attributes may further be defined by an extension,
 * which makes it possible to assign semantic meaning to
 * them within a specific domain.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public interface XAttribute extends XAttributable, Cloneable, Comparable<XAttribute>, Serializable {

	/**
	 * Retrieves the key, i.e. unique identifier, of this attribute.
	 * 
	 * @return The key of this attribute, as a string.
	 */
	public abstract String getKey();

	/**
	 * Retrieves the extension defining this attribute.
	 * 
	 * @return The extension of this attribute. May
	 * return <code>null</code>, if there is no extension
	 * defining this attribute.
	 */
	public abstract XExtension getExtension();
	
	/**
	 * Attributes must be cloneable.
	 * @return A clone of this attribute.
	 */
	public Object clone();
	
	/**
	 * String representation of the value.
	 * @return Returns the String representation of the value.
	 */
	public String toString();

	public void accept(XVisitor visitor, XAttributable attributable);
}
