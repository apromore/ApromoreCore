/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
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

    public static String extensionElementToString(final Object extensionElement) {
        if (extensionElement == null) {
            LOGGER.warn("extensionElementToString returning NULL");
            return null;
        }
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.xml.bind.Marshaller marshaller = javax.xml.bind.JAXBContext.newInstance(com.processconfiguration.ObjectFactory.class).createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FRAGMENT, true);
            marshaller.marshal(extensionElement, baos);
            return baos.toString();
        } catch (javax.xml.bind.JAXBException e) {
            throw new IllegalArgumentException("Invalid extension element", e);
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
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            assert documentBuilder.isNamespaceAware();
            Element result = documentBuilder.parse(new ByteArrayInputStream(value.getBytes("UTF-8"))).getDocumentElement();
            return result;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.warn("stringToAnyElement returning JAXBElement with plain String {} instead of XML", value);
            throw new IllegalArgumentException("Invalid Node in ANY!", e);
        }

    }

    /**
     * Converts a XML String to an Object suitable to put into xs:any
     *
     * @param value String containing XML
     * @return Object representation of XML
     */
    public static Element stringToAnyElementNoError(final String value) {
        // If Object is NULL, then we return NULL
        if (value == null) {
            return null;
        }
        try {
            // Otherwise return XML representation
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            assert documentBuilder.isNamespaceAware();
            Element result = documentBuilder.parse(new ByteArrayInputStream(value.getBytes("UTF-8"))).getDocumentElement();
            return result;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.warn("stringToAnyElement returning JAXBElement with plain String {} instead of XML", value);
            throw new IllegalArgumentException("Invalid Node in ANY!", e);
        }

    }

}
