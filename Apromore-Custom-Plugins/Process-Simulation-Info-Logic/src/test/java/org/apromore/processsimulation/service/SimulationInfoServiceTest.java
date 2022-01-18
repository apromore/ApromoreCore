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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.apromore.logman.attribute.graph.AttributeLogGraph;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.processdiscoverer.abstraction.AbstractAbstraction;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processsimulation.config.SimulationInfoConfig;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

class SimulationInfoServiceTest {

    private SimulationInfoService simulationInfoService;

    @Mock
    private SimulationInfoConfig config;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        simulationInfoService = new SimulationInfoService(config);

        when(config.isEnable()).thenReturn(true);
        when(config.getDefaultCurrency()).thenReturn("EUR");
        when(config.getDefaultDistributionType()).thenReturn("EXPONENTIAL");
        when(config.getDefaultTimeUnit()).thenReturn("SECONDS");

        Map<String, String> timeTableConfigMap = new HashMap<>();
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMETABLE_ID_KEY, "DEFAULT_TIMETABLE");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMETABLE_NAME_KEY, "Arrival Timetable");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_NAME_KEY, "Default Timeslot");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_FROM_TIME, "10:00:00.000+00:00");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_TO_TIME, "15:00:00.000+00:00");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_FROM_WEEKDAY_KEY, "MONDAY");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_TO_WEEKDAY_KEY, "THURSDAY");
        when(config.getDefaultTimetable()).thenReturn(timeTableConfigMap);
    }

    @Test
    void should_successfully_derive_general_simulation_info() {
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
        assertGeneralSimulationInfo(processSimulationInfo);
    }

    @Test
    void should_successfully_derive_task_simulation_info() throws Exception {
        // given
        AbstractAbstraction mockAbstraction = mock(AbstractAbstraction.class);
        AttributeLog mockAttributeLog = mock(AttributeLog.class);
        AttributeLogSummary mockAttributeLogSummary = mock(AttributeLogSummary.class);
        AttributeLogGraph mockAttributeLogGraph = mock(AttributeLogGraph.class);
        BPMNDiagram mockDiagram = TestHelper.readBpmnDiagram("/no_simulation_info_without_namespace_prefix.bpmn");

        when(mockAbstraction.getLog()).thenReturn(mockAttributeLog);
        when(mockAttributeLog.getLogSummary()).thenReturn(mockAttributeLogSummary);
        when(mockAttributeLogSummary.getCaseCount()).thenReturn(100L);
        when(mockAttributeLogSummary.getStartTime()).thenReturn(1577797200000L);
        when(mockAttributeLogSummary.getEndTime()).thenReturn(1580475600000L);

        when(mockAttributeLog.getGraphView()).thenReturn(mockAttributeLogGraph);
        when(mockAttributeLogGraph.getNodeWeight("a", MeasureType.DURATION, MeasureAggregation.MEAN,
            MeasureRelation.ABSOLUTE)).thenReturn(10100.00);
        when(mockAttributeLogGraph.getNodeWeight("b", MeasureType.DURATION, MeasureAggregation.MEAN,
            MeasureRelation.ABSOLUTE)).thenReturn(11110.00);
        when(mockAttributeLogGraph.getNodeWeight("c", MeasureType.DURATION, MeasureAggregation.MEAN,
            MeasureRelation.ABSOLUTE)).thenReturn(12120.00);
        when(mockAbstraction.getDiagram()).thenReturn(mockDiagram);

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAbstraction);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);
        assertEquals(3, processSimulationInfo.getTasks().size());
        assertTrue(
            processSimulationInfo.getTasks().stream()
                .map(element -> element.getElementId())
                .collect(Collectors.toList())
                .containsAll(Arrays.asList("Activity_089vlk4", "Activity_1m9vbxe", "Activity_0qorbah")));

        processSimulationInfo.getTasks().forEach(element -> {
            switch (element.getElementId()) {
                case "Activity_089vlk4":
                    assertEquals("10.10", element.getDistributionDuration().getArg1());
                    break;
                case "Activity_1m9vbxe":
                    assertEquals("11.11", element.getDistributionDuration().getArg1());
                    break;
                case "Activity_0qorbah":
                    assertEquals("12.12", element.getDistributionDuration().getArg1());
                    break;
                default:
                    fail("Unrecognised task identifier");
                    break;
            }

            assertNull(element.getDistributionDuration().getArg2());
            assertNull(element.getDistributionDuration().getMean());
            assertEquals(TimeUnit.SECONDS, element.getDistributionDuration().getTimeUnit());
            assertEquals(DistributionType.EXPONENTIAL, element.getDistributionDuration().getType());
        });

    }

    @Test
    void should_successfully_derive_timetable_info() throws Exception {
        // given
        AbstractAbstraction mockAbstraction = mock(AbstractAbstraction.class);
        AttributeLog mockAttributeLog = mock(AttributeLog.class);
        AttributeLogSummary mockAttributeLogSummary = mock(AttributeLogSummary.class);

        when(mockAbstraction.getLog()).thenReturn(mockAttributeLog);
        when(mockAttributeLog.getLogSummary()).thenReturn(mockAttributeLogSummary);
        when(mockAttributeLogSummary.getCaseCount()).thenReturn(100L);
        when(mockAttributeLogSummary.getStartTime()).thenReturn(1577797200000L);
        when(mockAttributeLogSummary.getEndTime()).thenReturn(1580475600000L);

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAbstraction);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);

        assertNotNull(processSimulationInfo.getTimetables());
        assertEquals(1, processSimulationInfo.getTimetables().size());
        assertEquals("Arrival Timetable", processSimulationInfo.getTimetables().get(0).getName());
        assertEquals("DEFAULT_TIMETABLE", processSimulationInfo.getTimetables().get(0).getId());
        assertTrue(processSimulationInfo.getTimetables().get(0).isDefaultTimetable());

        assertNotNull(processSimulationInfo.getTimetables().get(0).getRules());
        assertEquals(1, processSimulationInfo.getTimetables().get(0).getRules().size());
        assertNotNull(processSimulationInfo.getTimetables().get(0).getRules().get(0).getId());
        assertEquals("Default Timeslot", processSimulationInfo.getTimetables().get(0).getRules().get(0).getName());
        assertEquals("10:00:00.000+00:00",
            processSimulationInfo.getTimetables().get(0).getRules().get(0).getFromTime());
        assertEquals("15:00:00.000+00:00", processSimulationInfo.getTimetables().get(0).getRules().get(0).getToTime());
        assertEquals(DayOfWeek.MONDAY, processSimulationInfo.getTimetables().get(0).getRules().get(0).getFromWeekDay());
        assertEquals(DayOfWeek.THURSDAY, processSimulationInfo.getTimetables().get(0).getRules().get(0).getToWeekDay());
    }

    private void assertGeneralSimulationInfo(final ProcessSimulationInfo processSimulationInfo) {
        assertNotNull(processSimulationInfo.getId());
        assertNotNull(processSimulationInfo.getErrors());
        assertEquals(100L, processSimulationInfo.getProcessInstances());
        assertEquals("26784", processSimulationInfo.getArrivalRateDistribution().getArg1());
        assertNull(processSimulationInfo.getArrivalRateDistribution().getArg2());
        assertNull(processSimulationInfo.getArrivalRateDistribution().getMean());
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
    void should_return_null_if_feature_disabled() {
        // given
        when(config.isEnable()).thenReturn(false);
        AbstractAbstraction mockAbstraction = mock(AbstractAbstraction.class);

        // when
        ProcessSimulationInfo processSimulationInfo = simulationInfoService.deriveSimulationInfo(mockAbstraction);

        // then
        assertNull(processSimulationInfo);
    }

    @Test
    void should_enrich_with_general_simulation_info()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo = TestHelper.createMockProcessSimulationInfo(false);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnGeneralProcessSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_task_simulation_info()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo = TestHelper.createMockProcessSimulationInfo(true);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnGeneralProcessSimulationInfo(enrichedBpmn);
        assertBpmnTaskProcessSimulationInfo(enrichedBpmn);
    }


    @Test
    void should_enrich_with_simulation_info_for_model_with_no_xmlns_prefix()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info_without_namespace_prefix.bpmn");
        ProcessSimulationInfo processSimulationInfo = TestHelper.createMockProcessSimulationInfo(false);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnGeneralProcessSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_not_enrich_if_no_process_simulation_info() throws IOException {
        // given
        String originalBpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(originalBpmn, null);

        // then
        assertEquals(originalBpmn, enrichedBpmn);
    }

    @Test
    void should_not_enrich_if_feature_disabled() throws IOException {
        // given
        when(config.isEnable()).thenReturn(false);
        String originalBpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo = TestHelper.createMockProcessSimulationInfo(false);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(originalBpmn, processSimulationInfo);

        // then
        assertEquals(originalBpmn, enrichedBpmn);
    }

    private void assertBpmnGeneralProcessSimulationInfo(String bpmnXmlString)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        Node processSimulationInfoXmlNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo");

        NamedNodeMap processSimulationAttrMap = processSimulationInfoXmlNode.getAttributes();
        assertNotNull(processSimulationAttrMap.getNamedItem("id").getNodeValue());
        assertEquals(Currency.EUR.toString(), processSimulationAttrMap.getNamedItem("currency").getNodeValue());
        assertEquals("100", processSimulationAttrMap.getNamedItem("processInstances").getNodeValue());
        assertEquals("2019-12-31T13:00:00Z", processSimulationAttrMap.getNamedItem("startDateTime").getNodeValue());

        Node arrivalDistributionXmlNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/arrivalRateDistribution");
        NamedNodeMap arrivalRateDistributionAttrMap = arrivalDistributionXmlNode.getAttributes();
        assertEquals("26784", arrivalRateDistributionAttrMap.getNamedItem("arg1").getNodeValue());
        assertEquals("NaN", arrivalRateDistributionAttrMap.getNamedItem("arg2").getNodeValue());
        assertEquals("NaN", arrivalRateDistributionAttrMap.getNamedItem("mean").getNodeValue());
        assertEquals(DistributionType.EXPONENTIAL.toString(),
            arrivalRateDistributionAttrMap.getNamedItem("type").getNodeValue());

        Node timeUnitXmlNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/arrivalRateDistribution/timeUnit");
        assertEquals("seconds", timeUnitXmlNode.getFirstChild().getNodeValue());

    }

    private void assertBpmnTaskProcessSimulationInfo(String bpmnXmlString)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        assertTaskElement("node1", "EXPONENTIAL", "seconds", "34.34", "NaN", "NaN",
            bpmnXmlString, 1);
        assertTaskElement("node2", "EXPONENTIAL", "seconds", "56.56", "NaN", "NaN",
            bpmnXmlString, 2);
        assertTaskElement("node3", "EXPONENTIAL", "seconds", "89.89", "NaN", "NaN",
            bpmnXmlString, 3);

    }

    private void assertTaskElement(final String elementId, final String distributionType, final String timeUnit,
                                   final String arg1, final String arg2, final String mean,
                                   final String bpmnXmlString, int elementIndex)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        Node elementNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/elements/element[" + elementIndex + "]");

        assertEquals(elementId, elementNode.getAttributes().getNamedItem("elementId").getNodeValue());
        assertEquals(distributionType, elementNode.getFirstChild().getAttributes().getNamedItem("type").getNodeValue());
        assertEquals(arg1, elementNode.getFirstChild().getAttributes().getNamedItem("arg1").getNodeValue());
        assertEquals(arg2, elementNode.getFirstChild().getAttributes().getNamedItem("arg2").getNodeValue());
        assertEquals(mean, elementNode.getFirstChild().getAttributes().getNamedItem("mean").getNodeValue());
        assertEquals(timeUnit, elementNode.getFirstChild().getFirstChild().getFirstChild().getNodeValue());

    }

}