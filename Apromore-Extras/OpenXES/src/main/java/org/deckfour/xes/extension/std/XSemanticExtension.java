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
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;

/**
 * This extension adds semantic attributes to event log objects.
 * 
 * These semantic attributes reference concepts, which are represented by event
 * log objects, as unique URIs.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XSemanticExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8755188345751379342L;
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/semantic.xesext");
	/**
	 * Key for the model references attribute.
	 */
	public static final String KEY_MODELREFERENCE = "semantic:modelReference";
	/**
	 * Model references attribute prototype.
	 */
	public static XAttributeLiteral ATTR_MODELREFERENCE;

	/**
	 * Singleton instance of this extension.
	 */
	private static XSemanticExtension singleton = new XSemanticExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XSemanticExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Creates a new instance (hidden constructor).
	 */
	private XSemanticExtension() {
		super("Semantic", "semantic", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_MODELREFERENCE = factory.createAttributeLiteral(
				KEY_MODELREFERENCE, "__INVALID__", this);
		this.logAttributes.add((XAttribute) ATTR_MODELREFERENCE.clone());
		this.traceAttributes.add((XAttribute) ATTR_MODELREFERENCE.clone());
		this.eventAttributes.add((XAttribute) ATTR_MODELREFERENCE.clone());
		this.metaAttributes.add((XAttribute) ATTR_MODELREFERENCE.clone());
		// register mapping aliases
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_MODELREFERENCE,
				"Ontology Model Reference");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_GERMAN, KEY_MODELREFERENCE,
				"Ontologie-Modellreferenz");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_FRENCH, KEY_MODELREFERENCE,
				"Référence au Modèle Ontologique");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_SPANISH, KEY_MODELREFERENCE,
				"Referencia de Modelo Ontológico");
		XGlobalAttributeNameMap.instance().registerMapping(
				XGlobalAttributeNameMap.MAPPING_PORTUGUESE, KEY_MODELREFERENCE,
				"Referência de Modelo Ontológico");
	}

	/**
	 * Retrieves the list of model references which describe a log element
	 * (archive, log, trace, event, attribute).
	 * 
	 * @param target
	 *            Any log element (i.e., archive, log, trace, event, or
	 *            attribute) to be queried.
	 * @return The list of model references, as a list of strings, referred to
	 *         by this element.
	 */
	public List<String> extractModelReferences(XAttributable target) {
		ArrayList<String> modelReferences = new ArrayList<String>();
		XAttributeLiteral modelReferenceAttribute = (XAttributeLiteral) target
				.getAttributes().get(KEY_MODELREFERENCE);
		if (modelReferenceAttribute != null) {
			String refString = modelReferenceAttribute.getValue().trim();
			for (String reference : refString.split("\\s")) {
				modelReferences.add(reference.trim());
			}
		}
		return modelReferences;
	}

	/**
	 * Retrieves the list of model reference URIs which describe a log element
	 * (archive, log, trace, event, attribute).
	 * 
	 * @param target
	 *            Any log element (i.e., archive, log, trace, event, or
	 *            attribute) to be queried.
	 * @return The list of model references, as a list of URIs, referred to by
	 *         this element.
	 */
	public List<URI> extractModelReferenceURIs(XAttributable target) {
		List<String> refStrings = extractModelReferences(target);
		List<URI> refURIs = new ArrayList<URI>(refStrings.size());
		for (String refString : refStrings) {
			refURIs.add(URI.create(refString));
		}
		return refURIs;
	}

	/**
	 * Assigns to a log element (i.e., archive, log, trace, event, or attribute)
	 * a list of model references.
	 * 
	 * @param target
	 *            Any log element (i.e., archive, log, trace, event, or
	 *            attribute) to be assigned references to.
	 * @param modelReferences
	 *            The list of model references, as a list of strings, referred
	 *            to by this element.
	 */
	public void assignModelReferences(XAttributable target,
			List<String> modelReferences) {
		StringBuilder sb = new StringBuilder();
		for (String ref : modelReferences) {
			sb.append(ref);
			sb.append(" ");
		}
		if (sb.toString().trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_MODELREFERENCE
					.clone();
			attr.setValue(sb.toString().trim());
			target.getAttributes().put(KEY_MODELREFERENCE, attr);
		}
	}

	/**
	 * Assigns to a log element (i.e., archive, log, trace, event, or attribute)
	 * a list of model references.
	 * 
	 * @param target
	 *            Any log element (i.e., archive, log, trace, event, or
	 *            attribute) to be assigned references to.
	 * @param modelReferenceURIs
	 *            The list of model references, as a list of URIs, referred to
	 *            by this element.
	 */
	public void assignModelReferenceUris(XAttributable target,
			List<URI> modelReferenceURIs) {
		StringBuilder sb = new StringBuilder();
		for (URI ref : modelReferenceURIs) {
			sb.append(ref.toString());
			sb.append(" ");
		}
		if (sb.toString().trim().length() > 0) {
			XAttributeLiteral attr = (XAttributeLiteral) ATTR_MODELREFERENCE
					.clone();
			attr.setValue(sb.toString().trim());
			target.getAttributes().put(KEY_MODELREFERENCE, attr);
		}
	}

}
