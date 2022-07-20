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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private static final String BPMN_ELEMENT_PREFIX = "bpmn:";
    private static final String DIAGRAM_ELEMENT_PREFIX = "bpmndi:";
    private static final String BPMN_PLANE_TAG = "BPMNPlane";
    private static final String BPMN_PROCESS_TAG = "process";
    private static final String BPMN_ELEMENT_ATR = "bpmnElement";
    private static final String MISSING_PROCESS_MSG = "The document does not contain a process";
    private static final List<String> PROCESS_TAGS = List.of("bpmn:process", BPMN_PROCESS_TAG);
    private static final List<String> INCOMING_TAGS = List.of("bpmn:incoming", "incoming");
    private static final List<String> OUTGOING_TAGS = List.of("bpmn:outgoing", "outgoing");
    private static final List<String> FLOW_NODE_REF_TAGS = List.of("bpmn:flowNodeRef", "flowNodeRef");
    private static final List<String> EXT_ELEMENTS_TAGS = List.of("bpmn:extensionElements", "extensionElements");
    private static final List<String> DOC_ELEMENTS_TAGS = List.of("bpmn:documentation", "documentation");

    private static final String BASE_SINGLE_PROCESS_BPMN_XML = "<definitions"
        + " xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n"
        + " xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\">"
        + "<process id=\"Process_1\"/>"
        + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">"
        + "<bpmndi:BPMNPlane bpmnElement=\"Process_1\"/>"
        + "</bpmndi:BPMNDiagram>"
        + "</definitions>";

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
        NodeList elementsWithBpmnPrefix = doc.getElementsByTagName(BPMN_ELEMENT_PREFIX + elementName);

        List<Node> subprocessElements = convertToList(elementsWithoutBpmnPrefix);
        subprocessElements.addAll(convertToList(elementsWithBpmnPrefix));
        return subprocessElements;
    }

    public static List<Node> getDiagramElements(Document doc, String elementName) {
        NodeList elementsWithoutBpmnPrefix = doc.getElementsByTagName(elementName);
        NodeList elementsWithBpmnPrefix = doc.getElementsByTagName(DIAGRAM_ELEMENT_PREFIX + elementName);

        List<Node> subprocessElements = convertToList(elementsWithoutBpmnPrefix);
        subprocessElements.addAll(convertToList(elementsWithBpmnPrefix));
        return subprocessElements;
    }

    /**
     * Get the first diagram element which references the bpmn element with id bpmnElementId.
     * @param doc the document to search for the element in.
     * @param tagName the tag name of the element.
     * @param bpmnElementId the id of the bpmn element the diagram element refers to.
     * @return the first diagram element which references the bpmn element or null if none is found.
     */
    public static Node getDiagramElement(Document doc, String tagName, String bpmnElementId) {
        for (Node diagramElement : getDiagramElements(doc, tagName)) {
            if (!diagramElement.hasAttributes()) {
                continue;
            }
            if (bpmnElementId.equals(diagramElement.getAttributes().getNamedItem(BPMN_ELEMENT_ATR).getTextContent())) {
                return diagramElement;
            }
        }
        return null;
    }

    /**
     * Convert a diagram to a xml string.
     * @param document
     * @return an xml string.
     * @throws TransformerException
     */
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

    /**
     * Get the bpmn xml for each subprocess in the document.
     * @param document a process diagram in document form.
     * @param includeEmptySubprocesses true to include empty subprocess in the map.
     * @return a map of subprocess element ids the bpmn xml of the subprocess contents as a diagram.
     */
    public static Map<String, String> getSubprocessBpmnMap(Document document, boolean includeEmptySubprocesses)
        throws ParserConfigurationException, IOException, SAXException, ExportFormatException, TransformerException {
        Map<String, String> subprocessIdToDiagramMap = new HashMap<>();
        List<Node> subprocessNodes = getBPMNElements(document, "subProcess");

        for (Node subprocessNode : subprocessNodes) {
            Document subprocessDoc = getSubprocessDocument(subprocessNode);
            Node processNode = getBPMNElements(subprocessDoc, BPMN_PROCESS_TAG).get(0);
            if (!includeEmptySubprocesses && !processNode.hasChildNodes()) {
                continue; //skip empty subprocesses if check is on
            }

            //Add subprocess entry to map
            String subprocessId = subprocessNode.getAttributes().getNamedItem("id").getTextContent();
            subprocessIdToDiagramMap.put(subprocessId, getXMLString(subprocessDoc));
        }
        return subprocessIdToDiagramMap;
    }

    /**
     * Get the bpmn xml document a subprocess.
     * @param subprocessNode a document node representation of the subprocess.
     * @return A document with the subprocess contents extracted into a separate bpmn model.
     */
    private static Document getSubprocessDocument(Node subprocessNode)
        throws ExportFormatException, ParserConfigurationException, IOException, SAXException {
        Document originalDocument = subprocessNode.getOwnerDocument();
        //Build subprocess document
        Document subprocessDoc = getDocument(BASE_SINGLE_PROCESS_BPMN_XML);
        addMissingDocumentDefinitions(originalDocument, subprocessDoc);

        // Subprocesses should not have pools so the recreated subprocess bpmn model only
        // has a single process (no collaborations)
        Node processNode = getBPMNElements(subprocessDoc, BPMN_PROCESS_TAG).get(0);

        // Add all children elements of the subprocess to the new process.
        // Ignore the incoming and outgoing tags which connect the subprocess elements to its outer model.
        NodeList subprocessChildren = subprocessNode.getChildNodes();
        for (int i = 0; i < subprocessChildren.getLength(); i++) {
            Node child = subprocessChildren.item(i);

            if (!INCOMING_TAGS.contains(child.getNodeName())
                && !OUTGOING_TAGS.contains(child.getNodeName())
                && !EXT_ELEMENTS_TAGS.contains(child.getNodeName())
            ) {
                processNode.appendChild(subprocessDoc.importNode(child, true));
            }
        }

        //Copy diagram elements for the subprocess into the subprocess document
        List<String> elementIds = getChildIds(subprocessNode);
        List<Node> oldDiagramPlanes = getDiagramElements(originalDocument, BPMN_PLANE_TAG);

        if (oldDiagramPlanes.isEmpty()) {
            throw new ExportFormatException("No BPMNPlane elements found in the original diagram");
        }

        Node newDiagramPlane = getDiagramElements(subprocessDoc, BPMN_PLANE_TAG).get(0);

        for (Node oldDiagramPlane : oldDiagramPlanes) {
            for (Node subprocessDiagramChild : convertToList(oldDiagramPlane.getChildNodes())) {
                Node bpmnElement = subprocessDiagramChild.hasAttributes()
                    ? subprocessDiagramChild.getAttributes().getNamedItem(BPMN_ELEMENT_ATR) : null;
                if (bpmnElement != null &&  elementIds.contains(bpmnElement.getTextContent())) {
                    Node importedNode = subprocessDoc.importNode(subprocessDiagramChild, true);
                    newDiagramPlane.appendChild(importedNode);
                }
            }
        }

        return subprocessDoc;
    }

    /**
     * Get the element ids of all children in the node.
     * @param node a bpmn element.
     * @return a list of element ids of children in the node.
     */
    private static List<String> getChildIds(Node node) {
        List<String> ids = new ArrayList<>();
        NodeList nodeChildren = node.getChildNodes();

        for(int j = 0; j < nodeChildren.getLength(); j++) {
            if (!EXT_ELEMENTS_TAGS.contains(nodeChildren.item(j).getNodeName())) {
                Node nodeChild = nodeChildren.item(j);
                ids.addAll(getChildIds(nodeChild));

                //Add id to list
                if (nodeChild.hasAttributes()) {
                    Node id = nodeChild.getAttributes().getNamedItem("id");
                    if (id != null && !ids.contains(id.getTextContent())) {
                        ids.add(id.getTextContent());
                    }
                }
            }
        }
        return ids;
    }

    /**
     * Replace the contents of a subprocess with elements in another model.
     * @param subprocessNode the subprocess to replace the contents of.
     * @param linkedProcessDocument the document containing elements to add to the subprocess.
     * @throws ExportFormatException if the document does not contain a process.
     */
    public static void replaceSubprocessContents(Node subprocessNode, Document linkedProcessDocument)
        throws ExportFormatException {
        //Update definitions
        addMissingDocumentDefinitions(linkedProcessDocument, subprocessNode.getOwnerDocument());

        removeSubprocessContents(subprocessNode);

        Document bpmnDocument = subprocessNode.getOwnerDocument();

        List<Node> collaborationElements = getBPMNElements(linkedProcessDocument, "collaboration");

        if (!CollectionUtils.isEmpty(collaborationElements)) {
            throw new ExportFormatException("One or more linked subprocess models contain pools. Pools in subprocesses are not yet supported.");
        }

        List<Node> processElements = getBPMNElements(linkedProcessDocument, BPMN_PROCESS_TAG);
        if (CollectionUtils.isEmpty(processElements)) {
            throw new ExportFormatException(MISSING_PROCESS_MSG);
        }

        Node linkedProcessNode = processElements.get(0);
        //Add bpmn elements
        Map<String, String> idMap = addNodeChildren(linkedProcessNode, subprocessNode, null);

        //Replace diagram elements
        String processId = linkedProcessNode.hasAttributes()
            ? linkedProcessNode.getAttributes().getNamedItem("id").getTextContent() : "";
        Node linkedProcessDiagramNode = getDiagramElement(linkedProcessDocument, BPMN_PLANE_TAG, processId);

        List<Node> subProcessDiagramElements = getDiagramElements(bpmnDocument, BPMN_PLANE_TAG);
        if (CollectionUtils.isEmpty(subProcessDiagramElements) || linkedProcessDiagramNode == null) {
            throw new ExportFormatException(MISSING_PROCESS_MSG);
        }
        Node subProcessDiagramNode = subProcessDiagramElements.get(0);

        for (Node linkedProcessDiagramChild : convertToList(linkedProcessDiagramNode.getChildNodes())) {
            Node importedNode = bpmnDocument.importNode(linkedProcessDiagramChild, true);
            Node bpmnElement = importedNode.hasAttributes()
                ? importedNode.getAttributes().getNamedItem(BPMN_ELEMENT_ATR) : null;
            if (bpmnElement != null && idMap.containsKey(bpmnElement.getTextContent())) {
                String oldId = bpmnElement.getTextContent();
                bpmnElement.setTextContent(idMap.getOrDefault(oldId, oldId));
            }
            subProcessDiagramNode.appendChild(importedNode);
        }
    }

    /**
     * Remove the subprocess contents of the original xml.
     * Keep the extensions elements and incoming/outgoing tags.
     * @param subprocessNode the subprocess to remove the contents of.
     */
    private static void removeSubprocessContents(Node subprocessNode) throws ExportFormatException {
        //Remove all contents of subprocess node
        List<Node> retainList = new ArrayList<>();
        List<String> deletedElements = new ArrayList<>();

        while (subprocessNode.hasChildNodes()) {
            Node node = subprocessNode.getFirstChild();
            Node nodeId = node.hasAttributes() ? node.getAttributes().getNamedItem("id") : null;

            if (INCOMING_TAGS.contains(node.getNodeName())
                || OUTGOING_TAGS.contains(node.getNodeName())
                || EXT_ELEMENTS_TAGS.contains(node.getNodeName())
                || DOC_ELEMENTS_TAGS.contains(node.getNodeName())) {
                retainList.add(node);
            } else if (nodeId != null && !deletedElements.contains(nodeId.getTextContent())) {
                deletedElements.add(nodeId.getTextContent());
            }
            subprocessNode.removeChild(node);
        }

        //Re-add the nodes in the retain list
        for (Node node : retainList) {
            subprocessNode.appendChild(node);
        }

        //Remove associated diagram elements
        removeDiagramElements(subprocessNode.getOwnerDocument(), deletedElements);
    }

    /**
     * Remove diagram elements from a document which refer to the bpmn elements in the delete list.
     * @param doc The document containing diagram elements.
     * @param deleteList a list of bpmn element ids to delete.
     * @throws ExportFormatException if the document does not contain a process.
     */
    private static void removeDiagramElements(Document doc, List<String> deleteList) throws ExportFormatException {
        List<Node> bpmnPlaneElements = getDiagramElements(doc, BPMN_PLANE_TAG);
        if (CollectionUtils.isEmpty(bpmnPlaneElements)) {
            throw new ExportFormatException(MISSING_PROCESS_MSG);
        }

        Node processDiagramNode = bpmnPlaneElements.get(0);
        List<Node> retainList = new ArrayList<>();
        while (processDiagramNode.hasChildNodes()) {
            Node node = processDiagramNode.getFirstChild();
            Node bpmnElement = node.hasAttributes() ? node.getAttributes().getNamedItem(BPMN_ELEMENT_ATR) : null;

            if (bpmnElement != null && !deleteList.contains(bpmnElement.getTextContent())) {
                retainList.add(node);
            }

            processDiagramNode.removeChild(node);
        }

        //Re-add the nodes in the retain list
        for (Node node : retainList) {
            processDiagramNode.appendChild(node);
        }
    }

    /**
     * Add the children from one node to another node.
     * @param fromNode the node to get the child nodes from.
     * @param toNode the node to add the child nodes to.
     * @param elementIdMap a map of existing ids to new ids.
     * @return A map of updated ids with the original id as the key and the updated id as the value.
     */
    private static Map<String, String> addNodeChildren(Node fromNode, Node toNode, Map<String, String> elementIdMap) {
        Document bpmnDocument = toNode.getOwnerDocument();
        NodeList fromNodeChildren = fromNode.getChildNodes();
        Map<String, String> idMap = elementIdMap == null ? new HashMap<>() : new HashMap<>(elementIdMap);

        for (int j = 0; j < fromNodeChildren.getLength(); j++) {
            Node fromNodeChild = fromNodeChildren.item(j);
            if (PROCESS_TAGS.contains(fromNode.getNodeName())
                && EXT_ELEMENTS_TAGS.contains(fromNodeChild.getNodeName())) {
                continue;
            }

            Node importedNode = bpmnDocument.importNode(fromNodeChild, false);
            idMap.putAll(addNodeChildren(fromNodeChild, importedNode, idMap));

            //Deal with same ids
            if (importedNode.hasAttributes()) {
                Node[] replaceAttributes = {
                    importedNode.getAttributes().getNamedItem("id"),
                    importedNode.getAttributes().getNamedItem("sourceRef"),
                    importedNode.getAttributes().getNamedItem("targetRef")
                };

                for (Node replaceAttribute : replaceAttributes) {
                    if (replaceAttribute != null) {
                        String oldId = replaceAttribute.getTextContent();

                        idMap.putIfAbsent(oldId, getRandomId());
                        replaceAttribute.setTextContent(idMap.get(oldId));
                    }
                }
            } else if (INCOMING_TAGS.contains(importedNode.getNodeName())
                || OUTGOING_TAGS.contains(importedNode.getNodeName())
                || FLOW_NODE_REF_TAGS.contains(importedNode.getNodeName())) {
                String oldId = importedNode.getTextContent();

                idMap.putIfAbsent(oldId, getRandomId());
                importedNode.setTextContent(idMap.get(oldId));
            }
            toNode.appendChild(importedNode);
        }
        return idMap;
    }

    private static String getRandomId() {
        return "element" + UUID.randomUUID().toString().replace("-", "");
    }

    private static List<Node> convertToList(NodeList nodeList) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(nodeList.item(i));
        }
        return nodes;
    }

    private static void addMissingDocumentDefinitions(Document documentFrom, Document documentTo)
        throws ExportFormatException {
        List<Node> bpmnDefinitionsFrom = getBPMNElements(documentFrom, "definitions");
        List<Node> bpmnDefinitionsTo = getBPMNElements(documentTo, "definitions");

        if (CollectionUtils.isEmpty(bpmnDefinitionsFrom) || CollectionUtils.isEmpty(bpmnDefinitionsTo)) {
            throw new ExportFormatException("Missing bpmn:definitions tag");
        }

        Node bpmnDefinitionFrom = bpmnDefinitionsFrom.get(0);
        Node bpmnDefinitionTo = bpmnDefinitionsTo.get(0);

        for (int i = 0; i < bpmnDefinitionFrom.getAttributes().getLength(); i++) {
            Node attribute = bpmnDefinitionFrom.getAttributes().item(i);

            if (bpmnDefinitionTo.getAttributes().getNamedItem(attribute.getNodeName()) == null) {
                Node importedAttribute = documentTo.importNode(attribute, false);
                bpmnDefinitionTo.getAttributes().setNamedItem(importedAttribute);
            }

        }
    }

}
