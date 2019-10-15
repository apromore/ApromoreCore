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
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;

/**
 * This extension adds the organizational perspective to event logs.
 * 
 * It defines for events three attributes, referring to:
 * <ul>
 * <li>The resource which has executed the event</li>
 * <li>The role of this resource</li>
 * <li>The group of this resource</li>
 * <ul>
 * 
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XOrganizationalExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8578385457800103461L;
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/org.xesext");
	/**
	 * Key for the resource attribute.
	 */
	public static final String KEY_RESOURCE = "org:resource";
	/**
	 * Key for the role attribute.
	 */
	public static final String KEY_ROLE = "org:role";
	/**
	 * Key for the group attribute.
	 */
	public static final String KEY_GROUP = "org:group";
	/**
	 * Resource attribute prototype.
	 */
	public static XAttributeLiteral ATTR_RESOURCE;
	/**
	 * Role attribute prototype.
	 */
	public static XAttributeLiteral ATTR_ROLE;
	/**
	 * Group attribute prototype.
	 */
	public static XAttributeLiteral ATTR_GROUP;

	/**
	 * Singleton instance of this extension.
	 */
	private static XOrganizationalExtension singleton = new XOrganizationalExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XOrganizationalExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Creates a new instance (hidden constructor)
	 */
	private XOrganizationalExtension() {
		super("Organizational", "org", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_RESOURCE = factory.createAttributeLiteral(KEY_RESOURCE,
				"__INVALID__", this);
		ATTR_ROLE = factory.createAttributeLiteral(KEY_ROLE, "__INVALID__",
				this);
		ATTR_GROUP = factory.createAttributeLiteral(KEY_GROUP, "__INVALID__",
				this);
		this.eventAttributes.add((XAttribute) ATTR_RESOURCE.clone());
		this.eventAttributes.add((XAttribute) ATTR_ROLE.clone());
		this.eventAttributes.add((XAttribute) ATTR_GROUP.clone());
		// register aliases
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_RESOURCE,
				"Resource");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_ROLE, "Role");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_GROUP, "Group");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_RESOURCE, "Akteur");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_ROLE, "Rolle");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_GROUP, "Gruppe");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_FRENCH, KEY_RESOURCE, "Agent");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_FRENCH, KEY_ROLE, "Rôle");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_FRENCH, KEY_GROUP, "Groupe");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_RESOURCE,
				"Recurso");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_ROLE, "Papel");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_GROUP, "Grupo");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_RESOURCE,
				"Recurso");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_ROLE, "Papel");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_GROUP, "Grupo");
	}

	/**
	 * Extracts the resource attribute string from an event.
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return Resource string for the given event (may be <code>null</code> if
	 *         not defined)
	 */
	public String extractResource(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_RESOURCE);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	/**
	 * Assigns the resource attribute value for a given event.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param resource
	 *            Resource string to be assigned.
	 */
	public void assignResource(XEvent event, String resource) {
		if (resource != null && resource.trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_RESOURCE.clone();
			attr.setValue(resource.trim());
			event.getAttributes().put(KEY_RESOURCE, attr);
		}
	}

	/**
	 * Extracts the role attribute string from an event.
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return Role string for the given event (may be <code>null</code> if not
	 *         defined)
	 */
	public String extractRole(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_ROLE);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	/**
	 * Assigns the role attribute value for a given event.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param resource
	 *            Role string to be assigned.
	 */
	public void assignRole(XEvent event, String role) {
		if (role != null && role.trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_ROLE.clone();
			attr.setValue(role.trim());
			event.getAttributes().put(KEY_ROLE, attr);
		}
	}

	/**
	 * Extracts the group attribute string from an event.
	 * 
	 * @param event
	 *            Event to be queried.
	 * @return Group string for the given event (may be <code>null</code> if not
	 *         defined)
	 */
	public String extractGroup(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_GROUP);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	/**
	 * Assigns the group attribute value for a given event.
	 * 
	 * @param event
	 *            Event to be modified.
	 * @param resource
	 *            Group string to be assigned.
	 */
	public void assignGroup(XEvent event, String group) {
		if (group != null && group.trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_GROUP.clone();
			attr.setValue(group.trim());
			event.getAttributes().put(KEY_GROUP, attr);
		}
	}

}
