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
 * Copyright (c) 2018 Christian W. Guenther (christian@deckfour.org)
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
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 *
 */
public class XArtifactLifecycleExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5862656097463790043L;
	
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI.create("http://www.xes-standard.org/artifactlifecycle.xesext");
	/**
	 * Prefix for this extension.
	 */
	public static final String PREFIX = "artifactlifecycle";

	/**
	 * Levels of all defined attributes.
	 */
	private static enum AttributeLevel {
		EVENT, META
	};

	/**
	 * Types of all defined attributes.
	 */
	private static enum AttributeType {
		LIST, STRING
	};

	/**
	 * All defined attributes.
	 */
	private static enum DefinedAttribute {
		MOVES("moves", AttributeLevel.EVENT, AttributeType.LIST, "Lifecycle Moves"), //
		MODEL("model", AttributeLevel.META, AttributeType.STRING, "Lifecycle Model"), //
		INSTANCE("instance", AttributeLevel.META, AttributeType.STRING, "Artifact Instance"), //
		TRANSITION("transition", AttributeLevel.META, AttributeType.STRING, "Lifecycle Transition");

		String key;
		String alias;
		AttributeLevel level;
		AttributeType type;
		XAttribute prototype;

		DefinedAttribute(String key, AttributeLevel level, AttributeType type, String alias) {
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
	public static final String KEY_MOVES = DefinedAttribute.MOVES.key;
	public static final String KEY_MODEL = DefinedAttribute.MODEL.key;
	public static final String KEY_INSTANCE = DefinedAttribute.INSTANCE.key;
	public static final String KEY_TRANSITION = DefinedAttribute.TRANSITION.key;

	/**
	 * Global prototype place holders. Need to be initialized by constructor.
	 */
	public static XAttributeList ATTR_MOVES;
	public static XAttributeLiteral ATTR_MODEL;
	public static XAttributeLiteral ATTR_INSTANCE;
	public static XAttributeLiteral ATTR_TRANSITION;

	/**
	 * Singleton instance of this extension.
	 */
	private transient static XArtifactLifecycleExtension singleton = new XArtifactLifecycleExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XArtifactLifecycleExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Private constructor
	 */
	private XArtifactLifecycleExtension() {
		super("Artifact Lifecycle", PREFIX, EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();

		/*
		 * Initialize all defined attributes.
		 */
		for (DefinedAttribute attribute : DefinedAttribute.values()) {
			/*
			 * Initialize the prototype of the attribute. Depends on its type.
			 */
			switch (attribute.type) {
			case LIST: {
				attribute.setPrototype(factory.createAttributeList(attribute.key, this));
				break;
			}
			case STRING: {
				attribute.setPrototype(factory.createAttributeLiteral(attribute.key, "__INVALID__", this));
				break;
			}
			}
			/*
			 * Add the attribute to the proper list. Depends on the level.
			 */
			switch (attribute.level) {
			case EVENT: {
				this.eventAttributes.add((XAttribute) attribute.prototype.clone());
				break;
			}
			case META: {
				this.metaAttributes.add((XAttribute) attribute.prototype.clone());
				break;
			}
			}
			/*
			 * Initialize the proper global prototype place holder.
			 */
			switch (attribute) {
			case MOVES: {
				ATTR_MOVES = (XAttributeList) attribute.prototype;
				break;
			}
			case MODEL: {
				ATTR_MODEL = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case INSTANCE: {
				ATTR_INSTANCE = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case TRANSITION: {
				ATTR_TRANSITION = (XAttributeLiteral) attribute.prototype;
				break;
			}
			}
			/*
			 * Initialize the key alias.
			 */
			XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, attribute.key,
					attribute.alias);
		}
	}

	/*
	 * A list of handy assign and extract methods. Most are really straightforward.
	 */
	public List<XAttribute> extractMoves(XEvent event) {
		return extract(event, DefinedAttribute.MOVES, (List<XAttribute>) null);
	}

	public XAttributeList assignMoves(XAttribute event) {
		return assign(event, DefinedAttribute.MOVES);
	}

	public XAttributeList removeMoves(XAttribute event) {
		return (XAttributeList) remove(event, DefinedAttribute.MOVES);
	}

	public String extractModel(XAttribute event) {
		return extract(event, DefinedAttribute.MODEL, (String) null);
	}

	public XAttributeLiteral assignModel(XAttribute event, String model) {
		return assign(event, DefinedAttribute.MODEL, model);
	}

	public XAttributeLiteral removeModel(XAttribute event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.MODEL);
	}

	public String extractInstance(XAttribute event) {
		return extract(event, DefinedAttribute.INSTANCE, (String) null);
	}

	public XAttributeLiteral assignInstance(XAttribute event, String model) {
		return assign(event, DefinedAttribute.INSTANCE, model);
	}

	public XAttributeLiteral removeInstance(XAttribute event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.INSTANCE);
	}

	public String extractTransition(XAttribute event) {
		return extract(event, DefinedAttribute.TRANSITION, (String) null);
	}

	public XAttributeLiteral assignTransition(XAttribute event, String model) {
		return assign(event, DefinedAttribute.TRANSITION, model);
	}

	public XAttributeLiteral removeTRansition(XAttribute event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.TRANSITION);
	}

	/*
	 * Helper functions
	 */
	private String extract(XAttributable element, DefinedAttribute definedAttribute, String defaultValue) {
		XAttribute attribute = element.getAttributes().get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	private XAttributeLiteral assign(XAttributable element, DefinedAttribute definedAttribute, String value) {
		if (value != null) {
			XAttributeLiteral attr = (XAttributeLiteral) definedAttribute.prototype.clone();
			attr.setValue(value);
			element.getAttributes().put(definedAttribute.key, attr);
			return attr;
		}
		return null;
	}

	private List<XAttribute> extract(XAttributable element, DefinedAttribute definedAttribute,
			List<XAttribute> defaultValue) {
		XAttribute attribute = element.getAttributes().get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return new ArrayList<XAttribute>(((XAttributeList) attribute).getCollection());
		}
	}

	private XAttributeList assign(XAttributable element, DefinedAttribute definedAttribute) {
		XAttributeList attr = (XAttributeList) definedAttribute.prototype.clone();
		element.getAttributes().put(definedAttribute.key, attr);
		return attr;
	}

	private XAttribute remove(XAttributable element, DefinedAttribute definedAttribute) {
		return element.getAttributes().remove(definedAttribute.key);
	}
}
