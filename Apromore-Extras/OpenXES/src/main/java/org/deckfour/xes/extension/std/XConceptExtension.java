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

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;

/**
 * This extension provides naming for concepts in the event log type hierarchy.
 * 
 * It defines two attributes:
 * <ul>
 * <li>concept:name: Name (of any type hierarchy element)</li>
 * <li>concept:instance: Instance identifier (of events)</li>
 * </ul>
 * 
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XConceptExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6604751608301985546L;
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/concept.xesext");
	/**
	 * Key for the name attribute.
	 */
	public static final String KEY_NAME = "concept:name";
	/**
	 * Key for the instance attribute.
	 */
	public static final String KEY_INSTANCE = "concept:instance";
	/**
	 * Name attribute prototype
	 */
	public static XAttributeLiteral ATTR_NAME;
	/**
	 * Instance attribute prototype
	 */
	public static XAttributeLiteral ATTR_INSTANCE;

	/**
	 * Singleton instance of this extension.
	 */
	private transient static XConceptExtension singleton = new XConceptExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XConceptExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Private constructor
	 */
	private XConceptExtension() {
		super("Concept", "concept", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_NAME = factory.createAttributeLiteral(KEY_NAME, "__INVALID__",
				this);
		ATTR_INSTANCE = factory.createAttributeLiteral(KEY_INSTANCE,
				"__INVALID__", this);
		this.logAttributes.add((XAttribute) ATTR_NAME.clone());
		this.traceAttributes.add((XAttribute) ATTR_NAME.clone());
		this.eventAttributes.add((XAttribute) ATTR_NAME.clone());
		this.eventAttributes.add((XAttribute) ATTR_INSTANCE.clone());
		// register aliases
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_NAME, "Name");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_INSTANCE,
				"Instance");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_NAME, "Name");
		XGlobalAttributeNameMap.instance()
				.registerMapping(XGlobalAttributeNameMap.MAPPING_GERMAN,
						KEY_INSTANCE, "Instanz");
		XGlobalAttributeNameMap.instance()
				.registerMapping(XGlobalAttributeNameMap.MAPPING_FRENCH,
						KEY_NAME, "Appellation");
		XGlobalAttributeNameMap.instance()
				.registerMapping(XGlobalAttributeNameMap.MAPPING_FRENCH,
						KEY_INSTANCE, "Entité");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_NAME, "Nombre");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_INSTANCE,
				"Instancia");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_NAME, "Nome");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_INSTANCE,
				"Instância");
	}

	/**
	 * Retrieves the name of a log data hierarchy element, if set by this
	 * extension's name attribute.
	 * 
	 * @param element
	 *            Log hierarchy element to extract name from.
	 * @return The requested element name.
	 */
	public String extractName(XAttributable element) {
		XAttribute attribute = element.getAttributes().get(KEY_NAME);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	/**
	 * Assigns any log data hierarchy element its name, as defined by this
	 * extension's name attribute.
	 * 
	 * @param element
	 *            Log hierarchy element to assign name to.
	 * @param name
	 *            The name to be assigned.
	 */
	public void assignName(XAttributable element, String name) {
		if (name != null && name.trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_NAME.clone();
			attr.setValue(name);
			element.getAttributes().put(KEY_NAME, attr);
		}
	}

	/**
	 * Retrieves the activity instance identifier of an event, if set by this
	 * extension's instance attribute.
	 * 
	 * @param event
	 *            Event to extract instance from.
	 * @return The requested activity instance identifier.
	 */
	public String extractInstance(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_INSTANCE);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	/**
	 * Assigns any event its activity instance identifier, as defined by this
	 * extension's instance attribute.
	 * 
	 * @param event
	 *            Event to assign activity instance identifier to.
	 * @param name
	 *            The activity instance identifier to be assigned.
	 */
	public void assignInstance(XEvent event, String instance) {
		if (instance != null && instance.trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_INSTANCE.clone();
			attr.setValue(instance);
			event.getAttributes().put(KEY_INSTANCE, attr);
		}
	}

}
