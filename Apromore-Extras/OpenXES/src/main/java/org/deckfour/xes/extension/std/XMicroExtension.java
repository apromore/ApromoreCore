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
 * Copyright (c) 2016 Christian W. Guenther (christian@deckfour.org)
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
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XEvent;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 *
 */
public class XMicroExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -173374654036723348L;

	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI.create("http://www.xes-standard.org/micro.xesext");

	/**
	 * Prefix for this extension.
	 */
	public static final String PREFIX = "micro";

	/**
	 * Levels of all defined attributes.
	 */
	private static enum Level {
		EVENT
	};

	/**
	 * Types of all defined attributes.
	 */
	private static enum Type {
		ID, INT
	};

	/**
	 * All defined attributes.
	 */
	private static enum DefinedAttribute {
		LENGTH	("length",		Level.EVENT, Type.INT, 	"Number of child events for this event"),
		LEVEL	("level",		Level.EVENT, Type.INT, 	"Micro level of this event"), 
		PID		("parentId",	Level.EVENT, Type.ID, 	"Id of parent event of this event");

		String key;
		String alias;
		Level level;
		Type type;
		XAttribute prototype;

		DefinedAttribute(String key, Level level, Type type, String alias) {
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
	public static final String KEY_LENGTH	 		= DefinedAttribute.LENGTH.key;
	public static final String KEY_LEVEL		 	= DefinedAttribute.LEVEL.key;
	public static final String KEY_PID		 		= DefinedAttribute.PID.key;

	/**
	 * Global prototype place holders. Need to be initialized by constructor.
	 */
	public static XAttributeDiscrete 	ATTR_LENGTH;
	public static XAttributeDiscrete 	ATTR_LEVEL;
	public static XAttributeID		 	ATTR_PID;

	/**
	 * Singleton instance of this extension.
	 */
	private transient static XMicroExtension singleton = new XMicroExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XMicroExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Private constructor
	 */
	private XMicroExtension() {
		super("Micro", PREFIX, EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		/*
		 * Initialize all defined attributes.
		 */
		for (DefinedAttribute attribute : DefinedAttribute.values()) {
			/*
			 * Initialize the prototype of the attribute. Depends on its type.
			 */
			switch (attribute.type) {
			case ID: {
				attribute.setPrototype(factory.createAttributeID(
						attribute.key, new XID(), this));
				break;
			}
			case INT: {
				attribute.setPrototype(factory.createAttributeDiscrete(
						attribute.key, -1, this));
				break;
			}
			}
			/*
			 * Add the attribute to the proper list. Depends on the level.
			 */
			switch (attribute.level){
			case EVENT: {
				this.eventAttributes.add((XAttribute) attribute.prototype.clone());
				break;
			}
			}
			/*
			 * Initialize the proper global prototype place holder.
			 */
			switch (attribute){
			case LENGTH: {
				ATTR_LENGTH = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case LEVEL: {
				ATTR_LEVEL = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case PID: {
				ATTR_PID = (XAttributeID) attribute.prototype;
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

	public long extractLength(XEvent event) {
		return extract(event, DefinedAttribute.LENGTH, -1);
	}
	
	public void assignLength(XEvent event, long length) {
		assign(event, DefinedAttribute.LENGTH, length);
	}
	
	public void removeLength(XAttributable event) {
		remove(event, DefinedAttribute.LENGTH);
	}

	public long extractLevel(XEvent event) {
		return extract(event, DefinedAttribute.LEVEL, -1);
	}
	
	public void assignLevel(XEvent event, long level) {
		assign(event, DefinedAttribute.LEVEL, level);
	}
	
	public void removeLevel(XAttributable event) {
		remove(event, DefinedAttribute.LEVEL);
	}

	public XID extractParentId(XEvent event) {
		return extract(event, DefinedAttribute.PID, (XID) null);
	}
	
	public void assignParentId(XEvent event, XID parentId) {
		assign(event, DefinedAttribute.PID, parentId);
	}
	
	public void removeParentId(XAttributable event) {
		remove(event, DefinedAttribute.PID);
	}

	private XID extract(XAttributable element, DefinedAttribute definedAttribute, XID defaultValue) {
		XAttribute attribute = element.getAttributes().get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return ((XAttributeID) attribute).getValue();
		}
	}
	
	private void assign(XAttributable element, DefinedAttribute definedAttribute, XID value) {
		XAttributeID attr = (XAttributeID) definedAttribute.prototype.clone();
		attr.setValue(value);
		element.getAttributes().put(definedAttribute.key, attr);
	}

	private long extract(XAttributable element, DefinedAttribute definedAttribute, long defaultValue) {
		XAttribute attribute = element.getAttributes().get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return ((XAttributeDiscrete) attribute).getValue();
		}
	}
	
	private void assign(XAttributable element, DefinedAttribute definedAttribute, long value) {
		XAttributeDiscrete attr = (XAttributeDiscrete) definedAttribute.prototype.clone();
		attr.setValue(value);
		element.getAttributes().put(definedAttribute.key, attr);
	}

	private void remove(XAttributable element, DefinedAttribute definedAttribute) {
		element.getAttributes().remove(definedAttribute.key);
	}
	

}
