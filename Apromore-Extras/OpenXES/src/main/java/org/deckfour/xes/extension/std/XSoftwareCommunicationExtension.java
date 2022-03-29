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
 * Copyright (c) 2017 Christian W. Guenther (christian@deckfour.org)
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
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 */
public class XSoftwareCommunicationExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4543827658550657727L;
	
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/swcomm.xesext");
	/**
	 * Prefix for this extension.
	 */
	public static final String PREFIX = "swcomm";

	/**
	 * Levels of all defined attributes.
	 */
	private static enum AttributeLevel {
		EVENT
	};

	/**
	 * Types of all defined attributes.
	 */
	private static enum AttributeType {
		INT, STRING
	};

	/**
	 * All defined attributes.
	 */
	private static enum DefinedAttribute {
		LOCAL_HOST("localHost", AttributeLevel.EVENT, AttributeType.STRING,
				"Local endpoint - host name"), //
		LOCAL_PORT("localPort", AttributeLevel.EVENT, AttributeType.INT,
				"Local endpoint - port"), //
		REMOTE_HOST("remoteHost", AttributeLevel.EVENT, AttributeType.STRING,
				"Remote endpoint - host name"), //
		REMOTE_PORT("remotePort", AttributeLevel.EVENT, AttributeType.INT,
				"Remote endpoint - port");

		String key;
		String alias;
		AttributeLevel level;
		AttributeType type;
		XAttribute prototype;

		DefinedAttribute(String key, AttributeLevel level, AttributeType type,
				String alias) {
			this.key = PREFIX + ":" + key;
			this.level = level;
			this.type = type;
			this.alias = alias;
		}

		void setPrototype(XAttribute prototype) {
			this.prototype = prototype;
		}
	}

	/**
	 * Global key place holders. Can be initialized immediately.
	 */
	public static final String KEY_LOCAL_HOST = DefinedAttribute.LOCAL_HOST.key;
	public static final String KEY_LOCAL_PORT = DefinedAttribute.LOCAL_PORT.key;
	public static final String KEY_REMOTE_HOST = DefinedAttribute.REMOTE_HOST.key;
	public static final String KEY_REMOTE_PORT = DefinedAttribute.REMOTE_PORT.key;

	/**
	 * Global prototype place holders. Need to be initialized by constructor.
	 */
	public static XAttributeLiteral ATTR_LOCAL_HOST;
	public static XAttributeDiscrete ATTR_LOCAL_PORT;
	public static XAttributeLiteral ATTR_REMOTE_HOST;
	public static XAttributeDiscrete ATTR_REMOTE_PORT;

	/**
	 * Singleton instance of this extension.
	 */
	private transient static XSoftwareCommunicationExtension singleton = new XSoftwareCommunicationExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XSoftwareCommunicationExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Private constructor
	 */
	private XSoftwareCommunicationExtension() {
		super("Software Communication", PREFIX, EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();

		/*
		 * Initialize all defined attributes.
		 */
		for (DefinedAttribute attribute : DefinedAttribute.values()) {
			/*
			 * Initialize the prototype of the attribute. Depends on its type.
			 */
			switch (attribute.type) {
			case INT: {
				attribute.setPrototype(factory.createAttributeDiscrete(
						attribute.key, -1, this));
				break;
			}
			case STRING: {
				attribute.setPrototype(factory.createAttributeLiteral(
						attribute.key, "__INVALID__", this));
				break;
			}
			}
			/*
			 * Add the attribute to the proper list. Depends on the level.
			 */
			switch (attribute.level) {
			case EVENT: {
				this.eventAttributes.add((XAttribute) attribute.prototype
						.clone());
				break;
			}
			}
			/*
			 * Initialize the proper global prototype place holder.
			 */
			switch (attribute) {
			case LOCAL_HOST: {
				ATTR_LOCAL_HOST = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case LOCAL_PORT: {
				ATTR_LOCAL_PORT = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case REMOTE_HOST: {
				ATTR_REMOTE_HOST = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case REMOTE_PORT: {
				ATTR_REMOTE_PORT = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			}
			/*
			 * Initialize the key alias.
			 */
			XGlobalAttributeNameMap.instance().registerMapping(
					XGlobalAttributeNameMap.MAPPING_ENGLISH, attribute.key,
					attribute.alias);
		}
	}

	/*
	 * A list of handy assign and extract methods. Most are really
	 * straightforward.
	 */
	public String extractLocalHost(XEvent event) {
		return extract(event, DefinedAttribute.LOCAL_HOST, (String) null);
	}

	public XAttributeLiteral assignLocalHost(XEvent event, String localHost) {
		return assign(event, DefinedAttribute.LOCAL_HOST, localHost);
	}

	public long extractLocalPort(XEvent event) {
		return extract(event, DefinedAttribute.LOCAL_PORT, -1);
	}

	public XAttributeDiscrete assignLocalPort(XEvent event, long localPort) {
		return assign(event, DefinedAttribute.LOCAL_PORT, localPort);
	}

	public String extractRemoteHost(XEvent event) {
		return extract(event, DefinedAttribute.REMOTE_HOST, (String) null);
	}

	public XAttributeLiteral assignRemoteHost(XEvent event, String remoteHost) {
		return assign(event, DefinedAttribute.REMOTE_HOST, remoteHost);
	}

	public long extractRemotePort(XEvent event) {
		return extract(event, DefinedAttribute.REMOTE_PORT, -1);
	}

	public XAttributeDiscrete assignRemotePort(XEvent event, long remotePort) {
		return assign(event, DefinedAttribute.REMOTE_PORT, remotePort);
	}

	/*
	 * Helper functions
	 */
	private long extract(XAttributable element,
			DefinedAttribute definedAttribute, long defaultValue) {
		XAttribute attribute = element.getAttributes()
				.get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return ((XAttributeDiscrete) attribute).getValue();
		}
	}

	private XAttributeDiscrete assign(XAttributable element,
			DefinedAttribute definedAttribute, long value) {
		XAttributeDiscrete attr = (XAttributeDiscrete) definedAttribute.prototype
				.clone();
		attr.setValue(value);
		element.getAttributes().put(definedAttribute.key, attr);
		return attr;
	}

	private String extract(XAttributable element,
			DefinedAttribute definedAttribute, String defaultValue) {
		XAttribute attribute = element.getAttributes()
				.get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	private XAttributeLiteral assign(XAttributable element,
			DefinedAttribute definedAttribute, String value) {
		if (value != null) {
			XAttributeLiteral attr = (XAttributeLiteral) definedAttribute.prototype
					.clone();
			attr.setValue(value);
			element.getAttributes().put(definedAttribute.key, attr);
			return attr;
		}
		return null;
	}

}
