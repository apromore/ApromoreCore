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


package org.apromore.test.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apromore.exception.ExportFormatException;
import org.apromore.service.helper.BPMNDocumentHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

class BPMNDocumentHelperUnitTest {

    @Test
    void getDocumentInvalidXml() {
        assertThrows(Exception.class, () -> BPMNDocumentHelper.getDocument(""));
        assertThrows(Exception.class, () -> BPMNDocumentHelper.getDocument("</>"));
        assertThrows(Exception.class, () -> BPMNDocumentHelper.getDocument("<test>"));
        assertThrows(Exception.class, () -> BPMNDocumentHelper.getDocument("</test>"));
        assertThrows(Exception.class, () -> BPMNDocumentHelper.getDocument("Test"));
    }

    @Test
    void getDocumentValidXml() {
        Document document;
        try {
            document = BPMNDocumentHelper.getDocument("<bpmn/>");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail();
            throw new RuntimeException(e);
        }
        assertNotNull(document);
        assertThat(document.getElementsByTagName("bpmn").getLength()).isEqualTo(1);
    }

    @Test
    void getXMLString() {
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String originalXML = "<bpmn/>";

        try {
            Document document = BPMNDocumentHelper.getDocument(originalXML);
            String xml = BPMNDocumentHelper.getXMLString(document);
            assertThat(xml).isEqualTo(xmlHeader + originalXML);
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            fail();
            throw new RuntimeException(e);
        }

    }

