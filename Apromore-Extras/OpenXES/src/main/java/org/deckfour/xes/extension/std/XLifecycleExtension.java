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
import org.deckfour.xes.model.XLog;

/**
 * Extension defining additional attributes for the event lifecycle.
 * 
 * Lifecycles define a set of states for activities, with an accompanying set of
 * transitions between those states. Any event which is referring to by a
 * lifecycle represents a certain transition of an activity within that
 * lifecycle.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XLifecycleExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7368474477345685085L;

	/**
	 * Enumeration for the standard lifecycle model.
	 * 
	 * @author Christian W. Guenther (christian@deckfour.org)
	 */
	public enum StandardModel {

		SCHEDULE("schedule"), ASSIGN("assign"), WITHDRAW("withdraw"), REASSIGN(
				"reassign"), START("start"), SUSPEND("suspend"), RESUME(
				"resume"), PI_ABORT("pi_abort"), ATE_ABORT("ate_abort"), COMPLETE(
				"complete"), AUTOSKIP("autoskip"), MANUALSKIP("manualskip"), UNKNOWN(
				"unknown");

		/**
		 * Encoding string, i.e. name.
		 */
		private final String encoding;

		/**
		 * Creates a new instance.
		 * 
		 * @param encoding
		 *            Encoding of transition.
		 */
		private StandardModel(String encoding) {
			this.encoding = encoding;
		}

		/**
		 * Retrieves the encoding of the standard transition.
		 * 
		 * @return Encoding of this standard transition.
		 */
		public String getEncoding() {
			return encoding;
		}

		/**
		 * Decodes any encoding string, referring to the respective
		 * standard-model lifecycle transition object in this enum.
		 * 
		 * @param encoding
		 *            Encoding string.
		 * @return Standard-model transition.
		 */
		public static StandardModel decode(String encoding) {
			encoding = encoding.trim().toLowerCase();
			for (StandardModel transition : StandardModel.values()) {
				if (transition.encoding.equals(encoding)) {
					return transition;
				}
			}
			return StandardModel.UNKNOWN;
		}

	}

	/**
	 * The unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/lifecycle.xesext");
	/**
	 * Key for the lifecycle model attribute.
	 */
	public static final String KEY_MODEL = "lifecycle:model";
	/**
	 * Key for the transition attribute.
	 */
	public static final String KEY_TRANSITION = "lifecycle:transition";
	/**
	 * Value for the lifecycle model attribute referring to the standard model.
	 */
	public static final String VALUE_MODEL_STANDARD = "standard";
	/**
	 * Lifecycle model attribute prototype
	 */
	public static XAttributeLiteral ATTR_MODEL;
	/**
	 * Transition attribute prototype
	 */
	public static XAttributeLiteral ATTR_TRANSITION;

	/**
	 * Singleton instance of this extension.
	 */
	private static XLifecycleExtension singleton = new XLifecycleExtension();

	/**
	 * Provides static access to the singleton instance of this extension.
	 * 
	 * @return Singleton instance.
	 */
	public static XLifecycleExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Creates a new instance (hidden constructor).
	 */
	private XLifecycleExtension() {
		super("Lifecycle", "lifecycle", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_MODEL = factory.createAttributeLiteral(KEY_MODEL,
				VALUE_MODEL_STANDARD, this);
		ATTR_TRANSITION = factory.createAttributeLiteral(KEY_TRANSITION,
				StandardModel.COMPLETE.getEncoding(), this);
		this.logAttributes.add((XAttributeLiteral) ATTR_MODEL.clone());
		this.eventAttributes.add((XAttributeLiteral) ATTR_TRANSITION.clone());
		// register aliases
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_MODEL,
				"Lifecycle Model");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_TRANSITION,
				"Lifecycle Transition");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_MODEL,
				"Lebenszyklus-Model");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_TRANSITION,
				"Lebenszyklus-Transition");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_FRENCH, KEY_MODEL,
				"Modèle du Cycle Vital");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_FRENCH, KEY_TRANSITION,
				"Transition en Cycle Vital");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_MODEL,
				"Modelo de Ciclo de Vida");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_TRANSITION,
				"Transición en Ciclo de Vida");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_MODEL,
				"Modelo do Ciclo de Vida");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_TRANSITION,
				"Transição do Ciclo de Vida");
	}

	/**
	 * Extracts the lifecycle model identifier from a given log.
	 * 
	 * @param log
	 *            Event log.
	 * @return Lifecycle model identifier string.
	 */
	public String extractModel(XLog log) {
		XAttribute attribute = log.getAttributes().get(KEY_MODEL);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	/**
	 * Assigns a value for the lifecycle model identifier to a given log.
	 * 
	 * @param log
	 *            Log to be tagged.
	 * @param model
	 *            Lifecycle model identifier string to be used.
	 */
	public void assignModel(XLog log, String model) {
		if (model != null && model.trim().length() > 0) {
			XAttributeLiteral modelAttr = (XAttributeLiteral) ATTR_MODEL
					.clone();
			modelAttr.setValue(model.trim());
			log.getAttributes().put(KEY_MODEL, modelAttr);
		}
	}

	/**
	 * Checks, whether a given log uses the standard model for lifecycle
	 * transitions.
	 * 
	 * @param log
	 *            Log to be checked.
	 * @return Returns true, if the log indeed uses the standard lifecycle
	 *         model.
	 */
	public boolean usesStandardModel(XLog log) {
		String model = extractModel(log);
		if (model == null) {
			return false;
		} else if (model.trim().equals(VALUE_MODEL_STANDARD)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Extracts the lifecycle transition string from a given event.
	 * 
	 * @param event
	 *            An event.
	 * @return The lifecycle transition string of this event. Can be
	 *         <code>null</code>, if not defined.
	 */
	public String extractTransition(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_TRANSITION);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeLiteral) attribute).getValue();
		}
	}

	/**
	 * Extracts the standard lifecycle transition object from a given event.
	 * 
	 * @param event
	 *            An event.
	 * @return The standard lifecycle transition instance of this event. Can be
	 *         <code>null</code>, if not defined.
	 */
	public StandardModel extractStandardTransition(XEvent event) {
		String transition = extractTransition(event);
		if (transition != null) {
			return StandardModel.decode(transition);
		} else {
			return null;
		}
	}

	/**
	 * Assigns a lifecycle transition string to the given event.
	 * 
	 * @param event
	 *            Event to be tagged.
	 * @param transition
	 *            Lifecycle transition string to be assigned.
	 */
	public void assignTransition(XEvent event, String transition) {
		if (transition != null && transition.trim().length() > 0) {
			XAttributeLiteral transAttr = (XAttributeLiteral) ATTR_TRANSITION
					.clone();
			transAttr.setValue(transition.trim());
			event.getAttributes().put(KEY_TRANSITION, transAttr);
		}
	}

	/**
	 * Assigns a standard lifecycle transition to the given event.
	 * 
	 * @param event
	 *            Event to be tagged.
	 * @param transition
	 *            Standard lifecycle transition to be assigned.
	 */
	public void assignStandardTransition(XEvent event, StandardModel transition) {
		assignTransition(event, transition.getEncoding());
	}

}
