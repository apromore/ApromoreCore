/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Chathura Ekanayake
 */
public class XMLUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);


    /**
     * Converts an Element into its XML String representation.
     *
     * @param anyElement extensions element that should contain valid XML with unkown schema
     * @return String representation of XML
     */
    public static String anyElementToString(final Element anyElement) {
        // If Object is NULL, then we return NULL
        if (anyElement == null) {
            LOGGER.warn("anyElementToString returning NULL");
            return null;
        }
        // Otherwise return XML representation
        try {
            final DOMSource domSource = new DOMSource(anyElement);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (final TransformerException e) {
            throw new IllegalArgumentException("Invalid Node in ANY!", e);
        }
    }

    /**
     * Converts a XML String to an Object suitable to put into xs:any
     *
     * @param value String containing XML
     * @return Object representation of XML
     */
    public static Element stringToAnyElement(final String value) {
        // If Object is NULL, then we return NULL
        if (value == null) {
            LOGGER.warn("stringToAnyElement returning NULL");
            return null;
        }
        try {
            // Otherwise return XML representation
            final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return docBuilder.parse(new ByteArrayInputStream(value.getBytes("UTF-8"))).getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.warn("stringToAnyElement returning JAXBElement with plain String {} instead of XML", value);
            throw new IllegalArgumentException("Invalid Node in ANY!", e);
        }

    }

}