    @Test
    void getBPMNElements() {
        String originalXML = "<bpmn><bpmn:process/><process/></bpmn>";
        try {
            Document document = BPMNDocumentHelper.getDocument(originalXML);

            assertThat(BPMNDocumentHelper.getBPMNElements(document, "bpmn:process")).hasSize(1);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "process")).hasSize(2);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "subprocess")).isEmpty();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    void getBPMNElement() {
        String originalXML = "<bpmn><bpmn:process id=\"test\"/><process/></bpmn>";
        try {
            Document document = BPMNDocumentHelper.getDocument(originalXML);

            assertThat(BPMNDocumentHelper.getBPMNElement(document, "process", "test")).isNotNull();
            assertThat(BPMNDocumentHelper.getBPMNElement(document, "process", "")).isNull();
            assertThat(BPMNDocumentHelper.getBPMNElement(document, "subprocess", "test")).isNull();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "<bpmn><process><subProcess/></process></bpmn>, <bpmn><bpmn:definitions/></bpmn>",
        "<bpmn><bpmn:definitions><process><subProcess/></process></bpmn:definitions></bpmn>, <bpmn/>",
        "<bpmn><bpmn:definitions><process><subProcess/></process></bpmn:definitions></bpmn>, <bpmn><bpmn:definitions/></bpmn>"
    })
    void replaceSubprocessContentsInvalidDocument(String originalXML, String linkedProcessXML) {
        try {
            Document document = BPMNDocumentHelper.getDocument(originalXML);
            Document document2 = BPMNDocumentHelper.getDocument(linkedProcessXML);

            Node subProcessNode = BPMNDocumentHelper.getBPMNElements(document, "subProcess").get(0);
            assertThrows(ExportFormatException.class, () -> BPMNDocumentHelper.replaceSubprocessContents(subProcessNode, document2));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    void replaceSubprocessContents() {
        String originalXML = "<bpmn:definitions xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" id=\"Definitions_1\">"
            + "<bpmn><process id=\"p1\"><subProcess id=\"sp1\">"
            + "<bpmn:documentation textFormat=\"text/x-comments\">admin:comment</bpmn:documentation>"
            + "<extensionElements/><incoming/><outgoing/>"
            + "<startEvent id=\"start1\"/><endEvent id=\"end1\"/>"
            + "</subProcess></process></bpmn>"
            + "<bpmndi:BPMNDiagram><bpmndi:BPMNPlane bpmnElement=\"p1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"sp1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"start1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"end1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></bpmn:definitions>";
        String linkedProcessXML = "<bpmn:definitions xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" id=\"Definitions_2\">"
            + "<bpmn><process id=\"link_p1\"><extensionElements><test/></extensionElements>"
            + "<task id=\"link_t1\"><extensionElements/><incoming>edge1</incoming><outgoing>edge2</outgoing></task>"
            + "<sequenceFlow id=\"edge1\"/>"
            + "<sequenceFlow id=\"edge2\"/>"
            + "</process></bpmn>"
            + "<bpmndi:BPMNDiagram><bpmndi:BPMNPlane bpmnElement=\"link_p1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"link_t1\"/>"
            + "<bpmndi:BPMNEdge bpmnElement=\"edge1\"/>"
            + "<bpmndi:BPMNEdge bpmnElement=\"edge2\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></bpmn:definitions>";
        try {
            Document document = BPMNDocumentHelper.getDocument(originalXML);
            Document document2 = BPMNDocumentHelper.getDocument(linkedProcessXML);

            String xmlBeforeReplace = BPMNDocumentHelper.getXMLString(document);

            //Check elements before replace
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "process")).hasSize(1);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "subProcess")).hasSize(1);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "startEvent")).hasSize(1);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "endEvent")).hasSize(1);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "task")).isEmpty();
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "sequenceFlow")).isEmpty();
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "extensionElements")).hasSize(1);

            Node subProcessNode = BPMNDocumentHelper.getBPMNElements(document, "subProcess").get(0);
            BPMNDocumentHelper.replaceSubprocessContents(subProcessNode, document2);

            //Check elements after replace
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "process")).hasSize(1);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "subProcess")).hasSize(1);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "startEvent")).isEmpty();
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "endEvent")).isEmpty();
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "task")).hasSize(1);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "sequenceFlow")).hasSize(2);
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "extensionElements")).hasSize(2);

            //Check definitions after replace
            assertThat(BPMNDocumentHelper.getBPMNElements(document, "definitions")).hasSize(1);
            Node bpmnDefinitions = BPMNDocumentHelper.getBPMNElements(document, "definitions").get(0);
            assertThat(bpmnDefinitions.getAttributes().getNamedItem("id").getNodeValue()).isEqualTo("Definitions_1");
            assertThat(bpmnDefinitions.getAttributes().getNamedItem("xmlns:bpmndi").getNodeValue()).isEqualTo("http://www.omg.org/spec/BPMN/20100524/DI");
            assertThat(bpmnDefinitions.getAttributes().getNamedItem("xmlns:di").getNodeValue()).isEqualTo("http://www.omg.org/spec/DD/20100524/DI");

        } catch (ParserConfigurationException | IOException | SAXException | ExportFormatException |
                 TransformerException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    void getDocumentElement() {
        String originalXML = "<bpmn:definitions>"
            + "<bpmn><process id=\"p1\"><subProcess id=\"sp1\">"
            + "<bpmn:documentation textFormat=\"text/x-comments\">admin:comment</bpmn:documentation>"
            + "<extensionElements/><incoming/><outgoing/>"
            + "<startEvent id=\"start1\"/><endEvent id=\"end1\"/>"
            + "</subProcess></process></bpmn>"
            + "<bpmndi:BPMNDiagram><bpmndi:BPMNPlane bpmnElement=\"p1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"sp1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"start1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"end1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></bpmn:definitions>";

        try {
            Document doc = BPMNDocumentHelper.getDocument(originalXML);

            assertThat(BPMNDocumentHelper.getDiagramElement(doc, "BPMNShape", "end1")).isNotNull();
            assertThat(BPMNDocumentHelper.getDiagramElement(doc, "BPMNShape", "")).isNull();
            assertThat(BPMNDocumentHelper.getDiagramElement(doc, "BPMNDiagram", "")).isNull();

        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    void convertToBusinessProcessDiagram() throws Exception {
        String collaborationModel = CharStreams.toString(
            new InputStreamReader(getResourceAsStream("BPMN_models/collaborationModel.bpmn"))
        ).trim();

        Document collaborationModelDocument = BPMNDocumentHelper.getDocument(collaborationModel);
        List<Node> collaborationParticipants = BPMNDocumentHelper
            .getBPMNElements(collaborationModelDocument, "participant");

        Document bpdDocument = BPMNDocumentHelper.convertToBusinessProcessDiagram(collaborationModelDocument);

        assertThat(BPMNDocumentHelper.getBPMNElements(bpdDocument, "collaboration")).isEmpty();
        assertThat(BPMNDocumentHelper.getBPMNElements(bpdDocument, "participant")).isEmpty();
        assertThat(BPMNDocumentHelper.getBPMNElements(bpdDocument, "process")).hasSize(1);

        List<Node> bpdLaneSetElements = BPMNDocumentHelper.getBPMNElements(bpdDocument, "laneSet");
        assertThat(bpdLaneSetElements).hasSize(collaborationParticipants.size());

        //Assert that laneSets have ids and names of old participants
        List<String> participantIds = getNodesAttributeTextContentList(collaborationParticipants, "id");
        List<String> laneSetIds = getNodesAttributeTextContentList(bpdLaneSetElements, "id");
        assertThat(laneSetIds).isEqualTo(participantIds);

        List<String> participantNames = getNodesAttributeTextContentList(collaborationParticipants, "name");
        List<String> laneSetNames = getNodesAttributeTextContentList(bpdLaneSetElements, "name");
        assertThat(laneSetNames).isEqualTo(participantNames);
    }

    @Test
    void convertToBusinessProcessDiagramMissingReferencedProcess() throws Exception {
        String collaborationModel = CharStreams.toString(
            new InputStreamReader(getResourceAsStream("BPMN_models/collaborationModelWithMissingProcess.bpmn"))
        ).trim();

        Document collaborationModelDocument = BPMNDocumentHelper.getDocument(collaborationModel);
        assertThrows(ExportFormatException.class, () -> BPMNDocumentHelper.convertToBusinessProcessDiagram(collaborationModelDocument));
    }

    @Test
    void getSubprocessBpmnNoPlanes() {
        String originalXML = "<bpmn:definitions>"
            + "<bpmn><process id=\"p1\">"
            + "<subProcess id=\"sp1\">"
            + "<bpmn:documentation textFormat=\"text/x-comments\">admin:comment</bpmn:documentation>"
            + "<extensionElements/><incoming/><outgoing/>"
            + "<sequenceFlow id=\"flow1\" sourceRef=\"start1\" targetRef=\"end1\"/>"
            + "<startEvent id=\"start1\"><outgoing>flow1</outgoing></startEvent>"
            + "<endEvent id=\"end1\"><incoming>flow1</incoming></endEvent>"
            + "</subProcess>"
            + "<bpmn:subProcess id=\"sp2\">"
            + "<task id=\"task1\"/>"
            + "</bpmn:subProcess>"
            + "</process></bpmn>"
            + "<bpmndi:BPMNDiagram/></bpmn:definitions>";
        try {
            Document document = BPMNDocumentHelper.getDocument(originalXML);
            assertThrows(ExportFormatException.class, () -> BPMNDocumentHelper.getSubprocessBpmnMap(document, true));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSubprocessBpmnIncludeEmptySubprocesses() {
        String originalXML = "<bpmn:definitions>"
            + "<bpmn><process id=\"p1\">"
            + "<subProcess id=\"sp1\">"
            + "<bpmn:documentation textFormat=\"text/x-comments\">admin:comment</bpmn:documentation>"
            + "<extensionElements/><incoming/><outgoing/>"
            + "<sequenceFlow id=\"flow1\" sourceRef=\"start1\" targetRef=\"end1\"/>"
            + "<startEvent id=\"start1\"><outgoing>flow1</outgoing></startEvent>"
            + "<endEvent id=\"end1\"><incoming>flow1</incoming></endEvent>"
            + "</subProcess>"
            + "<bpmn:subProcess id=\"sp2\">"
            + "<task id=\"task1\"/>"
            + "</bpmn:subProcess>"
            + "<bpmn:subProcess id=\"sp3\"/>"
            + "</process></bpmn>"
            + "<bpmndi:BPMNDiagram><bpmndi:BPMNPlane bpmnElement=\"p1\">"
            + "<bpmndi:BPMNEdge bpmnElement=\"flow1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"sp1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"start1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"end1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "<bpmndi:BPMNPlane bpmnElement=\"sp2\">"
            + "<bpmndi:BPMNShape bpmnElement=\"task1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></bpmn:definitions>";

        String expectedSubprocess1XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\">"
            + "<process id=\"Process_1\">"
            + "<bpmn:documentation textFormat=\"text/x-comments\">admin:comment</bpmn:documentation>"
            + "<sequenceFlow id=\"flow1\" sourceRef=\"start1\" targetRef=\"end1\"/>"
            + "<startEvent id=\"start1\"><outgoing>flow1</outgoing></startEvent>"
            + "<endEvent id=\"end1\"><incoming>flow1</incoming></endEvent>"
            + "</process>"
            + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\"><bpmndi:BPMNPlane bpmnElement=\"Process_1\">"
            + "<bpmndi:BPMNEdge bpmnElement=\"flow1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"start1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"end1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></definitions>";

        String expectedSubprocess2XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\">"
            + "<process id=\"Process_1\">"
            + "<task id=\"task1\"/>"
            + "</process>"
            + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\"><bpmndi:BPMNPlane bpmnElement=\"Process_1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"task1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></definitions>";

        String expectedSubprocess3XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\">"
            + "<process id=\"Process_1\"/>"
            + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">"
            + "<bpmndi:BPMNPlane bpmnElement=\"Process_1\"/>"
            + "</bpmndi:BPMNDiagram></definitions>";

        try {
            Document doc = BPMNDocumentHelper.getDocument(originalXML);
            Map<String, String> subprocessBpmnMap = BPMNDocumentHelper.getSubprocessBpmnMap(doc, true);

            assertThat(subprocessBpmnMap).hasSize(3);
            assertThat(subprocessBpmnMap).containsEntry("sp1", expectedSubprocess1XML);
            assertThat(subprocessBpmnMap).containsEntry("sp2", expectedSubprocess2XML);
            assertThat(subprocessBpmnMap).containsEntry("sp3", expectedSubprocess3XML);

        } catch (ParserConfigurationException | IOException | SAXException
                 | ExportFormatException | TransformerException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSubprocessBpmnIgnoreEmptySubprocesses() {
        String originalXML = "<bpmn:definitions>"
            + "<bpmn><process id=\"p1\">"
            + "<subProcess id=\"sp1\">"
            + "<extensionElements/><incoming/><outgoing/>"
            + "</subProcess>"
            + "<bpmn:subProcess id=\"sp2\">"
            + "<incoming/><outgoing/>"
            + "<task id=\"task1\"/>"
            + "</bpmn:subProcess>"
            + "<bpmn:subProcess id=\"sp3\"/>"
            + "</process></bpmn>"
            + "<bpmndi:BPMNDiagram><bpmndi:BPMNPlane bpmnElement=\"p1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"sp1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"sp2\"/>"
            + "</bpmndi:BPMNPlane>"
            + "<bpmndi:BPMNPlane bpmnElement=\"sp2\">"
            + "<bpmndi:BPMNShape bpmnElement=\"task1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></bpmn:definitions>";

        String expectedSubprocess2XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\">"
            + "<process id=\"Process_1\">"
            + "<task id=\"task1\"/>"
            + "</process>"
            + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\"><bpmndi:BPMNPlane bpmnElement=\"Process_1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"task1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></definitions>";

        try {
            Document doc = BPMNDocumentHelper.getDocument(originalXML);
            Map<String, String> subprocessBpmnMap = BPMNDocumentHelper.getSubprocessBpmnMap(doc, false);

            assertThat(subprocessBpmnMap).hasSize(1);
            assertThat(subprocessBpmnMap).containsEntry("sp2", expectedSubprocess2XML);

        } catch (ParserConfigurationException | IOException | SAXException
                 | ExportFormatException | TransformerException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSubprocessBpmnWithLaneSets() {
        String originalXML = "<bpmn:definitions>"
            + "<bpmn><process id=\"p1\">"
            + "<subProcess id=\"sp1\">"
            + "<extensionElements/><incoming/><outgoing/>"
            + "<laneSet id=\"ls1\" name=\"Empty Example\"/>"
            + "</subProcess>"
            + "<bpmn:subProcess id=\"sp2\">"
            + "<incoming/><outgoing/>"
            + "<laneSet id=\"ls2\" name=\"Empty Example 2\"/>"
            + "<laneSet><lane id=\"lane1\">"
            + "<flowNodeRef>start1</flowNodeRef>"
            + "<flowNodeRef>task1</flowNodeRef>"
            + "<flowNodeRef>end1</flowNodeRef>"
            + "</lane></laneSet>"
            + "<startEvent id=\"start1\"><outgoing>flow1</outgoing></startEvent>"
            + "<endEvent id=\"end1\"><incoming>flow2</incoming></endEvent>"
            + "<task id=\"task1\"><incoming>flow1</incoming><outgoing>flow2</outgoing></task>"
            + "<sequenceFlow id=\"flow1\" sourceRef=\"start1\" targetRef=\"task1\"/>"
            + "<sequenceFlow id=\"flow2\" sourceRef=\"task1\" targetRef=\"end1\"/>"
            + "<messageFlow  id=\"flow3\" sourceRef=\"ls1\" targetRef=\"ls2\"/>"
            + "</bpmn:subProcess>"
            + "<bpmn:subProcess id=\"sp3\"/>"
            + "</process></bpmn>"
            + "<bpmndi:BPMNDiagram><bpmndi:BPMNPlane bpmnElement=\"p1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"sp1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"sp2\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"ls1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"ls2\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"lane1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "<bpmndi:BPMNPlane bpmnElement=\"sp2\">"
            + "<bpmndi:BPMNShape bpmnElement=\"start1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"end1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"task1\"/>"
            + "<bpmndi:BPMNEdge bpmnElement=\"flow1\"/>"
            + "<bpmndi:BPMNEdge bpmnElement=\"flow2\"/>"
            + "<bpmndi:BPMNEdge bpmnElement=\"flow3\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram></bpmn:definitions>";

        String expectedSubprocess1XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\">"
            + "<bpmn:collaboration id=\"Collaboration_1\">"
            + "<bpmn:participant id=\"ls1\" name=\"Empty Example\"/>"
            + "</bpmn:collaboration>"
            + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">"
            + "<bpmndi:BPMNPlane bpmnElement=\"Collaboration_1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"ls1\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram>"
            + "</definitions>";

        String expectedSubprocess2XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\">"
            + "<bpmn:collaboration id=\"Collaboration_1\">"
            + "<bpmn:participant id=\"ls2\" name=\"Empty Example 2\"/>"
            + "<bpmn:participant id=\"Participant_2\" processRef=\"Process_2\"/>"
            + "<messageFlow id=\"flow3\" sourceRef=\"ls1\" targetRef=\"ls2\"/>"
            + "</bpmn:collaboration>"
            + "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">"
            + "<bpmndi:BPMNPlane bpmnElement=\"Collaboration_1\">"
            + "<bpmndi:BPMNShape bpmnElement=\"ls2\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"lane1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"start1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"end1\"/>"
            + "<bpmndi:BPMNShape bpmnElement=\"task1\"/>"
            + "<bpmndi:BPMNEdge bpmnElement=\"flow1\"/>"
            + "<bpmndi:BPMNEdge bpmnElement=\"flow2\"/>"
            + "<bpmndi:BPMNEdge bpmnElement=\"flow3\"/>"
            + "</bpmndi:BPMNPlane>"
            + "</bpmndi:BPMNDiagram>"
            + "<bpmn:process id=\"Process_2\">"
            + "<laneSet id=\"LaneSet_2\"><lane id=\"lane1\">"
            + "<flowNodeRef>start1</flowNodeRef>"
            + "<flowNodeRef>task1</flowNodeRef>"
            + "<flowNodeRef>end1</flowNodeRef>"
            + "</lane></laneSet>"
            + "<startEvent id=\"start1\"><outgoing>flow1</outgoing></startEvent>"
            + "<endEvent id=\"end1\"><incoming>flow2</incoming></endEvent>"
            + "<task id=\"task1\"><incoming>flow1</incoming><outgoing>flow2</outgoing></task>"
            + "<sequenceFlow id=\"flow1\" sourceRef=\"start1\" targetRef=\"task1\"/>"
            + "<sequenceFlow id=\"flow2\" sourceRef=\"task1\" targetRef=\"end1\"/>"
            + "</bpmn:process>"
            + "</definitions>";

        try {
            Document doc = BPMNDocumentHelper.getDocument(originalXML);
            Map<String, String> subprocessBpmnMap = BPMNDocumentHelper.getSubprocessBpmnMap(doc, false);

            assertThat(subprocessBpmnMap).hasSize(2);
            assertThat(subprocessBpmnMap).containsEntry("sp1", expectedSubprocess1XML);
            assertThat(subprocessBpmnMap).containsEntry("sp2", expectedSubprocess2XML);

        } catch (ParserConfigurationException | IOException | SAXException
                 | ExportFormatException | TransformerException e) {
            fail();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param path the classpath of a desired resource within the test JAR
     * @return the content of the resource at <var>path</var>
     * @throws Exception if <var>path</var> doesn't match a resource in the test JAR
     */
    private InputStream getResourceAsStream(String path) throws Exception {
        InputStream result = getClass().getClassLoader().getResourceAsStream(path);
        if (result == null) {
            throw new Exception(path + " is not a resource");
        }

        return result;
    }

    private List<String> getNodesAttributeTextContentList(List<Node> nodes, String attribute) {
        return nodes.stream()
            .map(n -> n.getAttributes().getNamedItem(attribute).getTextContent())
            .sorted().collect(Collectors.toList());
    }

}
