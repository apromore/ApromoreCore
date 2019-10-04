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
package org.deckfour.xes.model;

import java.util.Date;

import org.deckfour.xes.model.impl.XsDateTimeFormat;

/**
 * Attribute with timestamp type value.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public interface XAttributeTimestamp extends XAttribute {

	/**
	 * Formatter to be used for formatting dates. This formatter is locale
	 * independent, to allow for serialized files to be moved between locales.
	 */
	public static final XsDateTimeFormat FORMATTER = new XsDateTimeFormat();

	/**
	 * Assigns the timestamp value of this attribute.
	 * 
	 * @param value
	 *            Value of the attribute.
	 */
	public void setValue(Date value);

	/**
	 * Assigns the timestamp value of this attribute in milliseconds.
	 * 
	 * @param value
	 *            Value of the attribute.
	 */
	public void setValueMillis(long value);

	/**
	 * Retrieves the timestamp value of this attribute.
	 * 
	 * @return Value of this attribute.
	 */
	public Date getValue();

	/**
	 * Retrieves the timestamp value of this attribute, in milliseconds.
	 * 
	 * @return Value of this attribute.
	 */
	public long getValueMillis();

}
