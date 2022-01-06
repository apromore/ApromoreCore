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
package org.apromore.processsimulation.service;

import org.apache.commons.io.IOUtils;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.processdiscoverer.abstraction.AbstractAbstraction;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.Distribution;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Errors;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.TimeUnit;
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
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimulationInfoServiceTest {

    private final SimulationInfoService simulationInfoService = SimulationInfoService.getInstance();

    /**
     * Test method encapsulating all other tests to test capturing sonar cloud coverage
     */
    @Test
    void testDeriveSimulationInfo() {
        should_successfully_derive_simulation_info();
        should_return_null_if_no_attribute_log();
        should_return_null_if_no_log_summary();
    }

    /**
     * Test method encapsulating all other tests to test capturing sonar cloud coverage
     */
    @Test
    void testEnrichWithSimulationInfo() throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        should_enrich_with_simulation_info();
        should_enrich_with_simulation_info_for_model_with_no_xmlns_prefix();
        should_not_enrich_if_no_process_simulation_info();
    }

    @Test
    void should_successfully_derive_simulation_info() {
        // given
        AbstractAbstraction mockAbstraction = mock(AbstractAbstraction.class);
        AttributeLog mockAttributeLog = mock(AttributeLog.class);
        AttributeLogSummary mockAttributeLogSummary = mock(AttributeLogSummary.class);
        BPMNDiagram mockDiagram = mock(BPMNDiagram.class);

        when(mockAbstraction.getLog()).thenReturn(mockAttributeLog);
        when(mockAttributeLog.getLogSummary()).thenReturn(mockAttributeLogSummary);
        when(mockAttributeLogSummary.getCaseCount()).thenReturn(100L);
        when(mockAttributeLogSummary.getStartTime()).thenReturn(1577797200000L);
        when(mockAttributeLogSummary.getEndTime()).thenReturn(1580475600000L);
        when(mockAbstraction.getDiagram()).thenReturn(mockDiagram);
        when(mockDiagram.getNodes()).thenReturn(null);

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAbstraction);

        // then
        assertNotNull(processSimulationInfo.getId());
        assertNotNull(processSimulationInfo.getErrors());
        assertEquals(100L, processSimulationInfo.getProcessInstances());
        assertEquals("26784", processSimulationInfo.getArrivalRateDistribution().getArg1());
        assertEquals("NaN", processSimulationInfo.getArrivalRateDistribution().getArg2());
        assertEquals("NaN", processSimulationInfo.getArrivalRateDistribution().getMean());
        assertEquals(TimeUnit.SECONDS, processSimulationInfo.getArrivalRateDistribution().getTimeUnit());
        assertEquals(DistributionType.EXPONENTIAL, processSimulationInfo.getArrivalRateDistribution().getType());
        assertEquals("2019-12-31T13:00:00Z", processSimulationInfo.getStartDateTime());
        assertEquals(Currency.EUR, processSimulationInfo.getCurrency());

    }

    @Test
    void should_return_null_if_no_attribute_log() {
        // given
        AbstractAbstraction mockAbstraction = mock(AbstractAbstraction.class);
        when(mockAbstraction.getLog()).thenReturn(null);

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAbstraction);

        // then
        assertNull(processSimulationInfo);
    }

    @Test
    void should_return_null_if_no_log_summary() {
        // given
        AbstractAbstraction mockAbstraction = mock(AbstractAbstraction.class);
        AttributeLog mockAttributeLog = mock(AttributeLog.class);
        when(mockAbstraction.getLog()).thenReturn(mockAttributeLog);
        when(mockAttributeLog.getLogSummary()).thenReturn(null);

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAbstraction);

        // then
        assertNull(processSimulationInfo);
    }

    @Test
    void should_enrich_with_simulation_info() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo = createMockProcessSimulationInfo();

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnProcessSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_simulation_info_for_model_with_no_xmlns_prefix() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = readBpmnFile("/no_simulation_info_without_namespace_prefix.bpmn");
        ProcessSimulationInfo processSimulationInfo = createMockProcessSimulationInfo();

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnProcessSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_not_enrich_if_no_process_simulation_info() throws IOException {
        // given
        String originalBpmn = readBpmnFile("/no_simulation_info.bpmn");

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(originalBpmn, null);

        // then
        assertEquals(originalBpmn, enrichedBpmn);
    }

    private void assertBpmnProcessSimulationInfo(String bpmnXmlString)
            throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        Node processSimulationInfoXmlNode = getProcessSimulationInfo(bpmnXmlString,
                        "/definitions/process/extensionElements/processSimulationInfo");

        NamedNodeMap processSimulationAttrMap = processSimulationInfoXmlNode.getAttributes();
        assertNotNull(processSimulationAttrMap.getNamedItem("id").getNodeValue());
        assertEquals(Currency.EUR.toString(), processSimulationAttrMap.getNamedItem("currency").getNodeValue());
        assertEquals("100", processSimulationAttrMap.getNamedItem("processInstances").getNodeValue());
        assertEquals("2019-12-31T13:00:00Z", processSimulationAttrMap.getNamedItem("startDateTime").getNodeValue());

        Node arrivalDistributionXmlNode = getProcessSimulationInfo(bpmnXmlString,
                "/definitions/process/extensionElements/processSimulationInfo/arrivalRateDistribution");
        NamedNodeMap arrivalRateDistributionAttrMap = arrivalDistributionXmlNode.getAttributes();
        assertEquals("26784", arrivalRateDistributionAttrMap.getNamedItem("arg1").getNodeValue());
        assertEquals("NaN", arrivalRateDistributionAttrMap.getNamedItem("arg2").getNodeValue());
        assertEquals("NaN", arrivalRateDistributionAttrMap.getNamedItem("mean").getNodeValue());
        assertEquals(DistributionType.EXPONENTIAL.toString(), arrivalRateDistributionAttrMap.getNamedItem("type").getNodeValue());

        Node timeUnitXmlNode = getProcessSimulationInfo(bpmnXmlString,
                "/definitions/process/extensionElements/processSimulationInfo/arrivalRateDistribution/timeUnit");
        assertEquals("seconds", timeUnitXmlNode.getFirstChild().getNodeValue());

    }

    private Node getProcessSimulationInfo(String bpmnXml, String xpathExpression)
            throws ParserConfigurationException, XPathExpressionException, IOException, SAXException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new ByteArrayInputStream(bpmnXml.getBytes()));
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (Node) xPath.compile(xpathExpression).evaluate(xmlDocument, XPathConstants.NODE);
    }

    private ProcessSimulationInfo createMockProcessSimulationInfo() {
        return ProcessSimulationInfo.builder()
                .id("some_random_guid")
                .errors(Errors.builder().build())
                .currency(Currency.EUR)
                .startDateTime(Instant.ofEpochMilli(1577797200000L).toString())
                .processInstances(100)
                .arrivalRateDistribution(
                        Distribution.builder()
                                .type(DistributionType.EXPONENTIAL)
                                .arg1("26784")
                                .arg2("NaN")
                                .mean("NaN")
                                .timeUnit(TimeUnit.SECONDS)
                                .build()
                )
                .build();
    }

    private String readBpmnFile(String fileName) throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream(fileName),
                StandardCharsets.UTF_8);
    }

}