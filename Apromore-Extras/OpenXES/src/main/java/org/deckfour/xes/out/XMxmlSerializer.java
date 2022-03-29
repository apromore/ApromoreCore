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
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
package org.deckfour.xes.out;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import org.deckfour.spex.SXDocument;
import org.deckfour.spex.SXTag;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XSemanticExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.logging.XLogging;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.util.XRuntimeUtils;
import org.deckfour.xes.util.XsDateTimeConversion;

/**
 * MXML serialization for XES data (legacy implementation). Note that this
 * serialization may be lossy, you should preferrably use the XES.XML
 * serialization for XES data.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 */
public class XMxmlSerializer implements XSerializer {

	/**
	 * Stores known event types internally for MXML.
	 */
	protected UnifiedSet<String> knownTypes;

	protected XsDateTimeConversion xsDateTimeConversion = new XsDateTimeConversion();

	public XMxmlSerializer() {
		knownTypes = new UnifiedSet<String>();
		knownTypes.add("schedule");
		knownTypes.add("assign");
		knownTypes.add("withdraw");
		knownTypes.add("reassign");
		knownTypes.add("start");
		knownTypes.add("suspend");
		knownTypes.add("resume");
		knownTypes.add("pi_abort");
		knownTypes.add("ate_abort");
		knownTypes.add("complete");
		knownTypes.add("autoskip");
		knownTypes.add("manualskip");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.out.XesSerializer#getDescription()
	 */
	public String getDescription() {
		return "MXML serialization (legacy)";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.out.XesSerializer#getName()
	 */
	public String getName() {
		return "MXML";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.out.XesSerializer#getAuthor()
	 */
	public String getAuthor() {
		return "Christian W. Günther";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.out.XesSerializer#getSuffices()
	 */
	public String[] getSuffices() {
		return new String[] { "mxml" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.out.XesSerializer#serialize(org.deckfour.xes.model.XLog,
	 * java.io.OutputStream)
	 */
	public void serialize(XLog log, OutputStream out) throws IOException {
		XLogging.log("start serializing log to MXML", XLogging.Importance.DEBUG);
		long start = System.currentTimeMillis();
		SXDocument doc = new SXDocument(out);
		doc.addComment("This file has been generated with the OpenXES library. It conforms");
		doc.addComment("to the legacy MXML standard for log storage and management.");
		doc.addComment("OpenXES library version: "
				+ XRuntimeUtils.OPENXES_VERSION);
		doc.addComment("OpenXES is available from http://www.xes-standard.org/");
		SXTag root = doc.addNode("WorkflowLog");
		SXTag source = root.addChildNode("Source");
		source.addAttribute("program", "XES MXML serialization");
		source.addAttribute("openxes.version", XRuntimeUtils.OPENXES_VERSION);
		SXTag process = root.addChildNode("Process");
		String id = XConceptExtension.instance().extractName(log);
		process.addAttribute("id", (id == null ? "none" : id));
		process.addAttribute("description", "process with id "
				+ XConceptExtension.instance().extractName(log));
		addModelReference(log, process);
		addAttributes(process, log.getAttributes().values());
		for (XTrace trace : log) {
			SXTag instance = process.addChildNode("ProcessInstance");
			instance.addAttribute("id", XConceptExtension.instance()
					.extractName(trace));
			instance.addAttribute("description", "instance with id "
					+ XConceptExtension.instance().extractName(trace));
			addModelReference(trace, instance);
			addAttributes(instance, trace.getAttributes().values());
			for (XEvent event : trace) {
				SXTag ate = instance.addChildNode("AuditTrailEntry");
				addAttributes(ate, event.getAttributes().values());
				SXTag wfme = ate.addChildNode("WorkflowModelElement");
				addModelReference(event, wfme);
				wfme.addTextNode(XConceptExtension.instance()
						.extractName(event));
				SXTag type = ate.addChildNode("EventType");
				XAttributeLiteral typeAttr = (XAttributeLiteral) event
						.getAttributes()
						.get(XLifecycleExtension.KEY_TRANSITION);
				if (typeAttr != null) {
					addModelReference(typeAttr, type);
					String typeStr = typeAttr.getValue().trim().toLowerCase();
					if (knownTypes.contains(typeStr)) {
						type.addTextNode(typeStr);
					} else {
						type.addAttribute("unknownType", typeAttr.getValue());
						type.addTextNode("unknown");
					}
				} else {
					type.addTextNode("complete");
				}
				XAttributeLiteral originatorAttr = (XAttributeLiteral) event
						.getAttributes().get(
								XOrganizationalExtension.KEY_RESOURCE);
				if (originatorAttr == null) {
					originatorAttr = (XAttributeLiteral) event.getAttributes()
							.get(XOrganizationalExtension.KEY_ROLE);
				}
				if (originatorAttr == null) {
					originatorAttr = (XAttributeLiteral) event.getAttributes()
							.get(XOrganizationalExtension.KEY_GROUP);
				}
				if (originatorAttr != null) {
					SXTag originator = ate.addChildNode("originator");
					addModelReference(originatorAttr, originator);
					originator.addTextNode(originatorAttr.getValue());
				}
				XAttributeTimestamp timestampAttr = (XAttributeTimestamp) event
						.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
				if (timestampAttr != null) {
					SXTag timestamp = ate.addChildNode("timestamp");
					addModelReference(timestampAttr, timestamp);
					Date date = timestampAttr.getValue();
					timestamp.addTextNode(xsDateTimeConversion.format(date));
				}
			}
		}
		doc.close();
		String duration = " (" + (System.currentTimeMillis() - start)
				+ " msec.)";
		XLogging.log("finished serializing log" + duration,
				XLogging.Importance.DEBUG);
	}

	/**
	 * Helper method, adds attributes to a tag.
	 * 
	 * @param node
	 *            The tag to add attributes to.
	 * @param attributes
	 *            The attributes to add.
	 */
	protected void addAttributes(SXTag node, Collection<XAttribute> attributes)
			throws IOException {
		SXTag data = node.addChildNode("Data");
		addAttributes(data, "", attributes);
	}

	/**
	 * Helper method, adds attributes to a tag.
	 * 
	 * @param dataNode
	 *            The tag to add attributes to.
	 * @param keyPrefix
	 *            the Key prefix of attributes.
	 * @param attributes
	 *            The attributes to add.
	 */
	protected void addAttributes(SXTag dataNode, String keyPrefix,
			Collection<XAttribute> attributes) throws IOException {
		for (XAttribute attribute : attributes) {
			// skip attributes defined by standard extensions
			// to ensure parity with MXML input files.
			if (// BVD: KEEP ALL Attributes
				// attribute.getKey().equals(XConceptExtension.KEY_NAME)
				// || attribute.getKey()
				// .equals(XConceptExtension.KEY_INSTANCE)
				// || attribute.getKey().equals(XLifecycleExtension.KEY_MODEL)
				// || attribute.getKey().equals(
				// XLifecycleExtension.KEY_TRANSITION)
				// || attribute.getKey().equals(
				// XOrganizationalExtension.KEY_GROUP)
				// || attribute.getKey().equals(
				// XOrganizationalExtension.KEY_RESOURCE)
				// || attribute.getKey().equals(
				// XOrganizationalExtension.KEY_ROLE)||
			attribute.getKey().equals(XSemanticExtension.KEY_MODELREFERENCE)
			// || attribute.getKey().equals(XTimeExtension.KEY_TIMESTAMP)
			) {
				continue;
			}
			SXTag attributeTag = dataNode.addChildNode("attribute");
			attributeTag.addAttribute("name", keyPrefix + attribute.getKey());
			addModelReference(attribute, attributeTag);
			attributeTag.addTextNode(attribute.toString());
			Collection<XAttribute> subAttributes = attribute.getAttributes()
					.values();
			if (subAttributes.size() > 0) {
				String subKeyPrefix = attribute.getKey();
				if (keyPrefix.length() > 0) {
					subKeyPrefix = keyPrefix + ":" + subKeyPrefix;
				}
				addAttributes(dataNode, subKeyPrefix, subAttributes);
			}
		}
	}

	/**
	 * Helper method, adds all model references of an attributable to the given
	 * tag.
	 * 
	 * @param object
	 *            Attributable element.
	 * @param target
	 *            Tag to add model references to.
	 */
	protected void addModelReference(XAttributable object, SXTag target)
			throws IOException {
		XAttributeLiteral modelRefAttr = (XAttributeLiteral) object
				.getAttributes().get(XSemanticExtension.KEY_MODELREFERENCE);
		if (modelRefAttr != null) {
			target.addAttribute("modelReference", modelRefAttr.getValue());
		}
	}

	/**
	 * toString() defaults to getName().
	 */
	public String toString() {
		return this.getName();
	}

}
