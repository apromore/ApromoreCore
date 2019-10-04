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
package org.deckfour.xes.in;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.classification.XEventResourceClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XSemanticExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.logging.XLogging;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.buffered.XTraceBufferedImpl;
import org.deckfour.xes.util.XsDateTimeConversion;
import org.deckfour.xes.util.XsDateTimeConversionJava7;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for the MXML format for event logs (deprecated).
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XMxmlParser extends XParser {
	
	protected XsDateTimeConversion xsDateTimeConversion = new XsDateTimeConversionJava7();

	/**
	 * Standard classifier used for MXML event logs.
	 */
	public static XEventClassifier MXML_STANDARD_CLASSIFIER;
	/**
	 * Event name classifier used for MXML event logs.
	 */
	public static XEventClassifier MXML_EVENT_NAME_CLASSIFIER;
	/**
	 * Originator classifier used for MXML event logs.
	 */
	public static XEventClassifier MXML_ORIGINATOR_CLASSIFIER;
	/**
	 * Collection of standard classifiers used for MXML event logs.
	 */
	public static List<XEventClassifier> MXML_CLASSIFIERS;
	
	static {
		// initialize MXML standard classifiers
		MXML_EVENT_NAME_CLASSIFIER = new XEventNameClassifier();
		MXML_ORIGINATOR_CLASSIFIER = new XEventResourceClassifier();
		MXML_STANDARD_CLASSIFIER = new XEventAttributeClassifier(
				"MXML Legacy Classifier",
				XConceptExtension.KEY_NAME, 
				XLifecycleExtension.KEY_TRANSITION);
		MXML_CLASSIFIERS = new ArrayList<XEventClassifier>();
		MXML_CLASSIFIERS.add(MXML_STANDARD_CLASSIFIER);
		MXML_CLASSIFIERS.add(MXML_EVENT_NAME_CLASSIFIER);
		MXML_CLASSIFIERS.add(MXML_ORIGINATOR_CLASSIFIER);
	}
	
	
	/**
	 * Factory to use for XES model assembly.
	 */
	private XFactory factory;
	
	
	/**
	 * Creates a new MXML parser instance.
	 * 
	 * @param factory The factory to use for XES model
	 *  building.
	 */
	public XMxmlParser(XFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Creates a new MXML parser instance, using the
	 * current standard factory for XES model building.
	 */
	public XMxmlParser() {
		this(XFactoryRegistry.instance().currentDefault());
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XParser#author()
	 */
	@Override
	public String author() {
		return "Christian W. Günther";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XParser#canParse(java.io.File)
	 */
	@Override
	public boolean canParse(File file) {
		String filename = file.getName();
		return endsWithIgnoreCase(filename, ".mxml") || endsWithIgnoreCase(filename, ".xml");
//		String suffix = filename.substring(filename.length() - 4);
//		if(suffix.equalsIgnoreCase("mxml")) {
//			return true;
//		} else {
//			suffix = filename.substring(filename.length() - 3);
//			return suffix.equalsIgnoreCase("xml");
//		}
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XParser#description()
	 */
	@Override
	public String description() {
		return "Reads XES models from legacy MXML serializations";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.in.XParser#name()
	 */
	@Override
	public String name() {
		return "MXML";
	}
	
	/**
	 * Parses a set of logs from the given input stream, which is 
	 * supposed to deliver an MXML serialization.
	 * 
	 * @param is Input stream, which is supposed to deliver
	 * an MXML serialization.
	 * @return The parsed list of logs.
	 */
	public List<XLog> parse(InputStream is) throws Exception {
		BufferedInputStream bis = new BufferedInputStream(is);
		// set up a specialized SAX2 handler to fill the container
		MxmlHandler handler = new MxmlHandler();
		// set up SAX parser and parse provided log file into the container
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		parser.parse(bis, handler);
		bis.close();
		return handler.getLogs();
	}
	
	/**
	 * This class implements a SAX2 handler for sequential
	 * parsing of MXML documents. It is geared towards
	 * directly constructing a log reader's data
	 * structures, of which it is a protected class.
	 *
	 * @author Christian W. Guenther (christian at deckfour dot org)
	 */
	protected class MxmlHandler extends DefaultHandler {

		/**
		 * Buffer for characters.
		 */
		protected StringBuffer buffer = null;
		// buffering attributes, to keep data between
		// start and end element events.
		protected ArrayList<XLog> logs = new ArrayList<XLog>();
		protected XLog currentProcess = null;
		protected XTrace currentInstance = null;
		protected XEvent entry = null;
		protected XAttributeLiteral sourceAttribute = null;
		protected XAttributeLiteral genericAttribute = null;
		protected XAttributeLiteral eventTypeAttribute = null;
		protected XAttributeLiteral originatorAttribute = null;
		protected boolean sourceOpen = false;
		protected Date timestamp = null;
		protected Date lastTimestamp = null;
		protected int numUnorderedEntries = 0;

		/**
		 * Creates a new SAX2 handler instance.
		 * @param aData Parent container to store data to.
		 * @param aFile LogFile used for parsing.
		 */
		protected MxmlHandler() {
			buffer = new StringBuffer();
			entry = null;
			sourceOpen = false;
			currentProcess = null;
			currentInstance = null;
			lastTimestamp = null;
			numUnorderedEntries = 0;
		}
		
		public List<XLog> getLogs() {
			return logs;
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			String tagName = localName;
			if (tagName.equalsIgnoreCase("")) {
				tagName = qName;
			}
			// probe element type
			if (tagName.equalsIgnoreCase("WorkflowLog")) {
				// ignore
			} else if (tagName.equalsIgnoreCase("Source")) {
				// start source
				sourceOpen = true;
				String program = attributes.getValue(attributes.getIndex("program"));
				sourceAttribute = factory.createAttributeLiteral("source", program, null);
				addModelReferences(attributes, sourceAttribute);
			} else if (tagName.equalsIgnoreCase("Process")) {
				// check if process is already contained
				String procId = attributes.getValue("id");
				String procDescr = attributes.getValue("description");
				currentProcess = factory.createLog();
				currentProcess.getExtensions().add(XConceptExtension.instance());
				currentProcess.getExtensions().add(XOrganizationalExtension.instance());
				currentProcess.getExtensions().add(XLifecycleExtension.instance());
				currentProcess.getExtensions().add(XSemanticExtension.instance());
				currentProcess.getExtensions().add(XTimeExtension.instance());
				if (sourceAttribute != null) {
					currentProcess.getAttributes().put(sourceAttribute.getKey(), sourceAttribute);
				}
				XConceptExtension.instance().assignName(currentProcess, procId);
				XLifecycleExtension.instance().assignModel(currentProcess, XLifecycleExtension.VALUE_MODEL_STANDARD);
				if(procDescr != null && procDescr.trim().length() > 0) {
					XAttributeLiteral description = factory.createAttributeLiteral("description", procDescr, null);
					currentProcess.getAttributes().put(description.getKey(), description);
				}
				addModelReferences(attributes, currentProcess);
			} else if (tagName.equalsIgnoreCase("ProcessInstance")) {
				// start process instance
				currentInstance = factory.createTrace();
				XConceptExtension.instance().assignName(currentInstance, attributes.getValue("id"));
				String descriptionString = attributes.getValue("description");
				if(descriptionString != null && descriptionString.trim().length() > 0) {
					XAttribute description = factory.createAttributeLiteral("description", descriptionString, null);
					currentInstance.getAttributes().put(description.getKey(), description);
				}
				addModelReferences(attributes, currentInstance);
			} else if (tagName.equalsIgnoreCase("AuditTrailEntry")) {
				// start audit trail entry
				entry = factory.createEvent();
			} else if (tagName.equalsIgnoreCase("Attribute")) {
				// set current attribute name
				genericAttribute = factory.createAttributeLiteral(attributes.getValue("name").trim(), "DEFAULT_VALUE", null);
				addModelReferences(attributes, genericAttribute);
			} else if (tagName.equalsIgnoreCase("EventType")) {
				eventTypeAttribute = (XAttributeLiteral)XLifecycleExtension.ATTR_TRANSITION.clone();
				// set current unknown event type
				if (attributes.getIndex("unknowntype") >= 0) {
					eventTypeAttribute.setValue(attributes.getValue("unknowntype"));
				} else {
					// reset event type value
					eventTypeAttribute.setValue("__INVALID__");
				}
				addModelReferences(attributes, eventTypeAttribute);
			} else if (tagName.equalsIgnoreCase("WorkflowModelElement")) {
				// started workflow model element
				addModelReferences(attributes, entry);
			} else if (tagName.equalsIgnoreCase("Originator")) {
				// started originator
				originatorAttribute = (XAttributeLiteral)XOrganizationalExtension.ATTR_RESOURCE.clone();
				addModelReferences(attributes, originatorAttribute);
			}
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void endElement(String uri, String localName, String qName) throws SAXException {
			String tagName = localName;
			if (tagName.equalsIgnoreCase("")) {
				tagName = qName;
			}
			// parse data
			if (tagName.equalsIgnoreCase("WorkflowLog")) {
				// finished reading
				if (numUnorderedEntries > 0) {
					// let number of unordered entries be shown
					XLogging.log("LogData: Log contains " + numUnorderedEntries +
							" audit trail entries in non-natural order!", XLogging.Importance.ERROR);
					XLogging.log(
							"LogData: The log file you have loaded is not MXML compliant! (error compensated transparently)",
							XLogging.Importance.ERROR);
				}
			} else if (tagName.equalsIgnoreCase("Process")) {
				// set log classifiers
				currentProcess.getClassifiers().addAll(MXML_CLASSIFIERS);
				// set guaranteed attributes
				currentProcess.getGlobalTraceAttributes().add((XAttribute)XConceptExtension.ATTR_NAME.clone());
				currentProcess.getGlobalEventAttributes().add((XAttribute)XConceptExtension.ATTR_NAME.clone());
				currentProcess.getGlobalEventAttributes().add((XAttribute)XLifecycleExtension.ATTR_TRANSITION.clone());
				logs.add(currentProcess);
				currentProcess = null;
			} else if (tagName.equalsIgnoreCase("Source")) {
				// finished source
				sourceOpen = false;
			} else if (tagName.equalsIgnoreCase("ProcessInstance")) {
				// finished process instance
				if (currentInstance.size() > 0) {
					// only use non-empty instances
					if(currentInstance instanceof XTraceBufferedImpl) {
						((XTraceBufferedImpl)currentInstance).consolidate();
					}
					currentProcess.add(currentInstance);
				}
				currentInstance = null;
				// reset last timestamp
				lastTimestamp = null;
			} else if (tagName.equalsIgnoreCase("AuditTrailEntry")) {
				// finished audit trail entry
				if (timestamp == null) {
					// no timestamp defaults to appending
					currentInstance.add(entry);
				} else if (lastTimestamp == null) {
					// no previous timestamp; append and remember timestamp
					currentInstance.add(entry);
					lastTimestamp = timestamp;
				} else {
					// both timestamp and previous timestamp present
					if (timestamp.compareTo(lastTimestamp) >= 0) {
						// last element in list as of timestamp order,
						// append and update reference timestamp
						currentInstance.add(entry);
						lastTimestamp = timestamp;
					} else {
						// audit trail entry is located somewhere in the middle of
						// the list; insert ordered, without updating reference timestamp
						if(currentInstance instanceof XTraceBufferedImpl) {
							((XTraceBufferedImpl)currentInstance).insertOrdered(entry);
						} else {
							// TODO: fix this!
							currentInstance.add(entry);
						}
					}
				}
				entry = null;
			} else if (tagName.equalsIgnoreCase("Attribute")) {
				String value = buffer.toString().trim();
				if(value.length() > 0) {
					// set generic attribute value
					genericAttribute.setValue(buffer.toString().trim());
					// check where to put this attribute,
					// proceed bottom-up:
					if (entry != null) {
						entry.getAttributes().put(genericAttribute.getKey(), genericAttribute);
					} else if (currentInstance != null) {
						currentInstance.getAttributes().put(genericAttribute.getKey(), genericAttribute);
					} else if (currentProcess != null) {
						currentProcess.getAttributes().put(genericAttribute.getKey(), genericAttribute);
					} else if (sourceOpen == true) {
						sourceAttribute.getAttributes().put(genericAttribute.getKey(), genericAttribute);
					}
				}
				// reset attribute
				genericAttribute = null;
			} else if (tagName.equalsIgnoreCase("EventType")) {
				// finished event type
				if(eventTypeAttribute.getValue().equals("__INVALID__")) {
					String type = buffer.toString().trim();
					if(type.length() > 0) {
						eventTypeAttribute.setValue(type);
						entry.getAttributes().put(eventTypeAttribute.getKey(), eventTypeAttribute);
					}
				} else {
					entry.getAttributes().put(eventTypeAttribute.getKey(), eventTypeAttribute);
				}
				eventTypeAttribute = null;
			} else if (tagName.equalsIgnoreCase("WorkflowModelElement")) {
				// finished workflow model element
				XConceptExtension.instance().assignName(entry, buffer.toString().trim());
			} else if (tagName.equalsIgnoreCase("Timestamp")) {
				// finished timestamp)
				String tsString = buffer.toString().trim();
				timestamp = xsDateTimeConversion.parseXsDateTime(tsString);
				if (timestamp != null) {
					XAttributeTimestamp timestampAttribute = (XAttributeTimestamp)XTimeExtension.ATTR_TIMESTAMP.clone();
					timestampAttribute.setValue(timestamp);
					entry.getAttributes().put(timestampAttribute.getKey(), timestampAttribute);
				}
			} else if (tagName.equalsIgnoreCase("Originator")) {
				// finished originator
				String originator = buffer.toString().trim();
				if (originator.length() > 0) {
					originatorAttribute.setValue(originator);
				}
				entry.getAttributes().put(originatorAttribute.getKey(), originatorAttribute);
				originatorAttribute = null;
			}
			// reset character buffer
			buffer.delete(0, buffer.length());
		}

		private void addModelReferences(Attributes attrs, XAttributable subject) {
			String refs = attrs.getValue("modelReference");
			if (refs != null) {
				XAttributeLiteral attribute = (XAttributeLiteral)XSemanticExtension.ATTR_MODELREFERENCE.clone();
				attribute.setValue(refs);
				subject.getAttributes().put(attribute.getKey(), attribute);
			} 
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		public void characters(char[] str, int offset, int len) throws SAXException {
			// append characters to buffer
			buffer.append(str, offset, len);
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#ignorableWhitespace(char[], int, int)
		 */
		public void ignorableWhitespace(char[] str, int offset, int len) throws SAXException {
			// append whitespace to buffer
			buffer.append(str, offset, len);
		}

	}

}
