/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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
package org.apromore.plugin.portal.processdiscoverer;

import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.plugin.portal.processdiscoverer.data.processSimulation.Currency;
import org.apromore.plugin.portal.processdiscoverer.data.processSimulation.Errors;
import org.apromore.plugin.portal.processdiscoverer.data.processSimulation.ProcessSimulationInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimulationInfoServiceTest {

    private final SimulationInfoService simulationInfoService = SimulationInfoService.getInstance();

    @Test
    void should_successfully_derive_simulation_info() {
        // given
        AttributeLog mockAttributeLog = mock(AttributeLog.class);
        AttributeLogSummary mockAttributeLogSummary = mock(AttributeLogSummary.class);
        when(mockAttributeLog.getLogSummary()).thenReturn(mockAttributeLogSummary);
        when(mockAttributeLogSummary.getCaseCount()).thenReturn(10L);
        when(mockAttributeLogSummary.getStartTime()).thenReturn(1577797200000L);

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAttributeLog);

        // then
        assertEquals(10L, processSimulationInfo.getProcessInstances());
        assertEquals("2019-12-31T13:00:00Z", processSimulationInfo.getStartDateTime());
        assertEquals(Currency.EUR.toString(), processSimulationInfo.getCurrency());
        assertNotNull(processSimulationInfo.getId());
        assertNotNull(processSimulationInfo.getErrors());
    }

    @Test
    void should_return_null_if_no_attribute_log() {
        // given
        AttributeLog mockAttributeLog = null;

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAttributeLog);

        // then
        assertNull(processSimulationInfo);
    }

    @Test
    void should_return_null_if_no_log_summary() {
        // given
        AttributeLog mockAttributeLog = mock(AttributeLog.class);
        when(mockAttributeLog.getLogSummary()).thenReturn(null);

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAttributeLog);

        // then
        assertNull(processSimulationInfo);
    }

    @Test
    void should_enrich_with_simulation_info() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = readBpmnFile("src/test/logs/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo = createMockProcessSimulationInfo();

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnProcessSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_simulation_info_for_model_with_no_xmlns_prefix() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = readBpmnFile("src/test/logs/no_simulation_info_without_namespace_prefix.bpmn");
        ProcessSimulationInfo processSimulationInfo = createMockProcessSimulationInfo();

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnProcessSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_not_enrich_if_no_process_simulation_info() throws IOException {
        // given
        String originalBpmn = readBpmnFile("src/test/logs/no_simulation_info.bpmn");

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(originalBpmn, null);

        // then
        assertEquals(originalBpmn, enrichedBpmn);
    }

    private void assertBpmnProcessSimulationInfo(String bpmnXmlString)
            throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        Node processSimulationInfoXmlNode = getProcessSimulationInfo(bpmnXmlString);

        NamedNodeMap processSimulationAttrMap = processSimulationInfoXmlNode.getAttributes();
        assertEquals(Currency.EUR.toString(), processSimulationAttrMap.getNamedItem("currency").getNodeValue());
        assertEquals("10", processSimulationAttrMap.getNamedItem("processInstances").getNodeValue());
        assertEquals("2019-12-31T13:00:00Z", processSimulationAttrMap.getNamedItem("startDateTime").getNodeValue());
        assertNotNull(processSimulationAttrMap.getNamedItem("id").getNodeValue());
    }

    private Node getProcessSimulationInfo(String bpmnXml)
            throws ParserConfigurationException, XPathExpressionException, IOException, SAXException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new ByteArrayInputStream(bpmnXml.getBytes()));
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/definitions/process/extensionElements/processSimulationInfo";
        return (Node) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);
    }

    private ProcessSimulationInfo createMockProcessSimulationInfo() {
        return ProcessSimulationInfo.builder()
                .currency(Currency.EUR.toString())
                .startDateTime(Instant.ofEpochMilli(1577797200000L).toString())
                .id("some_random_guid")
                .errors(Errors.builder().build())
                .processInstances(10)
                .build();
    }

    protected String readBpmnFile(String fullFilePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fullFilePath));
        return new String(encoded, StandardCharsets.UTF_8);
    }

}