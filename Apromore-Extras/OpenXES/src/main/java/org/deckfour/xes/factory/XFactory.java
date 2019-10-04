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
package org.deckfour.xes.factory;

import java.net.URI;
import java.util.Date;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * Factory interface, providing factory methods for creating all element classes
 * of the XES model type hierarchy.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public interface XFactory {

	/**
	 * Returns the name of the specific factory implementation.
	 */
	public abstract String getName();

	/**
	 * Returns the author name of the specific factory implementation.
	 */
	public abstract String getAuthor();

	/**
	 * Returns the vendor of the specific factory implementation.
	 */
	public abstract String getVendor();

	/**
	 * Returns a description of the specific factory implementation.
	 */
	public abstract String getDescription();

	/**
	 * Returns an URI, pointing to more information about the specific factory
	 * implementation.
	 */
	public abstract URI getUri();

	/**
	 * Creates a new XES log instance (Factory method).
	 * 
	 * @return A new log instance.
	 */
	public abstract XLog createLog();

	/**
	 * Creates a new XES log instance (Factory method).
	 * 
	 * @param attributes
	 *            The attributes of the log.
	 * @return A new log instance.
	 */
	public abstract XLog createLog(XAttributeMap attributes);

	/**
	 * Creates a new XES trace instance (Factory method).
	 * 
	 * @return A new trace instance.
	 */
	public abstract XTrace createTrace();

	/**
	 * Creates a new XES trace instance (Factory method).
	 * 
	 * @param attributes
	 *            The attributes of the trace.
	 * @return A new trace instance.
	 */
	public abstract XTrace createTrace(XAttributeMap attributes);

	/**
	 * Creates a new XES event instance (Factory method).
	 * 
	 * @return A new event instance.
	 */
	public abstract XEvent createEvent();

	/**
	 * Creates a new XES event instance (Factory method).
	 * 
	 * @param attributes
	 *            The attributes of the event.
	 * @return A new event instance.
	 */
	public abstract XEvent createEvent(XAttributeMap attributes);

	/**
	 * Creates a new XES event instance (Factory method). Only to be used in
	 * case of deserialization, such that the id remains consistent.
	 * 
	 * @param id
	 *            the id of this new event. Only to be used in case of
	 *            deserializing!
	 * @param attributes
	 *            the attributes of the event
	 * @return A new event instance
	 */
	public abstract XEvent createEvent(XID id, XAttributeMap attributes);

	/**
	 * Creates a new XES attribute map (Factory method).
	 * 
	 * @return A new XES attribute map instance.
	 */
	public abstract XAttributeMap createAttributeMap();

	/**
	 * Creates a new XES attribute with boolean type (Factory method).
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @param extension
	 *            The extension defining the attribute (set to <code>null</code>
	 *            , if the attribute is not associated to an extension)
	 * @return A newly created attribute.
	 */
	public abstract XAttributeBoolean createAttributeBoolean(String key,
			boolean value, XExtension extension);

	/**
	 * Creates a new XES attribute with continuous type (Factory method).
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @param extension
	 *            The extension defining the attribute (set to <code>null</code>
	 *            , if the attribute is not associated to an extension)
	 * @return A newly created attribute.
	 */
	public abstract XAttributeContinuous createAttributeContinuous(String key,
			double value, XExtension extension);

	/**
	 * Creates a new XES attribute with discrete type (Factory method).
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @param extension
	 *            The extension defining the attribute (set to <code>null</code>
	 *            , if the attribute is not associated to an extension)
	 * @return A newly created attribute.
	 */
	public abstract XAttributeDiscrete createAttributeDiscrete(String key,
			long value, XExtension extension);

	/**
	 * Creates a new XES attribute with literal type (Factory method).
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @param extension
	 *            The extension defining the attribute (set to <code>null</code>
	 *            , if the attribute is not associated to an extension)
	 * @return A newly created attribute.
	 */
	public abstract XAttributeLiteral createAttributeLiteral(String key,
			String value, XExtension extension);

	/**
	 * Creates a new XES attribute with timestamp type (Factory method).
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @param extension
	 *            The extension defining the attribute (set to <code>null</code>
	 *            , if the attribute is not associated to an extension)
	 * @return A newly created attribute.
	 */
	public abstract XAttributeTimestamp createAttributeTimestamp(String key,
			Date value, XExtension extension);

	/**
	 * Creates a new XES attribute with timestamp type (Factory method).
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            The value of the attribute, in milliseconds since 01/01/1970
	 *            0:00 GMT.
	 * @param extension
	 *            The extension defining the attribute (set to <code>null</code>
	 *            , if the attribute is not associated to an extension)
	 * @return A newly created attribute.
	 */
	public abstract XAttributeTimestamp createAttributeTimestamp(String key,
			long millis, XExtension extension);

	/**
	 * Creates a new XES attribute with id type (Factory method).
	 * 
	 * @param key
	 *            The key of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @param extension
	 *            The extension defining the attribute (set to <code>null</code>
	 *            , if the attribute is not associated to an extension)
	 * @return A newly created attribute.
	 */
	public abstract XAttributeID createAttributeID(String key,
			XID value, XExtension extension);
	
	public abstract XAttributeList createAttributeList(String key, XExtension extension);
	
	public abstract XAttributeContainer createAttributeContainer(String key, XExtension extension);
}