package org.apromore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author Chathura Ekanayake
 */
public class XMLUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);

    private static final String DEFAULT_NAMESPACE = "http://www.epml.de";

    public static void xmlToFile(final Document doc, final File file) {
        final String xmlString = xmlToString(doc);

        try {
            final FileWriter w = new FileWriter(file);
            w.write(xmlString);
            w.flush();
            w.close();
        } catch (final IOException e) {
            LOGGER.error("Error in xmlToFile", e);
        }
    }

    public static Element getChildElement(final String elementName, final Element parentElement) {
        final NodeList childList = parentElement.getElementsByTagName(elementName);
        if (childList == null || childList.getLength() == 0) {
            return null;
        }

        return (Element) childList.item(0);
    }

    public static String xmlToString(final Document doc) {
        try {
            final DOMSource domSource = new DOMSource(doc);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (final TransformerException ex) {
            LOGGER.error("Error in xmlToFile", ex);
            return null;
        }
    }

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
            return docBuilder.parse(new ByteArrayInputStream(value.getBytes())).getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.warn("stringToAnyElement returning JAXBElement with plain String {} instead of XML", value);
            throw new IllegalArgumentException("Invalid Node in ANY!", e);
        }

    }

    public static Element addElement(final Element parent, final String childName, final Document doc) {
        final Element childElement = doc.createElementNS(DEFAULT_NAMESPACE, childName);
        parent.appendChild(childElement);
        return childElement;
    }

    public static Element addElement(final Element parent, final String childName, final String textValue, final Document doc) {
        final Element childElement = doc.createElementNS(DEFAULT_NAMESPACE, childName);
        childElement.setTextContent(textValue);
        parent.appendChild(childElement);
        return childElement;
    }

    public static Element addElement(final Element parent, final String childName, final String attributeName, final String attributeValue, final Document doc) {
        final Element childElement = doc.createElementNS(DEFAULT_NAMESPACE, childName);
        childElement.setAttribute(attributeName, attributeValue);
        parent.appendChild(childElement);
        return childElement;
    }

}
