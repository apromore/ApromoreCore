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
package org.deckfour.xes.extension;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttribute;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for extension definition files.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XExtensionParser {
	
	/**
	 * Singleton parser instance.
	 */
	private static XExtensionParser singleton = null;
	
	/**
	 * Provides access to the singleton parser.
	 * @return The parser.
	 */
	public static synchronized XExtensionParser instance() {
		if(singleton == null) {
			singleton = new XExtensionParser();
		}
		return singleton;
	}
	
	/**
	 * Parses an extension from a definition file.
	 * 
	 * @param file The definition file containing the extension.
	 * @return The extension object, as defined in the provided file.
	 */
	public XExtension parse(File file) throws IOException, ParserConfigurationException, SAXException {
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
		// set up a specialized SAX2 handler to fill the container
		XExtensionHandler handler = new XExtensionHandler();
		// set up SAX parser and parse provided log file into the container
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		parser.parse(is, handler);
		is.close();
		return handler.getExtension();
	}
	
	/**
	 * Parses an extension from a URI.
	 * 
	 * @param file The URI which represents the extension definition file.
	 * @return The extension object, as defined in the file referenced by
	 * the given URI.
	 */
	public XExtension parse(URI uri) throws IOException, ParserConfigurationException, SAXException {
		BufferedInputStream is = new BufferedInputStream(uri.toURL().openStream());
		// set up a specialized SAX2 handler to fill the container
		XExtensionHandler handler = new XExtensionHandler();
		// set up SAX parser and parse provided log file into the container
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		parser.parse(is, handler);
		is.close();
		return handler.getExtension();
	}
	
	/**
	 * SAX handler class for extension definition files.
	 * 
	 * @author Christian W. Guenther (christian@deckfour.org)
	 *
	 */
	protected class XExtensionHandler extends DefaultHandler {
		
		/**
		 * The extension to be parsed.
		 */
		protected XExtension extension;
		/**
		 * The currently parsed attribute definition.
		 */
		protected XAttribute currentAttribute;
		/**
		 * Buffer for parsed attribute definitions.
		 */
		protected Collection<XAttribute> xAttributes;
		/**
		 * Factory for creating attributes.
		 */
		protected XFactory factory;
		
		/**
		 * Constructor, resets the handler.
		 */
		public XExtensionHandler() {
			reset();
		}
		
		/**
		 * Resets the handler to initial state.
		 */
		public void reset() {
			extension = null;
			currentAttribute = null;
			xAttributes = null;
			factory = XFactoryRegistry.instance().currentDefault();
		}
		
		/**
		 * Retrieves the parsed extension after parsing.
		 * @return The parsed extension.
		 */
		public XExtension getExtension() {
			return extension;
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			String tagName = localName;
			if (tagName.equalsIgnoreCase("")) {
				tagName = qName;
			}
			// parse data
			if(tagName.equalsIgnoreCase("xesextension")) {
				String xName = attributes.getValue("name");
				String xPrefix = attributes.getValue("prefix");
				URI xUri = null;
				try {
					xUri = new URI(attributes.getValue("uri"));
				} catch (URISyntaxException e) {
					e.printStackTrace();
					return;
				}
				extension = new XExtension(xName, xPrefix, xUri);
			} else if(tagName.equalsIgnoreCase("log")) {
				xAttributes = extension.getLogAttributes();
			} else if(tagName.equalsIgnoreCase("trace")) {
				xAttributes = extension.getTraceAttributes();
			} else if(tagName.equalsIgnoreCase("event")) {
				xAttributes = extension.getEventAttributes();
			} else if(tagName.equalsIgnoreCase("meta")) {
				xAttributes = extension.getMetaAttributes();
			} else if(tagName.equalsIgnoreCase("string")) {
				String key = extension.getPrefix() + ':' + attributes.getValue("key");
				currentAttribute = factory.createAttributeLiteral(key, "DEFAULT", extension);
				xAttributes.add(currentAttribute);
			} else if(tagName.equalsIgnoreCase("date")) {
				String key = extension.getPrefix() + ':' + attributes.getValue("key");
				currentAttribute = factory.createAttributeTimestamp(key, 0, extension);
				xAttributes.add(currentAttribute);
			} else if(tagName.equalsIgnoreCase("int")) {
				String key = extension.getPrefix() + ':' + attributes.getValue("key");
				currentAttribute = factory.createAttributeDiscrete(key, 0, extension);
				xAttributes.add(currentAttribute);
			} else if(tagName.equalsIgnoreCase("float")) {
				String key = extension.getPrefix() + ':' + attributes.getValue("key");
				currentAttribute = factory.createAttributeContinuous(key, 0, extension);
				xAttributes.add(currentAttribute);
			} else if(tagName.equalsIgnoreCase("boolean")) {
				String key = extension.getPrefix() + ':' + attributes.getValue("key");
				currentAttribute = factory.createAttributeBoolean(key, false, extension);
				xAttributes.add(currentAttribute);
			} else if(tagName.equalsIgnoreCase("id")) {
				String key = extension.getPrefix() + ':' + attributes.getValue("key");
				currentAttribute = factory.createAttributeID(key, XIDFactory.instance().createId(), extension);
				xAttributes.add(currentAttribute);
			} else if(currentAttribute != null && tagName.equalsIgnoreCase("alias")) {
				// globally register mapping alias
				String mapping = attributes.getValue("mapping");
				String name = attributes.getValue("name");
				XGlobalAttributeNameMap.instance().registerMapping(mapping, currentAttribute.getKey(), name);
			}
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			String tagName = localName;
			if (tagName.equalsIgnoreCase("")) {
				tagName = qName;
			}
			// close attribute
			if(tagName.equalsIgnoreCase("string")
					|| tagName.equalsIgnoreCase("date")
					|| tagName.equalsIgnoreCase("int")
					|| tagName.equalsIgnoreCase("float")
					|| tagName.equalsIgnoreCase("boolean")
					|| tagName.equalsIgnoreCase("id")) {
				currentAttribute = null;
			}
		}
		
		
		
		
	}

}
