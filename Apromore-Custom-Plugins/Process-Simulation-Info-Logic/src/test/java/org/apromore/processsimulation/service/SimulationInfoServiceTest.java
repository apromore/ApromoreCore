/**
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
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not @see <a href="http://www.gnu.org/licenses/lgpl-3.0.html"></a>
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
import org.apromore.processsimulation.config.SimulationInfoConfig;
import org.apromore.processsimulation.dto.SimulationData;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Element;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_ID_KEY, "A_DEFAULT_TIMETABLE_ID");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_NAME_KEY, "Arrival Timetable");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_NAME_KEY, "Default Timeslot");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_FROM_TIME, "10:00:00.000+00:00");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_TO_TIME, "15:00:00.000+00:00");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_FROM_WEEKDAY_KEY, "SUNDAY");
        timeTableConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_TO_WEEKDAY_KEY, "SATURDAY");
        when(config.getDefaultTimetable()).thenReturn(timeTableConfigMap);

        Map<String, String> defaultResourceConfigMap = new HashMap<>();
        defaultResourceConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_ID_KEY, "A_DEFAULT_RESOURCE_ID");
        defaultResourceConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_NAME_KEY, "The default resource name");
        when(config.getDefaultResource()).thenReturn(defaultResourceConfigMap);
    }

    @Test
    void should_successfully_derive_general_simulation_info() {
        // given
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);
    }

    @Test
    void should_successfully_derive_task_simulation_info() {
        // given
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);

        when(mockSimulationData.getDiagramNodeIDs()).thenReturn(Arrays.asList("a", "b", "c"));
        when(mockSimulationData.getDiagramNodeDuration("a")).thenReturn(10.10);
        when(mockSimulationData.getDiagramNodeDuration("b")).thenReturn(11.11);
        when(mockSimulationData.getDiagramNodeDuration("c")).thenReturn(12.12);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);

        assertEquals(3, processSimulationInfo.getTasks().size());
        assertTrue(
            processSimulationInfo.getTasks().stream()
                .map(Element::getElementId)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList("a", "b", "c")));

        processSimulationInfo.getTasks().forEach(element -> {
            switch (element.getElementId()) {
                case "a":
                    assertEquals("10.10", element.getDistributionDuration().getArg1());
                    break;
                case "b":
                    assertEquals("11.11", element.getDistributionDuration().getArg1());
                    break;
                case "c":
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
    void should_successfully_derive_timetable_info() {
        // given
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);

        assertNotNull(processSimulationInfo.getTimetables());
        assertEquals(1, processSimulationInfo.getTimetables().size());
        assertEquals("Arrival Timetable", processSimulationInfo.getTimetables().get(0).getName());
        assertEquals("A_DEFAULT_TIMETABLE_ID", processSimulationInfo.getTimetables().get(0).getId());
        assertTrue(processSimulationInfo.getTimetables().get(0).isDefaultTimetable());

        assertNotNull(processSimulationInfo.getTimetables().get(0).getRules());
        assertEquals(1, processSimulationInfo.getTimetables().get(0).getRules().size());
        assertNotNull(processSimulationInfo.getTimetables().get(0).getRules().get(0).getId());
        assertEquals("Default Timeslot", processSimulationInfo.getTimetables().get(0).getRules().get(0).getName());
        assertEquals("10:00:00.000+00:00",
            processSimulationInfo.getTimetables().get(0).getRules().get(0).getFromTime());
        assertEquals("15:00:00.000+00:00", processSimulationInfo.getTimetables().get(0).getRules().get(0).getToTime());
        assertEquals(DayOfWeek.SUNDAY, processSimulationInfo.getTimetables().get(0).getRules().get(0).getFromWeekDay());
        assertEquals(DayOfWeek.SATURDAY, processSimulationInfo.getTimetables().get(0).getRules().get(0).getToWeekDay());
    }

    @Test
    void should_successfully_derive_resource_info() {
        // given
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);
        when(mockSimulationData.getResourceCount()).thenReturn(27L);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);

        assertNotNull(processSimulationInfo.getResources());
        assertEquals(1, processSimulationInfo.getResources().size());
        assertEquals("A_DEFAULT_RESOURCE_ID", processSimulationInfo.getResources().get(0).getId());
        assertEquals("The default resource name", processSimulationInfo.getResources().get(0).getName());
        assertEquals("A_DEFAULT_TIMETABLE_ID", processSimulationInfo.getResources().get(0).getTimetableId());
        assertEquals(27, processSimulationInfo.getResources().get(0).getTotalAmount());
    }

    @Test
    void should_contain_no_resources_if_resource_count_is_zero() {
        // given
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);
        when(mockSimulationData.getResourceCount()).thenReturn(0L);


        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);

        assertNull(processSimulationInfo.getResources());
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
    void should_return_null_if_no_simulation_data() {
        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(null);

        // then
        assertNull(processSimulationInfo);
    }

    @Test
    void should_return_null_if_feature_disabled() {
        // given
        when(config.isEnable()).thenReturn(false);
        SimulationData mockSimulationData = mock(SimulationData.class);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertNull(processSimulationInfo);
    }

    @Test
    void should_enrich_with_general_simulation_info()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo =
            TestHelper.createMockProcessSimulationInfo(false, false, false, false);

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
        ProcessSimulationInfo processSimulationInfo =
            TestHelper.createMockProcessSimulationInfo(true, false, false, false);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnTaskProcessSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_timetable_simulation_info()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo =
            TestHelper.createMockProcessSimulationInfo(false, true, false, false);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnTimetableSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_resource_simulation_info()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo =
            TestHelper.createMockProcessSimulationInfo(false, false, true, false);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnResourceSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_gateway_probability_simulation_info()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo =
            TestHelper.createMockProcessSimulationInfo(false, false, false, true);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnGatewayProbabilitySimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_all_simulation_info()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo =
            TestHelper.createMockProcessSimulationInfo(true, true, true, true);

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, processSimulationInfo);

        // then
        assertBpmnGeneralProcessSimulationInfo(enrichedBpmn);
        assertBpmnTaskProcessSimulationInfo(enrichedBpmn);
        assertBpmnTimetableSimulationInfo(enrichedBpmn);
        assertBpmnResourceSimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_all_simulation_data()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        SimulationData simulationData = TestHelper.createMockSimulationData();

        // when
        String enrichedBpmn = simulationInfoService.enrichWithSimulationInfo(bpmn, simulationData);

        // then
        assertBpmnGeneralProcessSimulationInfo(enrichedBpmn);
        assertBpmnTaskProcessSimulationInfo(enrichedBpmn);
        assertBpmnTimetableSimulationInfo(enrichedBpmn);
        assertBpmnResourceSimulationInfo(enrichedBpmn);
        assertBpmnGatewayProbabilitySimulationInfo(enrichedBpmn);
    }

    @Test
    void should_enrich_with_simulation_info_for_model_with_no_xmlns_prefix()
        throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        // given
        String bpmn = TestHelper.readBpmnFile("/no_simulation_info_without_namespace_prefix.bpmn");
        ProcessSimulationInfo processSimulationInfo =
            TestHelper.createMockProcessSimulationInfo(false, false, false, false);

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
        String enrichedBpmn =
            simulationInfoService.enrichWithSimulationInfo(originalBpmn, (ProcessSimulationInfo) null);

        // then
        assertEquals(originalBpmn, enrichedBpmn);
    }

    @Test
    void should_not_enrich_if_feature_disabled() throws IOException {
        // given
        when(config.isEnable()).thenReturn(false);
        String originalBpmn = TestHelper.readBpmnFile("/no_simulation_info.bpmn");
        ProcessSimulationInfo processSimulationInfo =
            TestHelper.createMockProcessSimulationInfo(true, true, true, true);

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
        assertNull(arrivalRateDistributionAttrMap.getNamedItem("arg2"));
        assertNull(arrivalRateDistributionAttrMap.getNamedItem("mean"));
        assertEquals(DistributionType.EXPONENTIAL.toString(),
            arrivalRateDistributionAttrMap.getNamedItem("type").getNodeValue());

        Node timeUnitXmlNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/arrivalRateDistribution/timeUnit");
        assertEquals("seconds", timeUnitXmlNode.getFirstChild().getNodeValue());

    }

    private void assertBpmnTaskProcessSimulationInfo(String bpmnXmlString)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        NodeList elementNodeList = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/elements").getChildNodes();

        Map<String, Node> elementsMap = new HashMap<>();
        for (int i = 0; i < elementNodeList.getLength(); i++) {
            Node element = elementNodeList.item(i);
            elementsMap.put(element.getAttributes().getNamedItem("elementId").getNodeValue(), element);
        }

        assertTaskElement("node1", "EXPONENTIAL", "seconds", "34.34", elementsMap);
        assertTaskElement("node2", "EXPONENTIAL", "seconds", "56.56", elementsMap);
        assertTaskElement("node3", "EXPONENTIAL", "seconds", "89.89", elementsMap);

    }

    private void assertTaskElement(final String elementId, final String distributionType, final String timeUnit,
                                   final String arg1,
                                   final Map<String, Node> elementsMap) {

        Node elementNode = elementsMap.get(elementId);

        assertEquals(elementId, elementNode.getAttributes().getNamedItem("elementId").getNodeValue());
        assertEquals(distributionType, elementNode.getFirstChild().getAttributes().getNamedItem("type").getNodeValue());
        assertEquals(arg1, elementNode.getFirstChild().getAttributes().getNamedItem("arg1").getNodeValue());
        assertNull(elementNode.getFirstChild().getAttributes().getNamedItem("arg2"));
        assertNull(elementNode.getFirstChild().getAttributes().getNamedItem("mean"));
        assertEquals(timeUnit, elementNode.getFirstChild().getFirstChild().getFirstChild().getNodeValue());

    }

    private void assertBpmnTimetableSimulationInfo(String bpmnXmlString)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        Node timeTableNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/timetables/timetable[1]");
        assertEquals("A_DEFAULT_TIMETABLE_ID", timeTableNode.getAttributes().getNamedItem("id").getNodeValue());
        assertEquals("Arrival Timetable", timeTableNode.getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("true", timeTableNode.getAttributes().getNamedItem("default").getNodeValue());

        Node timeTableRuleNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/timetables/timetable[1]/rules/rule[1]");
        assertNotNull(timeTableRuleNode.getAttributes().getNamedItem("id").getNodeValue());
        assertEquals("Default Timeslot", timeTableRuleNode.getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("SUNDAY", timeTableRuleNode.getAttributes().getNamedItem("fromWeekDay").getNodeValue());
        assertEquals("SATURDAY", timeTableRuleNode.getAttributes().getNamedItem("toWeekDay").getNodeValue());
        assertEquals("10:00:00.000+00:00",
            timeTableRuleNode.getAttributes().getNamedItem("fromTime").getNodeValue());
        assertEquals("15:00:00.000+00:00", timeTableRuleNode.getAttributes().getNamedItem("toTime").getNodeValue());

    }

    private void assertBpmnResourceSimulationInfo(String bpmnXmlString)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        Node resourceNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/resources/resource[1]");
        assertEquals("A_DEFAULT_RESOURCE_ID", resourceNode.getAttributes().getNamedItem("id").getNodeValue());
        assertEquals("The default resource name", resourceNode.getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("A_DEFAULT_TIMETABLE_ID", resourceNode.getAttributes().getNamedItem("timetableId").getNodeValue());
        assertEquals("23", resourceNode.getAttributes().getNamedItem("totalAmount").getNodeValue());
    }

    private void assertBpmnGatewayProbabilitySimulationInfo(String bpmnXmlString)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        NodeList seqFlowNodeList = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/sequenceFlows").getChildNodes();

        Map<String, Node> sequenceFlowMap = new HashMap<>();
        for (int i = 0; i < seqFlowNodeList.getLength(); i++) {
            Node seqFlow = seqFlowNodeList.item(i);
            sequenceFlowMap.put(seqFlow.getAttributes().getNamedItem("elementId").getNodeValue(), seqFlow);
        }

        assertSequenceFlow("edge2", .2025, sequenceFlowMap);
        assertSequenceFlow("edge3", .3016, sequenceFlowMap);
        assertSequenceFlow("edge4", .4959, sequenceFlowMap);

    }

    private void assertSequenceFlow(final String elementId, double executionProbability,
                                    Map<String, Node> sequenceFlowMap) {

        Node seqFlowNode = sequenceFlowMap.get(elementId);

        assertEquals(elementId, seqFlowNode.getAttributes().getNamedItem("elementId").getNodeValue());
        assertEquals(Double.toString(executionProbability),
            seqFlowNode.getAttributes().getNamedItem("executionProbability").getNodeValue());

    }
}