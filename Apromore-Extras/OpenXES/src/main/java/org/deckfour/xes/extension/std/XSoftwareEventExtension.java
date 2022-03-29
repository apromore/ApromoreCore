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
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 * 
 */
public class XSoftwareEventExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8723771503683938737L;

	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/swevent.xesext");
	/**
	 * Prefix for this extension.
	 */
	public static final String PREFIX = "swevent";

	/**
	 * Levels of all defined attributes.
	 */
	private static enum AttributeLevel {
		EVENT, LOG, META
	};

	/**
	 * Types of all defined attributes.
	 */
	private static enum AttributeType {
		BOOLEAN, INT, LIST, STRING
	};

	/**
	 * Possible software event types
	 */
	public enum SoftwareEventType {
		CALL("call", "The start of a method block"), //
		CALLING("calling", "The start of calling / invoking another method"), //
		HANDLE("handle", "The start of an exception handle catch block"), //
		RETURN("return", "The normal end of a method block"), //
		RETURNING("returning", "The end of returning a called method"), //
		THROWS("throws",
				"The end of a method block in case of an uncaught exception"), //
		UNKNOWN("unknown", "Unknown software event type");

		private final String encoding;

		private SoftwareEventType(String encoding, String description) {
			this.encoding = encoding;
		}

		public String getEncoding() {
			return encoding;
		}

		public static SoftwareEventType decode(String encoding) {
			encoding = encoding.trim().toLowerCase();
			for (SoftwareEventType transition : SoftwareEventType.values()) {
				if (transition.encoding.equals(encoding)) {
					return transition;
				}
			}
			return SoftwareEventType.UNKNOWN;
		}

	}

	/**
	 * All defined attributes.
	 */
	private static enum DefinedAttribute {
		APP_NAME("appName", AttributeLevel.EVENT, AttributeType.STRING,
				"User defined application tier"), //
		APP_NODE("appNode", AttributeLevel.EVENT, AttributeType.STRING,
				"User defined application node"), //
		APP_SESSION("appSession", AttributeLevel.EVENT, AttributeType.STRING,
				"User defined application session"), //
		APP_TIER("appTier", AttributeLevel.EVENT, AttributeType.STRING,
				"User defined application name"), //
		CALLEE_CLASS("callee-class", AttributeLevel.EVENT,
				AttributeType.STRING, "Callee - Class"), //
		CALLEE_FILENAME("callee-filename", AttributeLevel.EVENT,
				AttributeType.STRING, "Callee - File name source code artifact"), //
		CALLEE_INSTANCEID("callee-instanceId", AttributeLevel.EVENT,
				AttributeType.STRING, "Callee - Instance id of class instance"), //
		CALLEE_ISCONSTRUCTOR("callee-isConstructor", AttributeLevel.EVENT,
				AttributeType.BOOLEAN, "Callee - Is a class constructor"), //
		CALLEE_LINENR("callee-lineNr", AttributeLevel.EVENT, AttributeType.INT,
				"Callee - Line number in source code artifact"), //
		CALLEE_METHOD("callee-method", AttributeLevel.EVENT,
				AttributeType.STRING, "Callee - Method"), //
		CALLEE_PACKAGE("callee-package", AttributeLevel.EVENT,
				AttributeType.STRING, "Callee - Package"), //
		CALLEE_PARAMSIG("callee-paramSig", AttributeLevel.EVENT,
				AttributeType.STRING, "Callee - Parameter signature"), //
		CALLEE_RETURNSIG("callee-returnSig", AttributeLevel.EVENT,
				AttributeType.STRING, "Callee - Return signature"), //
		CALLER_CLASS("caller-class", AttributeLevel.EVENT,
				AttributeType.STRING, "Caller - Class"), //
		CALLER_FILENAME("caller-filename", AttributeLevel.EVENT,
				AttributeType.STRING, "Caller - File name source code artifact"), //
		CALLER_INSTANCEID("caller-instanceId", AttributeLevel.EVENT,
				AttributeType.STRING, "Caller - Instance id of class instance"), //
		CALLER_ISCONSTRUCTOR("caller-isConstructor", AttributeLevel.EVENT,
				AttributeType.BOOLEAN, "Caller - Is a class constructor"), //
		CALLER_LINENR("caller-lineNr", AttributeLevel.EVENT, AttributeType.INT,
				"Caller - Line number in source code artifact"), //
		CALLER_METHOD("caller-method", AttributeLevel.EVENT,
				AttributeType.STRING, "Caller - Method"), //
		CALLER_PACKAGE("caller-package", AttributeLevel.EVENT,
				AttributeType.STRING, "Caller - Package"), //
		CALLER_PARAMSIG("caller-paramSig", AttributeLevel.EVENT,
				AttributeType.STRING, "Caller - Parameter signature"), //
		CALLER_RETURNSIG("caller-returnSig", AttributeLevel.EVENT,
				AttributeType.STRING, "Caller - Return signature"), //
		EX_CAUGHT("exCaught", AttributeLevel.EVENT, AttributeType.STRING,
				"Caught exception type"), //
		EX_THROWN("exThrown", AttributeLevel.EVENT, AttributeType.STRING,
				"Thrown exception type"), //
		HAS_DATA("hasData", AttributeLevel.LOG, AttributeType.BOOLEAN,
				"Has method data"), //
		HAS_EXCEPTION("hasException", AttributeLevel.LOG,
				AttributeType.BOOLEAN, "Has exception data"), //
		NANOTIME("nanotime", AttributeLevel.EVENT, AttributeType.INT,
				"Elapsed nano time"), //
		PARAMS("params", AttributeLevel.EVENT, AttributeType.LIST,
				"List of parameters for the called method"), //
		PARAM_VALUE("paramValue", AttributeLevel.META, AttributeType.STRING,
				"A parameter value in the list params"), //
		RETURN_VALUE("returnValue", AttributeLevel.EVENT, AttributeType.STRING,
				"Return value for the returning method"), //
		THREAD_ID("threadId", AttributeLevel.EVENT, AttributeType.STRING,
				"Thread id for generated event"), //
		TYPE("type", AttributeLevel.EVENT, AttributeType.STRING, "Event type"), //
		VALUE_TYPE("valueType", AttributeLevel.META, AttributeType.STRING,
				"A runtime value type for a return or parameter value");

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
	public static final String KEY_APP_NAME = DefinedAttribute.APP_NAME.key;
	public static final String KEY_APP_NODE = DefinedAttribute.APP_NODE.key;
	public static final String KEY_APP_SESSION = DefinedAttribute.APP_SESSION.key;
	public static final String KEY_APP_TIER = DefinedAttribute.APP_TIER.key;
	public static final String KEY_CALLEE_CLASS = DefinedAttribute.CALLEE_CLASS.key;
	public static final String KEY_CALLEE_FILENAME = DefinedAttribute.CALLEE_FILENAME.key;
	public static final String KEY_CALLEE_INSTANCEID = DefinedAttribute.CALLEE_INSTANCEID.key;
	public static final String KEY_CALLEE_ISCONSTRUCTOR = DefinedAttribute.CALLEE_ISCONSTRUCTOR.key;
	public static final String KEY_CALLEE_LINENR = DefinedAttribute.CALLEE_LINENR.key;
	public static final String KEY_CALLEE_METHOD = DefinedAttribute.CALLEE_METHOD.key;
	public static final String KEY_CALLEE_PACKAGE = DefinedAttribute.CALLEE_PACKAGE.key;
	public static final String KEY_CALLEE_PARAMSIG = DefinedAttribute.CALLEE_PARAMSIG.key;
	public static final String KEY_CALLEE_RETURNSIG = DefinedAttribute.CALLEE_PARAMSIG.key;
	public static final String KEY_CALLER_CLASS = DefinedAttribute.CALLER_CLASS.key;
	public static final String KEY_CALLER_FILENAME = DefinedAttribute.CALLER_FILENAME.key;
	public static final String KEY_CALLER_INSTANCEID = DefinedAttribute.CALLER_INSTANCEID.key;
	public static final String KEY_CALLER_ISCONSTRUCTOR = DefinedAttribute.CALLER_ISCONSTRUCTOR.key;
	public static final String KEY_CALLER_LINENR = DefinedAttribute.CALLER_LINENR.key;
	public static final String KEY_CALLER_METHOD = DefinedAttribute.CALLER_METHOD.key;
	public static final String KEY_CALLER_PACKAGE = DefinedAttribute.CALLER_PACKAGE.key;
	public static final String KEY_CALLER_PARAMSIG = DefinedAttribute.CALLER_PARAMSIG.key;
	public static final String KEY_CALLER_RETURNSIG = DefinedAttribute.CALLER_PARAMSIG.key;
	public static final String KEY_EX_CAUGHT = DefinedAttribute.EX_CAUGHT.key;
	public static final String KEY_EX_THROWN = DefinedAttribute.EX_THROWN.key;
	public static final String KEY_HAS_DATA = DefinedAttribute.HAS_DATA.key;
	public static final String KEY_HAS_EXCEPTION = DefinedAttribute.HAS_EXCEPTION.key;
	public static final String KEY_NANOTIME = DefinedAttribute.NANOTIME.key;
	public static final String KEY_PARAMS = DefinedAttribute.PARAMS.key;
	public static final String KEY_PARAM_VALUE = DefinedAttribute.PARAM_VALUE.key;
	public static final String KEY_RETURN_VALUE = DefinedAttribute.RETURN_VALUE.key;
	public static final String KEY_THREAD_ID = DefinedAttribute.THREAD_ID.key;
	public static final String KEY_TYPE = DefinedAttribute.TYPE.key;
	public static final String KEY_VALUE_TYPE = DefinedAttribute.VALUE_TYPE.key;

	/**
	 * Global prototype place holders. Need to be initialized by constructor.
	 */
	public static XAttributeLiteral ATTR_APP_NAME;
	public static XAttributeLiteral ATTR_APP_NODE;
	public static XAttributeLiteral ATTR_APP_SESSION;
	public static XAttributeLiteral ATTR_APP_TIER;
	public static XAttributeLiteral ATTR_CALLEE_CLASS;
	public static XAttributeLiteral ATTR_CALLEE_FILENAME;
	public static XAttributeLiteral ATTR_CALLEE_INSTANCEID;
	public static XAttributeBoolean ATTR_CALLEE_ISCONSTRUCTOR;
	public static XAttributeDiscrete ATTR_CALLEE_LINENR;
	public static XAttributeLiteral ATTR_CALLEE_METHOD;
	public static XAttributeLiteral ATTR_CALLEE_PACKAGE;
	public static XAttributeLiteral ATTR_CALLEE_PARAMSIG;
	public static XAttributeLiteral ATTR_CALLEE_RETURNSIG;
	public static XAttributeLiteral ATTR_CALLER_CLASS;
	public static XAttributeLiteral ATTR_CALLER_FILENAME;
	public static XAttributeLiteral ATTR_CALLER_INSTANCEID;
	public static XAttributeBoolean ATTR_CALLER_ISCONSTRUCTOR;
	public static XAttributeDiscrete ATTR_CALLER_LINENR;
	public static XAttributeLiteral ATTR_CALLER_METHOD;
	public static XAttributeLiteral ATTR_CALLER_PACKAGE;
	public static XAttributeLiteral ATTR_CALLER_PARAMSIG;
	public static XAttributeLiteral ATTR_CALLER_RETURNSIG;
	public static XAttributeLiteral ATTR_EX_CAUGHT;
	public static XAttributeLiteral ATTR_EX_THROWN;
	public static XAttributeBoolean ATTR_HAS_DATA;
	public static XAttributeBoolean ATTR_HAS_EXCEPTION;
	public static XAttributeDiscrete ATTR_NANOTIME;
	public static XAttributeList ATTR_PARAMS;
	public static XAttributeLiteral ATTR_PARAM_VALUE;
	public static XAttributeLiteral ATTR_RETURN_VALUE;
	public static XAttributeLiteral ATTR_THREAD_ID;
	public static XAttributeLiteral ATTR_TYPE;
	public static XAttributeLiteral ATTR_VALUE_TYPE;

	/**
	 * Singleton instance of this extension.
	 */
	private transient static XSoftwareEventExtension singleton = new XSoftwareEventExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XSoftwareEventExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Private constructor
	 */
	private XSoftwareEventExtension() {
		super("Software Event", PREFIX, EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();

		/*
		 * Initialize all defined attributes.
		 */
		for (DefinedAttribute attribute : DefinedAttribute.values()) {
			/*
			 * Initialize the prototype of the attribute. Depends on its type.
			 */
			switch (attribute.type) {
			case BOOLEAN: {
				attribute.setPrototype(factory.createAttributeBoolean(
						attribute.key, false, this));
				break;
			}
			case INT: {
				attribute.setPrototype(factory.createAttributeDiscrete(
						attribute.key, -1, this));
				break;
			}
			case LIST: {
				attribute.setPrototype(factory.createAttributeList(
						attribute.key, this));
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
			case LOG: {
				this.logAttributes
						.add((XAttribute) attribute.prototype.clone());
				break;
			}
			case META: {
				this.metaAttributes.add((XAttribute) attribute.prototype
						.clone());
				break;
			}
			}
			/*
			 * Initialize the proper global prototype place holder.
			 */
			switch (attribute) {
			case APP_NAME: {
				ATTR_APP_NAME = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case APP_NODE: {
				ATTR_APP_NODE = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case APP_SESSION: {
				ATTR_APP_SESSION = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case APP_TIER: {
				ATTR_APP_TIER = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLEE_CLASS: {
				ATTR_CALLEE_CLASS = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLEE_FILENAME: {
				ATTR_CALLEE_FILENAME = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLEE_ISCONSTRUCTOR: {
				ATTR_CALLEE_ISCONSTRUCTOR = (XAttributeBoolean) attribute.prototype;
				break;
			}
			case CALLEE_INSTANCEID: {
				ATTR_CALLEE_INSTANCEID = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLEE_LINENR: {
				ATTR_CALLEE_LINENR = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case CALLEE_METHOD: {
				ATTR_CALLEE_METHOD = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLEE_PACKAGE: {
				ATTR_CALLEE_PACKAGE = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLEE_PARAMSIG: {
				ATTR_CALLEE_PARAMSIG = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLEE_RETURNSIG: {
				ATTR_CALLEE_RETURNSIG = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLER_CLASS: {
				ATTR_CALLER_CLASS = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLER_FILENAME: {
				ATTR_CALLER_FILENAME = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLER_ISCONSTRUCTOR: {
				ATTR_CALLER_ISCONSTRUCTOR = (XAttributeBoolean) attribute.prototype;
				break;
			}
			case CALLER_INSTANCEID: {
				ATTR_CALLER_INSTANCEID = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLER_LINENR: {
				ATTR_CALLER_LINENR = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case CALLER_METHOD: {
				ATTR_CALLER_METHOD = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLER_PACKAGE: {
				ATTR_CALLER_PACKAGE = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLER_PARAMSIG: {
				ATTR_CALLER_PARAMSIG = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case CALLER_RETURNSIG: {
				ATTR_CALLER_RETURNSIG = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case EX_CAUGHT: {
				ATTR_EX_CAUGHT = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case EX_THROWN: {
				ATTR_EX_THROWN = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case HAS_DATA: {
				ATTR_HAS_DATA = (XAttributeBoolean) attribute.prototype;
				break;
			}
			case HAS_EXCEPTION: {
				ATTR_HAS_EXCEPTION = (XAttributeBoolean) attribute.prototype;
				break;
			}
			case NANOTIME: {
				ATTR_NANOTIME = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case PARAMS: {
				ATTR_PARAMS = (XAttributeList) attribute.prototype;
				break;
			}
			case PARAM_VALUE: {
				ATTR_PARAM_VALUE = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case RETURN_VALUE: {
				ATTR_RETURN_VALUE = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case THREAD_ID: {
				ATTR_THREAD_ID = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case TYPE: {
				ATTR_TYPE = (XAttributeLiteral) attribute.prototype;
				break;
			}
			case VALUE_TYPE: {
				ATTR_VALUE_TYPE = (XAttributeLiteral) attribute.prototype;
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
	public String extractAppName(XEvent event) {
		return extract(event, DefinedAttribute.APP_NAME, (String) null);
	}

	public XAttributeLiteral assignAppName(XEvent event, String appName) {
		return assign(event, DefinedAttribute.APP_NAME, appName);
	}

	public XAttributeLiteral removeAppName(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.APP_NAME);
	}

	public String extractAppNode(XEvent event) {
		return extract(event, DefinedAttribute.APP_NODE, (String) null);
	}

	public XAttributeLiteral assignAppNode(XEvent event, String appNode) {
		return assign(event, DefinedAttribute.APP_NODE, appNode);
	}

	public XAttributeLiteral removeAppNode(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.APP_NODE);
	}

	public String extractAppSession(XEvent event) {
		return extract(event, DefinedAttribute.APP_SESSION, (String) null);
	}

	public XAttributeLiteral assignAppSession(XEvent event, String appSession) {
		return assign(event, DefinedAttribute.APP_SESSION, appSession);
	}

	public XAttributeLiteral removeAppSession(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.APP_SESSION);
	}

	public String extractAppTier(XEvent event) {
		return extract(event, DefinedAttribute.APP_TIER, (String) null);
	}

	public XAttributeLiteral assignAppTier(XEvent event, String appTier) {
		return assign(event, DefinedAttribute.APP_TIER, appTier);
	}

	public XAttributeLiteral removeAppTier(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.APP_TIER);
	}

	public String extractCalleeClass(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_CLASS, (String) null);
	}

	public XAttributeLiteral assignCalleeClass(XEvent event, String calleeClass) {
		return assign(event, DefinedAttribute.CALLEE_CLASS, calleeClass);
	}

	public XAttributeLiteral removeCalleeClass(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.CALLEE_CLASS);
	}

	public String extractCalleeFilename(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_FILENAME, (String) null);
	}

	public XAttributeLiteral assignCalleeFilename(XEvent event,
			String calleeFilename) {
		return assign(event, DefinedAttribute.CALLEE_FILENAME, calleeFilename);
	}

	public XAttributeLiteral removeCalleeFilename(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLEE_FILENAME);
	}

	public String extractCalleeInstanceId(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_INSTANCEID, (String) null);
	}

	public XAttributeLiteral assignCalleeInstanceId(XEvent event,
			String calleeInstanceId) {
		return assign(event, DefinedAttribute.CALLEE_INSTANCEID,
				calleeInstanceId);
	}

	public XAttributeLiteral removeCalleeInstanceId(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLEE_INSTANCEID);
	}

	public boolean extractCalleeIsConstructor(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_ISCONSTRUCTOR, false);
	}

	public XAttributeBoolean assignCalleeIsConstructor(XEvent event,
			boolean calleeInstanceId) {
		return assign(event, DefinedAttribute.CALLEE_ISCONSTRUCTOR,
				calleeInstanceId);
	}

	public XAttributeBoolean removeCalleeIsConstructor(XEvent event) {
		return (XAttributeBoolean) remove(event,
				DefinedAttribute.CALLEE_ISCONSTRUCTOR);
	}

	public long extractCalleeLineNr(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_LINENR, -1);
	}

	public XAttributeDiscrete assignCalleeLineNr(XEvent event, long calleeLineNr) {
		return assign(event, DefinedAttribute.CALLEE_LINENR, calleeLineNr);
	}

	public XAttributeDiscrete removeCalleeLineNr(XEvent event) {
		return (XAttributeDiscrete) remove(event,
				DefinedAttribute.CALLEE_LINENR);
	}

	public String extractCalleeMethod(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_METHOD, (String) null);
	}

	public XAttributeLiteral assignCalleeMethod(XEvent event,
			String calleeMethod) {
		return assign(event, DefinedAttribute.CALLEE_METHOD, calleeMethod);
	}

	public XAttributeLiteral removeCalleeMethod(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.CALLEE_METHOD);
	}

	public String extractCalleePackage(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_PACKAGE, (String) null);
	}

	public XAttributeLiteral assignCalleePackage(XEvent event,
			String calleePackage) {
		return assign(event, DefinedAttribute.CALLEE_PACKAGE, calleePackage);
	}

	public XAttributeLiteral removeCalleePackage(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLEE_PACKAGE);
	}

	public String extractCalleeParamSig(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_PARAMSIG, (String) null);
	}

	public XAttributeLiteral assignCalleeParamSig(XEvent event,
			String calleeParamSig) {
		return assign(event, DefinedAttribute.CALLEE_PARAMSIG, calleeParamSig);
	}

	public XAttributeLiteral removeCalleeParamSig(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLEE_PARAMSIG);
	}

	public String extractCalleeReturnSig(XEvent event) {
		return extract(event, DefinedAttribute.CALLEE_RETURNSIG, (String) null);
	}

	public XAttributeLiteral assignCalleeReturnSig(XEvent event,
			String calleeReturnSig) {
		return assign(event, DefinedAttribute.CALLEE_RETURNSIG, calleeReturnSig);
	}

	public XAttributeLiteral removeCalleeReturnSig(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLEE_RETURNSIG);
	}

	public String extractCallerClass(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_CLASS, (String) null);
	}

	public XAttributeLiteral assignCallerClass(XEvent event, String callerClass) {
		return assign(event, DefinedAttribute.CALLER_CLASS, callerClass);
	}

	public XAttributeLiteral removeCallerClass(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.CALLER_CLASS);
	}

	public String extractCallerFilename(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_FILENAME, (String) null);
	}

	public XAttributeLiteral assignCallerFilename(XEvent event,
			String callerFilename) {
		return assign(event, DefinedAttribute.CALLER_FILENAME, callerFilename);
	}

	public XAttributeLiteral removeCallerFilename(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLER_FILENAME);
	}

	public String extractCallerInstanceId(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_INSTANCEID, (String) null);
	}

	public XAttributeLiteral assignCallerInstanceId(XEvent event,
			String callerInstanceId) {
		return assign(event, DefinedAttribute.CALLER_INSTANCEID,
				callerInstanceId);
	}

	public XAttributeLiteral removeCallerInstanceId(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLER_INSTANCEID);
	}

	public boolean extractCallerIsConstructor(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_ISCONSTRUCTOR, false);
	}

	public XAttributeBoolean assignCallerIsConstructor(XEvent event,
			boolean callerInstanceId) {
		return assign(event, DefinedAttribute.CALLER_ISCONSTRUCTOR,
				callerInstanceId);
	}

	public XAttributeBoolean removeCallerIsConstructor(XEvent event) {
		return (XAttributeBoolean) remove(event,
				DefinedAttribute.CALLER_ISCONSTRUCTOR);
	}

	public long extractCallerLineNr(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_LINENR, -1);
	}

	public XAttributeDiscrete assignCallerLineNr(XEvent event, long callerLineNr) {
		return assign(event, DefinedAttribute.CALLER_LINENR, callerLineNr);
	}

	public XAttributeDiscrete removeCallerLineNr(XEvent event) {
		return (XAttributeDiscrete) remove(event,
				DefinedAttribute.CALLER_LINENR);
	}

	public String extractCallerMethod(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_METHOD, (String) null);
	}

	public XAttributeLiteral assignCallerMethod(XEvent event,
			String callerMethod) {
		return assign(event, DefinedAttribute.CALLER_METHOD, callerMethod);
	}

	public XAttributeLiteral removeCallerMethod(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.CALLER_METHOD);
	}

	public String extractCallerPackage(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_PACKAGE, (String) null);
	}

	public XAttributeLiteral assignCallerPackage(XEvent event,
			String callerPackage) {
		return assign(event, DefinedAttribute.CALLER_PACKAGE, callerPackage);
	}

	public XAttributeLiteral removeCallerPackage(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLER_PACKAGE);
	}

	public String extractCallerParamSig(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_PARAMSIG, (String) null);
	}

	public XAttributeLiteral assignCallerParamSig(XEvent event,
			String callerParamSig) {
		return assign(event, DefinedAttribute.CALLER_PARAMSIG, callerParamSig);
	}

	public XAttributeLiteral removeCallerParamSig(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLER_PARAMSIG);
	}

	public String extractCallerReturnSig(XEvent event) {
		return extract(event, DefinedAttribute.CALLER_RETURNSIG, (String) null);
	}

	public XAttributeLiteral assignCallerReturnSig(XEvent event,
			String callerReturnSig) {
		return assign(event, DefinedAttribute.CALLER_RETURNSIG, callerReturnSig);
	}

	public XAttributeLiteral removeCallerReturnSig(XEvent event) {
		return (XAttributeLiteral) remove(event,
				DefinedAttribute.CALLER_RETURNSIG);
	}

	public String extractExCaught(XEvent event) {
		return extract(event, DefinedAttribute.EX_CAUGHT, (String) null);
	}

	public XAttributeLiteral assignExCaught(XEvent event, String exCaught) {
		return assign(event, DefinedAttribute.EX_CAUGHT, exCaught);
	}

	public XAttributeLiteral removeExCaught(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.EX_CAUGHT);
	}

	public String extractExThrown(XEvent event) {
		return extract(event, DefinedAttribute.EX_THROWN, (String) null);
	}

	public XAttributeLiteral assignExThrown(XEvent event, String exThrown) {
		return assign(event, DefinedAttribute.EX_THROWN, exThrown);
	}

	public XAttributeLiteral removeExThrown(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.EX_THROWN);
	}

	public boolean extractHasData(XLog log) {
		return extract(log, DefinedAttribute.HAS_DATA, false);
	}

	public XAttributeBoolean assignHasData(XLog log, boolean hasData) {
		return assign(log, DefinedAttribute.HAS_DATA, hasData);
	}

	public XAttributeBoolean removeHasData(XLog log) {
		return (XAttributeBoolean) remove(log, DefinedAttribute.HAS_DATA);
	}

	public boolean extractHasException(XLog log) {
		return extract(log, DefinedAttribute.HAS_EXCEPTION, false);
	}

	public XAttributeBoolean assignHasException(XLog log, boolean hasException) {
		return assign(log, DefinedAttribute.HAS_EXCEPTION, hasException);
	}

	public XAttributeBoolean removeHasException(XLog log) {
		return (XAttributeBoolean) remove(log, DefinedAttribute.HAS_EXCEPTION);
	}

	public long extractNanotime(XEvent event) {
		return extract(event, DefinedAttribute.NANOTIME, -1);
	}

	public XAttributeDiscrete assignNanotime(XEvent event, long nanotime) {
		return assign(event, DefinedAttribute.NANOTIME, nanotime);
	}

	public XAttributeDiscrete removeNanotime(XEvent event) {
		return (XAttributeDiscrete) remove(event, DefinedAttribute.NANOTIME);
	}

	public List<XAttribute> extractParams(XEvent event) {
		return extract(event, DefinedAttribute.PARAMS, (List<XAttribute>) null);
	}

	/*
	 * Initializes the list of paramValue attributes in the values element to
	 * the empty list. Use addParamValue to add paramValue attributes to this list.
	 */
	public XAttributeList assignParams(XEvent event) {
		return assign(event, DefinedAttribute.PARAMS);
	}

	public XAttributeList removeParams(XEvent event) {
		return (XAttributeList) remove(event, DefinedAttribute.PARAMS);
	}

	/*
	 * The exception to the rule. As paramValue is a direct child attribute of
	 * the values element, there is not parent attribute to have an assign or
	 * extract on. The parameter values need to be extracted by calling
	 * extractParams on the event, which returns the list of all paramValue
	 * attributes as contained in the values element.
	 */
	public XAttributeLiteral addParamValue(XAttributeList attribute,
			String paramValue) {
		return assignToValues(attribute, DefinedAttribute.PARAM_VALUE,
				paramValue);
	}

	public String extractReturnValue(XEvent event) {
		return extract(event, DefinedAttribute.RETURN_VALUE, (String) null);
	}

	public XAttributeLiteral assignReturnValue(XEvent event, String returnValue) {
		return assign(event, DefinedAttribute.RETURN_VALUE, returnValue);
	}

	public XAttributeLiteral removeReturnValue(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.RETURN_VALUE);
	}

	public String extractThreadId(XEvent event) {
		return extract(event, DefinedAttribute.THREAD_ID, (String) null);
	}

	public XAttributeLiteral assignThreadId(XEvent event, String threadId) {
		return assign(event, DefinedAttribute.THREAD_ID, threadId);
	}

	public XAttributeLiteral removeThreadId(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.THREAD_ID);
	}

	public SoftwareEventType extractType(XEvent event) {
		return extract(event, DefinedAttribute.TYPE, SoftwareEventType.UNKNOWN);
	}

	public XAttributeLiteral assignType(XEvent event, SoftwareEventType type) {
		return assign(event, DefinedAttribute.TYPE, type);
	}

	public XAttributeLiteral removeType(XEvent event) {
		return (XAttributeLiteral) remove(event, DefinedAttribute.TYPE);
	}

	public String extractValueType(XAttribute attribute) {
		return extract(attribute, DefinedAttribute.VALUE_TYPE, (String) null);
	}

	public XAttributeLiteral assignValueType(XAttribute attribute,
			String valueTYpe) {
		return assign(attribute, DefinedAttribute.VALUE_TYPE, valueTYpe);
	}

	public XAttributeLiteral removeValueType(XAttribute attribute) {
		return (XAttributeLiteral) remove(attribute,
				DefinedAttribute.VALUE_TYPE);
	}

	/*
	 * Helper functions
	 */
	private boolean extract(XAttributable element,
			DefinedAttribute definedAttribute, boolean defaultValue) {
		XAttribute attribute = element.getAttributes()
				.get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return ((XAttributeBoolean) attribute).getValue();
		}
	}

	private XAttributeBoolean assign(XAttributable element,
			DefinedAttribute definedAttribute, boolean value) {
		XAttributeBoolean attr = (XAttributeBoolean) definedAttribute.prototype
				.clone();
		attr.setValue(value);
		element.getAttributes().put(definedAttribute.key, attr);
		return attr;
	}

	private List<XAttribute> extract(XAttributable element,
			DefinedAttribute definedAttribute, List<XAttribute> defaultValue) {
		XAttribute attribute = element.getAttributes()
				.get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return new ArrayList<XAttribute>(
					((XAttributeList) attribute).getCollection());
		}
	}

	private XAttributeList assign(XAttributable element,
			DefinedAttribute definedAttribute) {
		XAttributeList attr = (XAttributeList) definedAttribute.prototype
				.clone();
		element.getAttributes().put(definedAttribute.key, attr);
		return attr;
	}

	private XAttributeLiteral assignToValues(XAttributeList list,
			DefinedAttribute definedAttribute, String value) {
		if (value != null) {
			XAttributeLiteral attr = (XAttributeLiteral) definedAttribute.prototype
					.clone();
			attr.setValue(value);
			list.addToCollection(attr);
			return attr;
		}
		return null;
	}

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

	private SoftwareEventType extract(XAttributable element,
			DefinedAttribute definedAttribute, SoftwareEventType defaultValue) {
		XAttribute attribute = element.getAttributes()
				.get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return SoftwareEventType.decode(((XAttributeLiteral) attribute)
					.getValue());
		}
	}

	private XAttributeLiteral assign(XAttributable element,
			DefinedAttribute definedAttribute, SoftwareEventType value) {
		if (value != null) {
			XAttributeLiteral attr = (XAttributeLiteral) definedAttribute.prototype
					.clone();
			attr.setValue(value.encoding);
			element.getAttributes().put(definedAttribute.key, attr);
			return attr;
		}
		return null;
	}

	private XAttribute remove(XAttributable element,
			DefinedAttribute definedAttribute) {
		return element.getAttributes().remove(definedAttribute.key);
	}

}
