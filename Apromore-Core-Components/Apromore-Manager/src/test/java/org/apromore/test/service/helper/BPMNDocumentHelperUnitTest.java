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

import java.io.IOException;
import java.util.Map;
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

}
