package org.apromore.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Chathura Ekanayake
 */
public class XMLUtils {

    private static final String DEFAULT_NAMESPACE = "http://www.epml.de";

    public static void xmlToFile(Document doc, File file) {
        String xmlString = xmlToString(doc);

        try {
            FileWriter w = new FileWriter(file);
            w.write(xmlString);
            w.flush();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Element getChildElement(String elementName, Element parentElement) {
        NodeList childList = parentElement.getElementsByTagName(elementName);
        if (childList == null || childList.getLength() == 0) {
            return null;
        }

        return (Element) childList.item(0);
    }

    public static String xmlToString(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Element addElement(Element parent, String childName, Document doc) {
        Element childElement = doc.createElementNS(DEFAULT_NAMESPACE, childName);
        parent.appendChild(childElement);
        return childElement;
    }

    public static Element addElement(Element parent, String childName, String textValue, Document doc) {
        Element childElement = doc.createElementNS(DEFAULT_NAMESPACE, childName);
        childElement.setTextContent(textValue);
        parent.appendChild(childElement);
        return childElement;
    }

    public static Element addElement(Element parent, String childName, String attributeName, String attributeValue, Document doc) {
        Element childElement = doc.createElementNS(DEFAULT_NAMESPACE, childName);
        childElement.setAttribute(attributeName, attributeValue);
        parent.appendChild(childElement);
        return childElement;
    }
}
