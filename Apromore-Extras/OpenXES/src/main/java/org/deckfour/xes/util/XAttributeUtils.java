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
package org.deckfour.xes.util;

import java.text.ParseException;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;

/**
 * Utilities for working with attributes.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class XAttributeUtils {

	/**
	 * For the given attribute, returns its type, i.e., the most high-level,
	 * typed interface this attribute implements.
	 * 
	 * @param attribute
	 *            Attribute to analyze.
	 * @return High-level type interface of this attribute.
	 */
	public static Class<? extends XAttribute> getType(XAttribute attribute) {
		if (attribute instanceof XAttributeList) {
			return XAttributeList.class;
		} else if (attribute instanceof XAttributeContainer) {
			return XAttributeContainer.class;
		} else if (attribute instanceof XAttributeLiteral) {
			return XAttributeLiteral.class;
		} else if (attribute instanceof XAttributeBoolean) {
			return XAttributeBoolean.class;
		} else if (attribute instanceof XAttributeContinuous) {
			return XAttributeContinuous.class;
		} else if (attribute instanceof XAttributeDiscrete) {
			return XAttributeDiscrete.class;
		} else if (attribute instanceof XAttributeTimestamp) {
			return XAttributeTimestamp.class;
		} else if (attribute instanceof XAttributeID) {
			return XAttributeID.class;
		} else {
			throw new AssertionError("Unexpected attribute type!");
		}
	}

	/**
	 * For the given attribute, derives the standardized string describing the
	 * attributes specific type (used, e.g., for serialization).
	 * 
	 * @param attribute
	 *            Attribute to extract type string from.
	 * @return String representation of the attribute's specific type.
	 */
	public static String getTypeString(XAttribute attribute) {
		if (attribute instanceof XAttributeList) {
			return "LIST";
		} else if (attribute instanceof XAttributeContainer) {
			return "CONTAINER";
		} else if (attribute instanceof XAttributeLiteral) {
			return "LITERAL";
		} else if (attribute instanceof XAttributeBoolean) {
			return "BOOLEAN";
		} else if (attribute instanceof XAttributeContinuous) {
			return "CONTINUOUS";
		} else if (attribute instanceof XAttributeDiscrete) {
			return "DISCRETE";
		} else if (attribute instanceof XAttributeTimestamp) {
			return "TIMESTAMP";
		} else if (attribute instanceof XAttributeID) {
			return "ID";
		} else {
			throw new AssertionError("Unexpected attribute type!");
		}
	}

	/**
	 * Derives a prototype for the given attribute. This prototype attribute
	 * will be equal in all respects, expect for the value of the attribute.
	 * This value will be set to a default value, depending on the specific type
	 * of the given attribute.
	 * 
	 * @param instance
	 *            Attribute to derive prototype from.
	 * @return The derived prototype attribute.
	 */
	public static XAttribute derivePrototype(XAttribute instance) {
		XAttribute prototype = (XAttribute) instance.clone();
		if (prototype instanceof XAttributeList) {
		} else if (prototype instanceof XAttributeContainer) {
		} else if (prototype instanceof XAttributeLiteral) {
			((XAttributeLiteral) prototype).setValue("DEFAULT");
		} else if (prototype instanceof XAttributeBoolean) {
			((XAttributeBoolean) prototype).setValue(true);
		} else if (prototype instanceof XAttributeContinuous) {
			((XAttributeContinuous) prototype).setValue(0.0);
		} else if (prototype instanceof XAttributeDiscrete) {
			((XAttributeDiscrete) prototype).setValue(0);
		} else if (prototype instanceof XAttributeTimestamp) {
			((XAttributeTimestamp) prototype).setValueMillis(0);
		} else if (prototype instanceof XAttributeID) {
			((XAttributeID) prototype).setValue(XIDFactory.instance().createId());
		} else {
			throw new AssertionError("Unexpected attribute type!");
		}
		return prototype;
	}

	/**
	 * Composes the appropriate attribute type from the string-based information
	 * found, e.g., in XML serializations.
	 * 
	 * @param factory
	 *            Factory to use for creating the attribute.
	 * @param key
	 *            Key of the attribute.
	 * @param value
	 *            Value of the attribute.
	 * @param type
	 *            Type string of the attribute.
	 * @param extension
	 *            Extension of the attribute (can be <code>null</code>).
	 * @return An appropriate attribute.
	 */
	public static XAttribute composeAttribute(XFactory factory, String key,
			String value, String type, XExtension extension) {
		type = type.trim();
		if (type.equalsIgnoreCase("LIST")) {
			XAttributeList attr = factory.createAttributeList(key, extension);
			return attr;
		} else if (type.equalsIgnoreCase("CONTAINER")) {
			XAttributeContainer attr = factory.createAttributeContainer(key, extension);
			return attr;
		} else if (type.equalsIgnoreCase("LITERAL")) {
			XAttributeLiteral attr = factory.createAttributeLiteral(key, value,
					extension);
			return attr;
		} else if (type.equalsIgnoreCase("BOOLEAN")) {
			XAttributeBoolean attr = factory.createAttributeBoolean(key,
					Boolean.parseBoolean(value), extension);
			return attr;
		} else if (type.equalsIgnoreCase("CONTINUOUS")) {
			XAttributeContinuous attr = factory.createAttributeContinuous(key,
					Double.parseDouble(value), extension);
			return attr;
		} else if (type.equalsIgnoreCase("DISCRETE")) {
			XAttributeDiscrete attr = factory.createAttributeDiscrete(key, Long
					.parseLong(value), extension);
			return attr;
		} else if (type.equalsIgnoreCase("TIMESTAMP")) {
			XAttributeTimestamp attr;
			try {
				synchronized (XAttributeTimestamp.FORMATTER) {
					attr = factory.createAttributeTimestamp(key,
							XAttributeTimestamp.FORMATTER.parseObject(value),
							extension);
				}
			} catch (ParseException e) {
				throw new AssertionError(
						"OpenXES: could not parse date-time attribute. Value: "
								+ value);
			}
			return attr;
		} else if (type.equalsIgnoreCase("ID")) {
			XAttributeID attr = factory.createAttributeID(key, XID.parse(value), extension);
			return attr;
		} else {
			throw new AssertionError("OpenXES: could not parse attribute type!");
		}
	}

	/**
	 * Static helper method for extracting all extensions from an attribute map.
	 * 
	 * @param attributeMap
	 *            The attribute map from which to extract extensions.
	 * @return The set of extensions in the attribute map.
	 */
	public static Set<XExtension> extractExtensions(
			Map<String, XAttribute> attributeMap) {
		UnifiedSet<XExtension> extensions = new UnifiedSet<XExtension>();
		for (XAttribute attribute : attributeMap.values()) {
			XExtension extension = attribute.getExtension();
			if (extension != null) {
				extensions.add(extension);
			}
		}
		return extensions;
	}

}
