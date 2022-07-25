/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
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
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
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
import org.w3c.dom.Element;
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
    private static final List<String> MESSAGE_FLOW_TAGS = List.of("bpmn:messageFlow", "messageFlow");
    private static final List<String> LANE_SET_TAGS = List.of("bpmn:laneSet", "laneSet");
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

    private static final String BASE_COLLABORATION_BPMN_XML = "<definitions"
        + " xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n"
        + " xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\">"
        + "<bpmn:collaboration id=\"Collaboration_1\"/>"
        + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">"
        + "<bpmndi:BPMNPlane bpmnElement=\"Collaboration_1\"/>"
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

    public static Node getBPMNElement(Document doc, String elementName, String bpmnElementId) {
        for (Node bpmnElement : getBPMNElements(doc, elementName)) {
            if (!bpmnElement.hasAttributes() || bpmnElement.getAttributes().getNamedItem("id") == null) {
                continue;
            }
            if (bpmnElementId.equals(bpmnElement.getAttributes().getNamedItem("id").getTextContent())) {
                return bpmnElement;
            }
        }
        return null;
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
            if (!includeEmptySubprocesses && isEmptyBpmn(subprocessDoc)) {
                continue; //skip empty subprocesses if check is on
            }

            //Add subprocess entry to map
            String subprocessId = subprocessNode.getAttributes().getNamedItem("id").getTextContent();
            subprocessIdToDiagramMap.put(subprocessId, getXMLString(subprocessDoc));
        }
        return subprocessIdToDiagramMap;
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

        linkedProcessDocument = convertToBusinessProcessDiagram(linkedProcessDocument);

        List<Node> processElements = getBPMNElements(linkedProcessDocument, BPMN_PROCESS_TAG);
        if (CollectionUtils.isEmpty(processElements)) {
            throw new ExportFormatException(MISSING_PROCESS_MSG);
        }

        Node linkedProcessNode = processElements.get(0);
        //Add bpmn elements
        Map<String, String> idMap = addNodeChildren(linkedProcessNode, subprocessNode, null);

        List<Node> linkedProcessDiagramPlanes = getDiagramElements(linkedProcessDocument, BPMN_PLANE_TAG);
        List<Node> subProcessDiagramElements = getDiagramElements(bpmnDocument, BPMN_PLANE_TAG);
        if (CollectionUtils.isEmpty(subProcessDiagramElements) || CollectionUtils.isEmpty(linkedProcessDiagramPlanes)) {
            throw new ExportFormatException(MISSING_PROCESS_MSG);
        }

        Node linkedProcessDiagramNode = linkedProcessDiagramPlanes.get(0);
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
     * Converts a bpmn collaboration diagram document to a business process diagram document.
     * @param bpmnCollaborationDiagram a bpmn xml document with a collaboration of processes.
     * @return a bpmn xml document with a single process. Collaborations in the previous diagram
     * are converted to laneSets.
     * @throws ExportFormatException throw an exception if the conversion is unsuccessful.
     */
    public static Document convertToBusinessProcessDiagram(Document bpmnCollaborationDiagram)
        throws ExportFormatException {
        List<Node> collaborationElements = getBPMNElements(bpmnCollaborationDiagram, "collaboration");
        if (CollectionUtils.isEmpty(collaborationElements)) {
            return bpmnCollaborationDiagram; //No collaboration elements - return original diagram
        }

        Document singleProcessDiagram;
        try {
            singleProcessDiagram = getDocument(BASE_SINGLE_PROCESS_BPMN_XML);
            addMissingDocumentDefinitions(bpmnCollaborationDiagram, singleProcessDiagram);
            Node processNode = getBPMNElements(singleProcessDiagram, BPMN_PROCESS_TAG).get(0);

            Node collaborationElement = collaborationElements.get(0);
            for (Node collaborationElementChild : convertToList(collaborationElement.getChildNodes())) {
                switch (collaborationElementChild.getNodeName()) {
                    case "extensionElements":
                    case "bpmn:extensionElements":
                        break;
                    case "participant":
                    case "bpmn:participant":
                        copyParticipantContentsToProcess(collaborationElementChild, processNode);
                        break;
                    default:
                        Node importedNode = singleProcessDiagram.importNode(collaborationElementChild, true);
                        processNode.appendChild(importedNode);
                }
            }

            //Keep bpmn diagram elements the same
            Node oldDiagramPlane = getDiagramElements(bpmnCollaborationDiagram, BPMN_PLANE_TAG).get(0);
            Node newDiagramPlane = getDiagramElements(singleProcessDiagram, BPMN_PLANE_TAG).get(0);

            for (Node collaborationDiagramChild : convertToList(oldDiagramPlane.getChildNodes())) {
                Node importedNode = singleProcessDiagram.importNode(collaborationDiagramChild, true);
                newDiagramPlane.appendChild(importedNode);
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ExportFormatException("Could not create a document from the base xml.");
        }

        return singleProcessDiagram;
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
        Map<String, String> idMap = elementIdMap == null ? new HashMap<>() : new HashMap<>(elementIdMap);

        List<String> idRefBaseTags = List.of("incoming", "outgoing", "flowNodeRef", "sourceRef", "targetRef");
        List<String> idRefCompleteTags = new ArrayList<>(idRefBaseTags);
        idRefBaseTags.forEach(tag -> idRefCompleteTags.add(BPMN_ELEMENT_PREFIX + tag));


        NodeList fromNodeChildren = fromNode.getChildNodes();
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
            } else if (idRefCompleteTags.contains(importedNode.getNodeName())) {
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

    private static void copyParticipantContentsToProcess(Node participant, Node processNode)
        throws ExportFormatException {
        Document bpmnCollaborationDocument = participant.getOwnerDocument();
        Document singleProcessDocument = processNode.getOwnerDocument();

        Node participantId = participant.getAttributes().getNamedItem("id");
        Node participantName = participant.getAttributes().getNamedItem("name");

        if (participant.getAttributes().getNamedItem("processRef") == null) {
            //This is a participant with text only - no elements. It still needs to be copied.
            createLaneSetFromParticipant(participant, processNode);
        } else {
            //Get the referenced process
            String processRefId = participant.getAttributes().getNamedItem("processRef").getTextContent();
            Node processRef = getBPMNElement(bpmnCollaborationDocument, BPMN_PROCESS_TAG, processRefId);

            if (processRef == null) {
                throw new ExportFormatException("Could not not find referenced process.");
            }

            //Only add laneSet if the subprocess does not have one
            Node lane = null;
            if (!hasLaneSets(processRef)) {
                //Add a laneSet for the participant. The laneSet should have the same id and name as the participant.
                Element laneSet = createLaneSetFromParticipant(participant, processNode);
                lane = singleProcessDocument.createElement("bpmn:lane");
                laneSet.appendChild(lane);
            }

            //Iterate through child nodes
            for (int i = 0; i < processRef.getChildNodes().getLength(); i++) {
                Node processChildNode = processRef.getChildNodes().item(i);
                //Add all non-laneSet elements to the processNode
                Node importedNode = singleProcessDocument.importNode(processChildNode, true);
                processNode.appendChild(importedNode);

                if (LANE_SET_TAGS.contains(processChildNode.getNodeName())) {
                    //replace id and name with that of participant
                    Element importedElement = (Element) importedNode;

                    if (participantId != null) {
                        importedElement.setAttribute("id", participantId.getTextContent());
                    }
                    if (participantName != null) {
                        importedElement.setAttribute("name", participantName.getTextContent());
                    }
                } else if (lane != null && importedNode.getAttributes() != null
                    && importedNode.getAttributes().getNamedItem("id") != null) {
                    Node flowNodeRef = singleProcessDocument.createElement("bpmn:flowNodeRef");
                    flowNodeRef.setTextContent(importedNode.getAttributes().getNamedItem("id").getTextContent());
                    lane.appendChild(flowNodeRef);
                }
            }
        }
    }

    private static Element createLaneSetFromParticipant(Node participant, Node laneSetParent) {
        Document document = laneSetParent.getOwnerDocument();
        Element laneSet = document.createElement("bpmn:laneSet");

        Node participantId = participant.getAttributes().getNamedItem("id");
        if (participantId != null) {
            laneSet.setAttribute("id", participantId.getTextContent());
        }

        Node participantName = participant.getAttributes().getNamedItem("name");
        if (participantName != null) {
            laneSet.setAttribute("name", participantName.getTextContent());
        }
        laneSetParent.appendChild(laneSet);
        return laneSet;
    }

    /**
     * Get the bpmn xml document a subprocess.
     * @param subprocessNode a document node representation of the subprocess.
     * @return A document with the subprocess contents extracted into a separate bpmn model.
     */
    private static Document getSubprocessDocument(Node subprocessNode)
        throws ExportFormatException, ParserConfigurationException, IOException, SAXException {
        if (hasLaneSets(subprocessNode)) {
            return createCollaborationDocument(subprocessNode);
        } else {
            return createSingleProcessDocument(subprocessNode);
        }
    }

    private static Document createSingleProcessDocument(Node subprocessNode)
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
        copyDiagramElementsToDocument(subprocessNode, subprocessDoc);

        return subprocessDoc;
    }


    private static Document createCollaborationDocument(Node node)
        throws ExportFormatException, ParserConfigurationException, IOException, SAXException {
        Document originalDocument = node.getOwnerDocument();

        //Build subprocess document
        Document subprocessDoc = getDocument(BASE_COLLABORATION_BPMN_XML);
        addMissingDocumentDefinitions(originalDocument, subprocessDoc);

        Node definitionsNode = getBPMNElements(subprocessDoc, "definitions").get(0);
        Node collaborationNode = getBPMNElements(subprocessDoc, "collaboration").get(0);

        //Add participant for each laneSet
        List<Node> subprocessChildren = convertToList(node.getChildNodes());
        int laneSetCount = 0;
        List<String> importedElements = new ArrayList<>();
        List<Node> collaborationElements = new ArrayList<>(subprocessChildren);
        collaborationElements.removeIf(n -> n.getAttributes() == null || n.getAttributes().getNamedItem("id") == null);

        for (Node child : subprocessChildren) {
            if (LANE_SET_TAGS.contains(child.getNodeName())) {
                laneSetCount++;
                createParticipantFromLaneSet(child, subprocessDoc, laneSetCount);

                //Keep track of added elements
                Node id = child.getAttributes().getNamedItem("id");
                if (id != null) {
                    importedElements.add(id.getTextContent());
                }

                Node processNode = definitionsNode.getLastChild();
                importedElements.addAll(getChildIds(processNode));

                collaborationElements.remove(child);
                collaborationElements.removeIf(
                    n -> importedElements.contains(n.getAttributes().getNamedItem("id").getTextContent())
                );
            }
        }

        //Add all elements which do not belong to a process directly to the collaboration
        for (Node collaborationElement : collaborationElements) {
            collaborationNode.appendChild(subprocessDoc.importNode(collaborationElement, true));
        }

        //Copy diagram elements for the subprocess into the subprocess document
        copyDiagramElementsToDocument(node, subprocessDoc);

        return subprocessDoc;
    }

    private static void createParticipantFromLaneSet(Node laneSet, Document document, int index) {
        Node laneSetId = laneSet.getAttributes().getNamedItem("id");
        String participantId = laneSetId == null ? "Participant_" + index : laneSetId.getTextContent();

        Element participantNode = document.createElement("bpmn:participant");
        participantNode.setAttribute("id", participantId);

        Node laneSetName = laneSet.getAttributes().getNamedItem("name");
        if (laneSetName != null) {
            participantNode.setAttribute("name", laneSetName.getTextContent());
        }

        if (laneSet.hasChildNodes()) {
            String processRefId = "Process_" + index;
            String newLaneSetId = "LaneSet_" + index;
            participantNode.setAttribute("processRef", processRefId);
            createReferencedProcess(laneSet, document, newLaneSetId, processRefId);
        }

        Node collaborationNode = getBPMNElements(document, "collaboration").get(0);
        collaborationNode.appendChild(participantNode);
    }

    private static void createReferencedProcess(Node laneSet, Document document, String laneSetId,
                                                String processRefId) {
        Node definitionsNode = getBPMNElements(document, "definitions").get(0);

        Element processNode = document.createElement("bpmn:process");
        processNode.setAttribute("id", processRefId);
        definitionsNode.appendChild(processNode);

        //Copy laneSet into process - may need to remove laneSet id as it has been transferred onto the participant
        Element importedLaneSet = (Element) document.importNode(laneSet, true);
        importedLaneSet.setAttribute("id", laneSetId);
        processNode.appendChild(importedLaneSet);

        //Copy elements associated with laneSet into process
        List<String> ids = new ArrayList<>(getLaneSetRelatedElementIds(laneSet));
        NodeList laneSetSiblings = laneSet.getParentNode().getChildNodes();
        for (int i = 0; i < laneSetSiblings.getLength(); i++) {
            Node laneSetSibling = laneSetSiblings.item(i);

            if (laneSetSibling.getAttributes() == null || MESSAGE_FLOW_TAGS.contains(laneSetSibling.getNodeName())) {
                continue;
            }
            Node id = laneSetSibling.getAttributes().getNamedItem("id");
            if (id != null && ids.contains(id.getTextContent())) {
                Node importedNode = document.importNode(laneSetSibling, true);
                processNode.appendChild(importedNode);
            }
        }
    }

    private static boolean hasLaneSets(Node subprocess) {
        NodeList subprocessChildren = subprocess.getChildNodes();
        for (int i = 0; i < subprocessChildren.getLength(); i++) {
            Node child = subprocessChildren.item(i);

            if (LANE_SET_TAGS.contains(child.getNodeName())) {
                return true;
            }
        }
        return false;
    }

    private static void copyDiagramElementsToDocument(Node node, Document document) throws ExportFormatException {
        Document originalDocument = node.getOwnerDocument();
        List<String> elementIds = getChildIds(node);
        List<Node> oldDiagramPlanes = getDiagramElements(originalDocument, BPMN_PLANE_TAG);

        if (oldDiagramPlanes.isEmpty()) {
            throw new ExportFormatException("No BPMNPlane elements found in the original diagram");
        }

        Node newDiagramPlane = getDiagramElements(document, BPMN_PLANE_TAG).get(0);

        for (Node oldDiagramPlane : oldDiagramPlanes) {
            for (Node subprocessDiagramChild : convertToList(oldDiagramPlane.getChildNodes())) {
                Node bpmnElement = subprocessDiagramChild.hasAttributes()
                    ? subprocessDiagramChild.getAttributes().getNamedItem(BPMN_ELEMENT_ATR) : null;
                if (bpmnElement != null &&  elementIds.contains(bpmnElement.getTextContent())) {
                    Node importedNode = document.importNode(subprocessDiagramChild, true);
                    newDiagramPlane.appendChild(importedNode);
                }
            }
        }
    }

    /**
     * Get the element ids of all children in the node.
     * @param node a bpmn element.
     * @return a list of element ids of children in the node.
     */
    private static List<String> getChildIds(Node node) {
        List<String> ids = new ArrayList<>();
        NodeList nodeChildren = node.getChildNodes();

        for (int j = 0; j < nodeChildren.getLength(); j++) {
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

    private static List<String> getLaneSetFlowNodeRefIds(Node node) {
        List<String> ids = new ArrayList<>();
        NodeList nodeChildren = node.getChildNodes();

        for (int j = 0; j < nodeChildren.getLength(); j++) {
            Node nodeChild = nodeChildren.item(j);
            if (FLOW_NODE_REF_TAGS.contains(nodeChild.getNodeName())) {
                ids.add(nodeChild.getTextContent());
            } else {
                ids.addAll(getLaneSetFlowNodeRefIds(nodeChild));
            }
        }
        ids.removeIf(Objects::isNull);
        return ids;
    }

    private static List<String> getReferencedIds(Node node) {
        List<String> ids = new ArrayList<>();
        List<String> idRefBaseTags = List.of("incoming", "outgoing", "flowNodeRef", "sourceRef", "targetRef");
        List<String> idRefCompleteTags = new ArrayList<>(idRefBaseTags);
        idRefBaseTags.forEach(tag -> idRefCompleteTags.add(BPMN_ELEMENT_PREFIX + tag));

        if (idRefCompleteTags.contains(node.getNodeName()) && node.getTextContent() != null) {
            ids.add(node.getTextContent());
        }

        for (Node child : convertToList(node.getChildNodes())) {
            ids.addAll(getReferencedIds(child));
        }

        ids.removeIf(Objects::isNull);
        return ids.stream().distinct().collect(Collectors.toList());
    }

    private static List<String> getLaneSetRelatedElementIds(Node laneSet) {
        List<String> flowNodeRefIds = getLaneSetFlowNodeRefIds(laneSet);
        List<String> laneSetRelatedElementIds = new ArrayList<>(flowNodeRefIds);

        NodeList laneSetSiblings = laneSet.getParentNode().getChildNodes();
        for (int i = 0; i < laneSetSiblings.getLength(); i++) {
            Node laneSetSibling = laneSetSiblings.item(i);

            if (laneSetSibling.getAttributes() == null || MESSAGE_FLOW_TAGS.contains(laneSetSibling.getNodeName())) {
                continue;
            }

            Node id = laneSetSibling.getAttributes().getNamedItem("id");
            if (id == null) {
                continue;
            }

            if (flowNodeRefIds.contains(id.getTextContent())) {
                laneSetRelatedElementIds.addAll(getReferencedIds(laneSetSibling));
            }

            //Handle edges which reference nodes in laneSet
            Node sourceRef = laneSetSibling.getAttributes().getNamedItem("sourceRef");
            Node targetRef = laneSetSibling.getAttributes().getNamedItem("targetRef");
            String sourceRefId = sourceRef == null ? null : sourceRef.getTextContent();
            String targetRefId = targetRef == null ? null : targetRef.getTextContent();

            if (flowNodeRefIds.contains(sourceRefId) || flowNodeRefIds.contains(targetRefId)) {
                laneSetRelatedElementIds.add(id.getTextContent());
                laneSetRelatedElementIds.add(sourceRefId);
                laneSetRelatedElementIds.add(targetRefId);
            }
        }

        laneSetRelatedElementIds.removeIf(Objects::isNull);
        return laneSetRelatedElementIds.stream().distinct().collect(Collectors.toList());
    }

    private static boolean isEmptyBpmn(Document document) {
        List<Node> participantNodes = getBPMNElements(document, "participant");
        List<Node> processNodes = getBPMNElements(document, BPMN_PROCESS_TAG);

        return participantNodes.isEmpty() && processNodes.size() == 1 && !processNodes.get(0).hasChildNodes();
    }
}
