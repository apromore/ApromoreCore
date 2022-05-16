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

package org.apromore.service.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.collections.CollectionUtils;
import org.apromore.exception.ExportFormatException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class BPMNDocumentHelper {

    private BPMNDocumentHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static Document getDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.parse(new InputSource(new StringReader(xml)));
    }

    public static List<Node> getBPMNElements(Document doc, String elementName) {
        NodeList elementsWithoutBpmnPrefix = doc.getElementsByTagName(elementName);
        NodeList elementsWithBpmnPrefix = doc.getElementsByTagName("bpmn:" + elementName);

        List<Node> subprocessElements = convertToList(elementsWithoutBpmnPrefix);
        subprocessElements.addAll(convertToList(elementsWithBpmnPrefix));
        return subprocessElements;
    }

    public static String getXMLString(Document document) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newDefaultInstance();
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        String xmlTag = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return xmlTag + writer.getBuffer().toString().replaceAll("[\n\r]", "");
    }

    public static void replaceSubprocessContents(Node subprocessNode, Document linkedProcessDocument)
        throws ExportFormatException {
        removeSubprocessContents(subprocessNode);

        Document bpmnDocument = subprocessNode.getOwnerDocument();
        List<Node> processElements = getBPMNElements(linkedProcessDocument, "process");
        if (CollectionUtils.isEmpty(processElements)) {
            throw new ExportFormatException("The document does not contain a process");
        }

        Node linkedProcessNode = processElements.get(0);
        NodeList linkedProcessChildren = linkedProcessNode.getChildNodes();
        //Add contents of the linked process model to the <subprocess> tag
        //exclude the process extension elements of the linked model
        for(int j = 0; j < linkedProcessChildren.getLength(); j++) {
            if (!linkedProcessChildren.item(j).getNodeName().equals("bpmn:extensionElements") &&
                !linkedProcessChildren.item(j).getNodeName().equals("extensionElements")) {
                Node importedNode = bpmnDocument.importNode(linkedProcessChildren.item(j), true);
                subprocessNode.appendChild(importedNode);
            }
        }
    }

    /**
     * Remove the subprocess contents of the original xml.
     * Keep the extensions elements and incoming/outgoing tags.
     * @param subprocessNode
     */
    private static void removeSubprocessContents(Node subprocessNode) {
        List<Node> retainList = new ArrayList<>();

        NodeList subprocessChildNodes = subprocessNode.getChildNodes();
        for (int n = 0; n < subprocessChildNodes.getLength(); n++) {
            Node node = subprocessChildNodes.item(n);
            if (node.getNodeName().equals("bpmn:extensionElements")
                || node.getNodeName().equals("extensionElements")
                || node.getNodeName().equals("bpmn:incoming")
                || node.getNodeName().equals("incoming")
                || node.getNodeName().equals("bpmn:outgoing")
                || node.getNodeName().equals("outgoing")) {
                retainList.add(node);
            }
        }

        //Remove all contents of subprocess node
        while (subprocessNode.hasChildNodes()) {
            subprocessNode.removeChild(subprocessNode.getFirstChild());
        }

        //Re-add the nodes in the retain list
        for (Node node : retainList) {
            subprocessNode.appendChild(node);
        }
    }

    public static List<Node> convertToList(NodeList nodeList) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(nodeList.item(i));
        }
        return nodes;
    }

}
