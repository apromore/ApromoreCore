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
package org.deckfour.xes.extension.std;

import java.net.URI;
import java.util.Date;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;

/**
 * This extension defines the Time perspective on event logs. It makes it
 * possible to assign to each event a timestamp, describing when the event has
 * occurred.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XTimeExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3632061569016038500L;
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/time.xesext");
	/**
	 * Key for the timestamp attribute.
	 */
	public static final String KEY_TIMESTAMP = "time:timestamp";
	/**
	 * Timestamp attribute prototype.
	 */
	public static XAttributeTimestamp ATTR_TIMESTAMP;

	/**
	 * Singleton instance of this extension.
	 */
	private static XTimeExtension singleton = new XTimeExtension();

	/**
	 * Provides access to the singleton instance of this extension.
	 * 
	 * @return The Time extension singleton.
	 */
	public static XTimeExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Creates a new instance (hidden constructor).
	 */
	private XTimeExtension() {
		super("Time", "time", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_TIMESTAMP = factory.createAttributeTimestamp(KEY_TIMESTAMP, 0,
				this);
		this.eventAttributes.add((XAttribute) ATTR_TIMESTAMP.clone());
		// register mapping aliases
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_TIMESTAMP,
				"Timestamp");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_TIMESTAMP,
				"Zeitstempel");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_FRENCH, KEY_TIMESTAMP,
				"Horodateur");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_TIMESTAMP,
				"Timestamp");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_TIMESTAMP,
				"Timestamp");
	}

	/**
	 * Extracts from a given event the timestamp.
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return The timestamp of this event, as a Date object (may be
	 *         <code>null</code> if not defined).
	 */
	public Date extractTimestamp(XEvent event) {
		XAttributeTimestamp timestampAttribute = (XAttributeTimestamp) event
				.getAttributes().get(KEY_TIMESTAMP);
		if (timestampAttribute == null) {
			return null;
		} else {
			return timestampAttribute.getValue();
		}
	}

	/**
	 * Assigns to a given event its timestamp.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param timestamp
	 *            Timestamp, as a Date object.
	 */
	public void assignTimestamp(XEvent event, Date timestamp) {
		assignTimestamp(event, timestamp.getTime());
	}

	/**
	 * Assigns to a given event its timestamp.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param time
	 *            Timestamp, as a long of milliseconds in UNIX time.
	 */
	public void assignTimestamp(XEvent event, long time) {
		XAttributeTimestamp attr = (XAttributeTimestamp) ATTR_TIMESTAMP.clone();
		attr.setValueMillis(time);
		event.getAttributes().put(KEY_TIMESTAMP, attr);
	}

}
