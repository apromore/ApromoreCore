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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.apromore.processsimulation.config.SimulationInfoConfig;
import org.apromore.processsimulation.dto.SimulationData;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Element;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.Resource;
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
        defaultResourceConfigMap.put(SimulationInfoConfig.CONFIG_DEFAULT_ID_PREFIX_KEY, "QBP_");
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
    void should_successfully_derive_task_simulation_info_with_default_resource() {
        // given
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);

        when(mockSimulationData.getDiagramNodeIDs()).thenReturn(Arrays.asList("a", "b", "c"));
        when(mockSimulationData.getDiagramNodeDuration("a")).thenReturn(10100.00);
        when(mockSimulationData.getDiagramNodeDuration("b")).thenReturn(11110.00);
        when(mockSimulationData.getDiagramNodeDuration("c")).thenReturn(12120.00);

        when(mockSimulationData.getRoleNameByNodeId(anyString())).thenReturn("The default resource name");

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

            assertEquals("A_DEFAULT_RESOURCE_ID", element.getResourceIds().get(0));
            assertNull(element.getDistributionDuration().getArg2());
            assertNull(element.getDistributionDuration().getMean());
            assertEquals(TimeUnit.SECONDS, element.getDistributionDuration().getTimeUnit());
            assertEquals(DistributionType.EXPONENTIAL, element.getDistributionDuration().getType());
        });
    }

    @Test
    void should_successfully_derive_task_simulation_info_with_associated_resource() {
        // given
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);

        when(mockSimulationData.getDiagramNodeIDs()).thenReturn(Arrays.asList("a", "b", "c"));
        when(mockSimulationData.getDiagramNodeDuration("a")).thenReturn(10100.00);
        when(mockSimulationData.getDiagramNodeDuration("b")).thenReturn(11110.00);
        when(mockSimulationData.getDiagramNodeDuration("c")).thenReturn(12120.00);

        when(mockSimulationData.getResourceCountsByRole()).thenReturn(Map.of("Role_1", 5, "Role_2", 10, "Role_3", 15));
        when(mockSimulationData.getRoleNameByNodeId("a")).thenReturn("Role_1");
        when(mockSimulationData.getRoleNameByNodeId("b")).thenReturn("Role_2");
        when(mockSimulationData.getRoleNameByNodeId("c")).thenReturn("Role_3");

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
            assertNotEquals("A_DEFAULT_RESOURCE_ID", element.getResourceIds().get(0));
            assertTrue(element.getResourceIds().get(0)
                .matches("QBP_[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
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

        Map<String, Integer> mockRoleToResourceCounts = Map.of(
            "Role_1", 5,
            "Role_2", 10,
            "Role_3", 15
        );
        when(mockSimulationData.getResourceCountsByRole()).thenReturn(mockRoleToResourceCounts);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);

        assertNotNull(processSimulationInfo.getResources());
        assertEquals(3, processSimulationInfo.getResources().size());

        Optional<Resource> role1 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_1"))
                .findFirst();
        assertResource("Role_1", 5, "A_DEFAULT_TIMETABLE_ID", role1);

        Optional<Resource> role2 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_2"))
                .findFirst();
        assertResource("Role_2", 10, "A_DEFAULT_TIMETABLE_ID", role2);

        Optional<Resource> role3 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_3"))
                .findFirst();
        assertResource("Role_3", 15, "A_DEFAULT_TIMETABLE_ID", role3);
    }

    private void assertResource(
        final String expectedResourceName,
        final int expectedResourceCount,
        final String expectedTimetableId,
        final Optional<Resource> actualRole) {
        assertTrue(actualRole.isPresent());
        assertEquals(expectedResourceName, actualRole.get().getName());
        assertEquals(expectedResourceCount, actualRole.get().getTotalAmount());
        assertEquals(expectedTimetableId, actualRole.get().getTimetableId());
    }

    @Test
    void should_contain_default_resource_if_no_roles() {
        // given
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);
        when(mockSimulationData.getResourceCount()).thenReturn(19L);
        when(mockSimulationData.getResourceCountsByRole()).thenReturn(null);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(processSimulationInfo);

        assertEquals(1, processSimulationInfo.getResources().size());
        assertEquals("A_DEFAULT_RESOURCE_ID", processSimulationInfo.getResources().get(0).getId());
        assertEquals("The default resource name", processSimulationInfo.getResources().get(0).getName());
        assertEquals("A_DEFAULT_TIMETABLE_ID", processSimulationInfo.getResources().get(0).getTimetableId());
        assertEquals(19, processSimulationInfo.getResources().get(0).getTotalAmount());
    }

    private void assertGeneralSimulationInfo(final ProcessSimulationInfo processSimulationInfo) {
        assertNotNull(processSimulationInfo.getId());
        assertNotNull(processSimulationInfo.getErrors());
        assertEquals(100L, processSimulationInfo.getProcessInstances());
        assertEquals("26784.00", processSimulationInfo.getArrivalRateDistribution().getArg1());
        assertNull(processSimulationInfo.getArrivalRateDistribution().getArg2());
        assertNull(processSimulationInfo.getArrivalRateDistribution().getMean());
        assertEquals(TimeUnit.HOURS, processSimulationInfo.getArrivalRateDistribution().getTimeUnit());
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

    @Test
    void should_convert_display_time_unit_based_on_time_duration() {

        long startTime = 1643839200000L; // Thursday, 3 February 2022 09:00:00 GMT+11:00 DST

        /* *********************************************
         * duration < 1 second
         * Display Unit -> seconds
         * Display duration -> value in seconds
         * *********************************************
         */
        long endTime = 1643839200001L; // Thursday, 3 February 2022 09:00:00.001 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.SECONDS);

        /* *********************************************
         * duration < 60 seconds
         * Display Unit -> seconds
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1643839259000L; // Thursday, 3 February 2022 09:00:59 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.SECONDS);


        /* *********************************************
         * duration == 60 seconds (1 min)
         * Display Unit -> seconds
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1643839260000L; // Thursday, 3 February 2022 09:01:00 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.SECONDS);

        /* *********************************************
         * duration > 1 min
         * Display Unit -> minutes
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1643839290000L; // Thursday, 3 February 2022 09:01:30 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.MINUTES);

        /* *********************************************
         * duration == 1 hour
         * Display Unit -> minutes
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1643842800000L; // Thursday, 3 February 2022 10:00:00 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.MINUTES);

        /* *********************************************
         * duration > 1 hour
         * Display Unit -> hours
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1643843400000L; // Thursday, 3 February 2022 10:10:00 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.HOURS);

        /* *********************************************
         * duration == 1 day
         * Display Unit -> hours
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1643925600000L; // Friday, 4 February 2022 09:00:00 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.HOURS);

        /* *********************************************
         * duration > 1 day
         * Display Unit -> hours
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1644012000000L; // Saturday, 5 February 2022 09:00:00 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.HOURS);

        /* *********************************************
         * duration == 1 month
         * Display Unit -> hours
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1646258400000L; // Thursday, 3 March 2022 09:00:00 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.HOURS);

        /* *********************************************
         * duration > 1 month
         * Display Unit -> hours
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1648940400000L; // Sunday, 3 April 2022 09:00:00 GMT+10:00
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.HOURS);

        /* *********************************************
         * duration == 1 year
         * Display Unit -> hours
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1675375200000L; // Friday, 3 February 2023 09:00:00 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.HOURS);

        /* *********************************************
         * duration > 1 year
         * Display Unit -> hours
         * Display duration -> value in seconds
         * *********************************************
         */
        endTime = 1738533600000L; // Monday, 3 February 2025 09:00:00 GMT+11:00 DST
        testTimeUnitsAndDurations(startTime, endTime, TimeUnit.HOURS);
    }

    private void testTimeUnitsAndDurations(long startTime, long endTime, TimeUnit expectedTimeUnit) {
        // given
        long caseCount = 1;
        SimulationData simulationData = SimulationData.builder()
            .startTime(startTime)
            .endTime(endTime)
            .caseCount(caseCount)
            .build();

        // when
        ProcessSimulationInfo simulationInfo = simulationInfoService.transformToSimulationInfo(simulationData);

        // then
        assertEquals(expectedTimeUnit, simulationInfo.getArrivalRateDistribution().getTimeUnit());
        assertEquals(getInterArrivalTime(startTime, endTime, caseCount),
            simulationInfo.getArrivalRateDistribution().getArg1());
    }

    private String getInterArrivalTime(long startTime, long endTime, long caseCount) {
        double interArrivalTimeMillis = (endTime - startTime) / (double) caseCount;

        return BigDecimal.valueOf(interArrivalTimeMillis / (double) TimeUnit.SECONDS.getNumberOfMilliseconds())
            .setScale(2, RoundingMode.HALF_UP).toString();
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
        assertEquals("26784.00", arrivalRateDistributionAttrMap.getNamedItem("arg1").getNodeValue());
        assertNull(arrivalRateDistributionAttrMap.getNamedItem("arg2"));
        assertNull(arrivalRateDistributionAttrMap.getNamedItem("mean"));
        assertEquals(DistributionType.EXPONENTIAL.toString(),
            arrivalRateDistributionAttrMap.getNamedItem("type").getNodeValue());

        Node timeUnitXmlNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/arrivalRateDistribution/timeUnit");
        assertEquals("hours", timeUnitXmlNode.getFirstChild().getNodeValue());

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
        assertTaskElement("node3", "EXPONENTIAL", "minutes", "89.89", elementsMap);

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

        NodeList resourceNodeList = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/resources").getChildNodes();

        Map<String, Node> resourceNodeMap = new HashMap<>();
        for (int i = 0; i < resourceNodeList.getLength(); i++) {
            Node resource = resourceNodeList.item(i);
            resourceNodeMap.put(resource.getAttributes().getNamedItem("name").getNodeValue(), resource);
        }

        assertResources("Role_1", 5, "A_DEFAULT_TIMETABLE_ID", resourceNodeMap);
        assertResources("Role_2", 10, "A_DEFAULT_TIMETABLE_ID", resourceNodeMap);
        assertResources("Role_3", 15, "A_DEFAULT_TIMETABLE_ID", resourceNodeMap);
    }

    private void assertResources(String resourceName, int totalAmount, String timetableId,
                                 final Map<String, Node> resourceNodeMap) {
        Node resourceNode = resourceNodeMap.get(resourceName);

        assertEquals(resourceName, resourceNode.getAttributes().getNamedItem("name").getNodeValue());
        assertEquals(String.valueOf(totalAmount),
            resourceNode.getAttributes().getNamedItem("totalAmount").getNodeValue());
        assertEquals(timetableId, resourceNode.getAttributes().getNamedItem("timetableId").getNodeValue());

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