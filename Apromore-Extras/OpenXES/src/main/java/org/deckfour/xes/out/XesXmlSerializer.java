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
package org.deckfour.xes.out;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.deckfour.spex.SXDocument;
import org.deckfour.spex.SXTag;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.logging.XLogging;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeCollection;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.util.XRuntimeUtils;
import org.deckfour.xes.util.XTokenHelper;
import org.deckfour.xes.util.XsDateTimeConversion;
import org.deckfour.xes.util.XsDateTimeConversionJava7;

/**
 * XES plain XML serialization for the XES format.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XesXmlSerializer implements XSerializer {

	protected XsDateTimeConversion xsDateTimeConversion = new XsDateTimeConversionJava7();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.out.XesSerializer#getDescription()
	 */
	public String getDescription() {
		return "XES XML Serialization";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.out.XesSerializer#getName()
	 */
	public String getName() {
		return "XES XML";
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
		return new String[] { "xes" };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.out.XesSerializer#serialize(org.deckfour.xes.model.XLog,
	 * java.io.OutputStream)
	 */
	public void serialize(XLog log, OutputStream out) throws IOException {
		XLogging.log("start serializing log to XES.XML",
				XLogging.Importance.DEBUG);
		long start = System.currentTimeMillis();
		SXDocument doc = new SXDocument(out);
		doc.addComment("This file has been generated with the OpenXES library. It conforms");
		doc.addComment("to the XML serialization of the XES standard for log storage and");
		doc.addComment("management.");
		doc.addComment("XES standard version: " + XRuntimeUtils.XES_VERSION);
		doc.addComment("OpenXES library version: "
				+ XRuntimeUtils.OPENXES_VERSION);
		doc.addComment("OpenXES is available from http://www.openxes.org/");
		SXTag logTag = doc.addNode("log");
		logTag.addAttribute("xes.version", XRuntimeUtils.XES_VERSION);
		logTag.addAttribute("xes.features", "nested-attributes");
		logTag.addAttribute("openxes.version", XRuntimeUtils.OPENXES_VERSION);
//		logTag.addAttribute("xmlns", "http://www.xes-standard.org/");
		// define extensions
		for (XExtension extension : log.getExtensions()) {
			SXTag extensionTag = logTag.addChildNode("extension");
			extensionTag.addAttribute("name", extension.getName());
			extensionTag.addAttribute("prefix", extension.getPrefix());
			extensionTag.addAttribute("uri", extension.getUri().toString());
		}
		// define global attributes
		addGlobalAttributes(logTag, "trace", log.getGlobalTraceAttributes());
		addGlobalAttributes(logTag, "event", log.getGlobalEventAttributes());
		// define classifiers
		for (XEventClassifier classifier : log.getClassifiers()) {
			if (classifier instanceof XEventAttributeClassifier) {
				XEventAttributeClassifier attrClass = (XEventAttributeClassifier) classifier;
				SXTag clsTag = logTag.addChildNode("classifier");
				clsTag.addAttribute("name", attrClass.name());
				clsTag.addAttribute("keys", XTokenHelper
						.formatTokenString((List<String>) Arrays
								.asList(attrClass.getDefiningAttributeKeys())));
			}
		}
		// add log attributes
		addAttributes(logTag, log.getAttributes().values());
		for (XTrace trace : log) {
			SXTag traceTag = logTag.addChildNode("trace");
			addAttributes(traceTag, trace.getAttributes().values());
			for (XEvent event : trace) {
				SXTag eventTag = traceTag.addChildNode("event");
				addAttributes(eventTag, event.getAttributes().values());
			}
		}
		//
		doc.close();
		String duration = " (" + (System.currentTimeMillis() - start)
				+ " msec.)";
		XLogging.log("finished serializing log" + duration,
				XLogging.Importance.DEBUG);
	}

	/**
	 * Helper method for defining global attributes on a given scope.
	 */
	protected void addGlobalAttributes(SXTag parent, String scope,
			List<XAttribute> attributes) throws IOException {
		if (attributes.size() > 0) {
			SXTag guaranteedNode = parent.addChildNode("global");
			guaranteedNode.addAttribute("scope", scope);
			addAttributes(guaranteedNode, attributes);
		}
	}

	/**
	 * Helper method, adds the given collection of attributes to the given Tag.
	 * 
	 * @param tag
	 *            Tag to add attributes to.
	 * @param attributes
	 *            The attributes to add.
	 */
	protected void addAttributes(SXTag tag, Collection<XAttribute> attributes)
			throws IOException {
		for (XAttribute attribute : attributes) {
			SXTag attributeTag;
			if (attribute instanceof XAttributeList) {
				attributeTag = tag.addChildNode("list");
				attributeTag.addAttribute("key", attribute.getKey());
			} else if (attribute instanceof XAttributeContainer) {
				attributeTag = tag.addChildNode("container");
				attributeTag.addAttribute("key", attribute.getKey());
			} else if (attribute instanceof XAttributeLiteral) {
				attributeTag = tag.addChildNode("string");
				attributeTag.addAttribute("key", attribute.getKey());
				attributeTag.addAttribute("value", attribute.toString());
			} else if (attribute instanceof XAttributeDiscrete) {
				attributeTag = tag.addChildNode("int");
				attributeTag.addAttribute("key", attribute.getKey());
				attributeTag.addAttribute("value", attribute.toString());
			} else if (attribute instanceof XAttributeContinuous) {
				attributeTag = tag.addChildNode("float");
				attributeTag.addAttribute("key", attribute.getKey());
				attributeTag.addAttribute("value", attribute.toString());
			} else if (attribute instanceof XAttributeTimestamp) {
				attributeTag = tag.addChildNode("date");
				attributeTag.addAttribute("key", attribute.getKey());
				Date timestamp = ((XAttributeTimestamp) attribute).getValue();
				attributeTag.addAttribute("value",
						xsDateTimeConversion.format(timestamp));
			} else if (attribute instanceof XAttributeBoolean) {
				attributeTag = tag.addChildNode("boolean");
				attributeTag.addAttribute("key", attribute.getKey());
				attributeTag.addAttribute("value", attribute.toString());
			} else if (attribute instanceof XAttributeID) {
				attributeTag = tag.addChildNode("id");
				attributeTag.addAttribute("key", attribute.getKey());
				attributeTag.addAttribute("value", attribute.toString());
			} else {
				throw new IOException("Unknown attribute type!");
			}
			if (attribute instanceof XAttributeCollection) {
				/*
				 * Use order as specified by the collection.
				 */
				Collection<XAttribute> childAttributes = ((XAttributeCollection) attribute).getCollection();
				addAttributes(attributeTag, childAttributes);
			} else {
				// FM: First check for existence of nested-attributes 
				//     to avoid unnecessary creation of XAttributeMapLazyImpl instances
				if (attribute.hasAttributes()) {
					addAttributes(attributeTag, attribute.getAttributes().values());
				}
			}
		}
	}

	/**
	 * toString() defaults to getName().
	 */
	public String toString() {
		return this.getName();
	}

}
