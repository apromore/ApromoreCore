/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2011 Christian W. Guenther (christian@deckfour.org)
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
import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeID;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 *
 */
public class XIdentityExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4908408129891998507L;
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/identity.xesext");
	/**
	 * Key for the identity attribute.
	 */
	public static final String KEY_ID = "identity:id";
	/**
	 * Identity attribute prototype
	 */
	public static XAttributeID ATTR_ID;
	
	/**
	 * Singleton instance of this extension.
	 */
	private transient static XIdentityExtension singleton = new XIdentityExtension();
	
	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XIdentityExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}
	
	/**
	 * Private constructor
	 */
	private XIdentityExtension() {
		super("Identity", "identity", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_ID = factory.createAttributeID(KEY_ID, XIDFactory.instance().createId(),
				this);
		this.logAttributes.add((XAttribute) ATTR_ID.clone());
		this.traceAttributes.add((XAttribute) ATTR_ID.clone());
		this.eventAttributes.add((XAttribute) ATTR_ID.clone());
		this.metaAttributes.add((XAttribute) ATTR_ID.clone());
		// register aliases
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_ID, "Identity");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_ID, "Identität");
		XGlobalAttributeNameMap.instance()
				.registerMapping(XGlobalAttributeNameMap.MAPPING_FRENCH,
						KEY_ID, "Identité");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_ID, "Identidad");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_ID, "Identidade");
	}

	/**
	 * Retrieves the id of a log data hierarchy element, if set by this
	 * extension's id attribute.
	 * 
	 * @param element
	 *            Log hierarchy element to extract name from.
	 * @return The requested element id.
	 */
	public XID extractID(XAttributable element) {
		XAttribute attribute = element.getAttributes().get(KEY_ID);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeID) attribute).getValue();
		}
	}

	/**
	 * Assigns any log data hierarchy element its id, as defined by this
	 * extension's id attribute.
	 * 
	 * @param element
	 *            Log hierarchy element to assign id to.
	 * @param id
	 *            The id to be assigned.
	 */
	public void assignID(XAttributable element, XID id) {
		if (id != null) {
			XAttributeID attr = (XAttributeID) ATTR_ID.clone();
			attr.setValue(id);
			element.getAttributes().put(KEY_ID, attr);
		}
	}
}
