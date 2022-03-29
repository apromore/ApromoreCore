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
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
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

import java.util.Objects;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;

/**
 * This class implements continuous type attributes.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class XAttributeContinuousImpl extends XAttributeImpl implements
		XAttributeContinuous {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1789813595800348876L;
	
	/**
	 * Value of the attribute.
	 */
	private double value;

	/**
	 * Creates a new instance.
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            Value of the attribute.
	 */
	public XAttributeContinuousImpl(String key, double value) {
		this(key, value, null);
	}

	public XAttributeContinuousImpl() {
		this("", 0, null);
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            Value of the attribute.
	 * @param extension
	 *            The extension of the attribute.
	 */
	public XAttributeContinuousImpl(String key, double value,
			XExtension extension) {
		super(key, extension);
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see XAttributeContinuous#getValue()
	 */
	public double getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see XAttributeContinuous#setValue(boolean)
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Double.toString(this.value);
	}

	public Object clone() {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof XAttributeContinuous) { // compares types
			XAttributeContinuous other = (XAttributeContinuous) obj;
			return super.equals(other) // compares keys
					&& (value == other.getValue()); // compares values
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(getKey(), value);
	}
	
	@Override
	public int compareTo(XAttribute other) {
		if (!(other instanceof XAttributeContinuous)) {
			throw new ClassCastException();
		}
		int result = super.compareTo(other);
		if (result != 0) {
			return result;
		}
		return ((Double)value).compareTo(((XAttributeContinuous)other).getValue());
	}
}
