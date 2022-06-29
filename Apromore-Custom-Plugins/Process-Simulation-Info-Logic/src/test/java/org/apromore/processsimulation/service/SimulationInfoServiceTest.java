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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CustomCalendarService;
import org.apromore.dao.model.Usermetadata;
import org.apromore.processsimulation.config.SimulationInfoConfig;
import org.apromore.processsimulation.dto.EdgeFrequency;
import org.apromore.processsimulation.dto.SimulationData;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Element;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.Resource;
import org.apromore.processsimulation.model.TimeUnit;
import org.apromore.processsimulation.model.Timetable;
import org.apromore.service.UserMetadataService;
import org.apromore.util.UserMetadataTypeEnum;
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

    @Mock
    private CustomCalendarService calendarService;

    @Mock
    private UserMetadataService userMetadataService;

    @BeforeEach
    void setup() throws IOException {
        MockitoAnnotations.openMocks(this);

        simulationInfoService = new SimulationInfoService(config, calendarService,userMetadataService);

        when(config.isEnable()).thenReturn(true);
        when(config.getDefaultCurrency()).thenReturn("EUR");
        when(config.getDefaultDistributionType()).thenReturn("EXPONENTIAL");
        when(config.getDefaultTimeUnit()).thenReturn("SECONDS");

        when(config.getDefaultTimetableId()).thenReturn("A_DEFAULT_TIMETABLE_ID");
        when(config.getCustomTimetableId()).thenReturn("A_CUSTOM_TIMETABLE_ID");
        when(config.getDefaultTimeslotName()).thenReturn("Default Timeslot");

        when(config.getDefaultResourceId()).thenReturn("A_DEFAULT_RESOURCE_ID");
        when(config.getDefaultResourceIdPrefix()).thenReturn("QBP_");
        when(config.getDefaultResourceName()).thenReturn("The default resource name");
        when(config.getDefaultMaxProcessInstances()).thenReturn(25000L);

        when(userMetadataService.getUserMetadataByLog(anyInt(), eq(UserMetadataTypeEnum.COST_TABLE))).thenReturn(
            Collections.emptySet());

        CalendarModel mockCalendarModel = new CalendarModelBuilder().withAllDayAllTime().build();
        mockCalendarModel.setName(SimulationData.DEFAULT_CALENDAR_NAME);

        when(calendarService.getGenericCalendar()).thenReturn(mockCalendarModel);
    }

    @Test
    void should_successfully_derive_general_simulation_info() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(26784.00, processSimulationInfo);
    }

    @Test
    void should_successfully_derive_task_simulation_info_with_default_resource() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();

        when(mockSimulationData.getDiagramNodeIDs()).thenReturn(Arrays.asList("a", "b", "c"));
        when(mockSimulationData.getDiagramNodeDuration("a")).thenReturn(10100.00);
        when(mockSimulationData.getDiagramNodeDuration("b")).thenReturn(11110.00);
        when(mockSimulationData.getDiagramNodeDuration("c")).thenReturn(12120.00);

        when(mockSimulationData.getResourceCountsByRole()).thenReturn(null);
        when(mockSimulationData.getRoleNameByNodeId(anyString())).thenReturn("The default resource name");

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(26784.00, processSimulationInfo);

        assertTaskInfo(processSimulationInfo, true);
    }

    @Test
    void should_successfully_derive_task_simulation_info_with_default_role() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();

        when(mockSimulationData.getDiagramNodeIDs()).thenReturn(Arrays.asList("a", "b", "c"));
        when(mockSimulationData.getDiagramNodeDuration("a")).thenReturn(10100.00);
        when(mockSimulationData.getDiagramNodeDuration("b")).thenReturn(11110.00);
        when(mockSimulationData.getDiagramNodeDuration("c")).thenReturn(12120.00);

        when(mockSimulationData.getResourceCountsByRole()).thenReturn(Map.of(SimulationData.DEFAULT_ROLE, 30));
        when(mockSimulationData.getRoleNameByNodeId(anyString())).thenReturn(SimulationData.DEFAULT_ROLE);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(26784.00, processSimulationInfo);

        assertTaskInfo(processSimulationInfo, true);
    }

    @Test
    void should_successfully_derive_task_simulation_info_with_associated_resource() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();

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
        assertGeneralSimulationInfo(26784.00, processSimulationInfo);

        assertTaskInfo(processSimulationInfo, false);
    }

    private void assertTaskInfo(final ProcessSimulationInfo processSimulationInfo, boolean expectDefaultResource) {
        assertEquals(3, processSimulationInfo.getTasks().size());
        assertTrue(
            processSimulationInfo.getTasks().stream()
                .map(Element::getElementId)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList("a", "b", "c")));

        processSimulationInfo.getTasks().forEach(element -> {
            switch (element.getElementId()) {
                case "a":
                    assertEquals(10.10, element.getDistributionDuration().getArg1());
                    break;
                case "b":
                    assertEquals(11.11, element.getDistributionDuration().getArg1());
                    break;
                case "c":
                    assertEquals(12.12, element.getDistributionDuration().getArg1());
                    break;
                default:
                    fail("Unrecognised task identifier");
                    break;
            }

            if (expectDefaultResource) {
                assertEquals("QBP_A_DEFAULT_RESOURCE_ID", element.getResourceIds().get(0));
            } else {
                assertFalse(element.getResourceIds().get(0).contains("A_DEFAULT_RESOURCE_ID"));
                assertTrue(element.getResourceIds().get(0)
                    .matches("QBP_[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
            }
            assertEquals(0.0, element.getDistributionDuration().getArg2());
            assertEquals(0.0, element.getDistributionDuration().getMean());
            assertEquals(TimeUnit.SECONDS, element.getDistributionDuration().getTimeUnit());
            assertEquals(DistributionType.EXPONENTIAL, element.getDistributionDuration().getType());
        });
    }

    @Test
    void should_successfully_derive_timetable_info_with_generic_24_7_calendar() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(26784.00, processSimulationInfo);

        assertNotNull(processSimulationInfo.getTimetables());
        assertEquals(1, processSimulationInfo.getTimetables().size());
        assertEquals("24/7", processSimulationInfo.getTimetables().get(0).getName());
        assertEquals("A_CUSTOM_TIMETABLE_ID", processSimulationInfo.getTimetables().get(0).getId());
        assertTrue(processSimulationInfo.getTimetables().get(0).isDefaultTimetable());
        assertNotNull(processSimulationInfo.getTimetables().get(0).getRules());
        assertEquals(1, processSimulationInfo.getTimetables().get(0).getRules().size());
        assertNotNull(processSimulationInfo.getTimetables().get(0).getRules().get(0).getId());
        assertEquals("Default Timeslot", processSimulationInfo.getTimetables().get(0).getRules().get(0).getName());
        assertEquals("00:00:00.000",
            processSimulationInfo.getTimetables().get(0).getRules().get(0).getFromTime());
        assertEquals("23:59:59.999", processSimulationInfo.getTimetables().get(0).getRules().get(0).getToTime());
        assertEquals(DayOfWeek.MONDAY, processSimulationInfo.getTimetables().get(0).getRules().get(0).getFromWeekDay());
        assertEquals(DayOfWeek.SUNDAY, processSimulationInfo.getTimetables().get(0).getRules().get(0).getToWeekDay());
    }

    @Test
    void should_successfully_derive_timetable_info_with_custom_calendar() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();

        CalendarModel mockCustomCalendarModel = new CalendarModelBuilder().with5DayWorking().build();
        mockCustomCalendarModel.setName("Mock Business Calendar");
        when(mockSimulationData.getCalendarModel()).thenReturn(mockCustomCalendarModel);

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
        assertGeneralSimulationInfo(6624.00, processSimulationInfo);

        assertNotNull(processSimulationInfo.getTimetables());
        assertEquals(2, processSimulationInfo.getTimetables().size());

        assertTimetableInfo(processSimulationInfo.getTimetables().get(0),
            "Log timetable", "A_CUSTOM_TIMETABLE_ID", true,
            "09:00:00.000", "17:00:00.000", DayOfWeek.MONDAY, DayOfWeek.FRIDAY);

        assertTimetableInfo(processSimulationInfo.getTimetables().get(1),
            "24/7", "A_DEFAULT_TIMETABLE_ID", false,
            "00:00:00.000", "23:59:59.999", DayOfWeek.MONDAY, DayOfWeek.SUNDAY);

        assertNotNull(processSimulationInfo.getResources());
        assertEquals(3, processSimulationInfo.getResources().size());

        Optional<Resource> role1 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_1"))
                .findFirst();
        assertResource("Role_1", 5, "A_CUSTOM_TIMETABLE_ID", role1);

        Optional<Resource> role2 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_2"))
                .findFirst();
        assertResource("Role_2", 10, "A_CUSTOM_TIMETABLE_ID", role2);

        Optional<Resource> role3 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_3"))
                .findFirst();
        assertResource("Role_3", 15, "A_CUSTOM_TIMETABLE_ID", role3);
    }

    private void assertTimetableInfo(
        final Timetable actualTimetable,
        final String expectedName,
        final String expectedId,
        boolean isDefaultExpected,
        final String expectedFromTime,
        final String expectedToTime,
        final DayOfWeek expectedFromDay,
        final DayOfWeek expectedToDay) {

        assertEquals(expectedName, actualTimetable.getName());
        assertEquals(expectedId, actualTimetable.getId());
        assertEquals(isDefaultExpected, actualTimetable.isDefaultTimetable());
        assertNotNull(actualTimetable.getRules());
        assertEquals(1, actualTimetable.getRules().size());
        assertNotNull(actualTimetable.getRules().get(0).getId());
        assertEquals("Default Timeslot", actualTimetable.getRules().get(0).getName());
        assertEquals(expectedFromTime, actualTimetable.getRules().get(0).getFromTime());
        assertEquals(expectedToTime, actualTimetable.getRules().get(0).getToTime());
        assertEquals(expectedFromDay, actualTimetable.getRules().get(0).getFromWeekDay());
        assertEquals(expectedToDay, actualTimetable.getRules().get(0).getToWeekDay());

    }

    @Test
    void should_successfully_derive_resource_with_no_cost_info() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();
        when(mockSimulationData.getResourceCount()).thenReturn(27L);

        Map<String, Integer> mockRoleToResourceCounts = Map.of(
            "Role_1", 5,
            "Role_2", 10,
            "Role_3", 15
        );
        when(mockSimulationData.getResourceCountsByRole()).thenReturn(mockRoleToResourceCounts);
        when(userMetadataService.getUserMetadataByLog(anyInt(), eq(UserMetadataTypeEnum.COST_TABLE))).thenReturn(
            Collections.emptySet());

        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);
        assertNotNull(processSimulationInfo.getResources());

        Optional<Resource> role1 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_1"))
                .findFirst();
        assertResourceCostPerhour("Role_1", 0, role1);

    }

    @Test
    void should_successfully_derive_resource_info_with_costing_but_not_parsable() throws JsonProcessingException {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();
        when(mockSimulationData.getResourceCount()).thenReturn(27L);

        Map<String, Integer> mockRoleToResourceCounts = Map.of(
            "Role_1", 5,
            "Role_2", 10,
            "Role_3", 15
        );
        when(mockSimulationData.getResourceCountsByRole()).thenReturn(mockRoleToResourceCounts);

        Usermetadata usermetadata = new Usermetadata();
        usermetadata.setContent("dummy");
        when(userMetadataService.getUserMetadataByLog(anyInt(), eq(UserMetadataTypeEnum.COST_TABLE))).thenReturn(
            Set.of(usermetadata));

        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);
        assertNotNull(processSimulationInfo.getResources());

        Optional<Resource> role1 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_1"))
                .findFirst();
        assertResourceCostPerhour("Role_1", 0, role1);
    }

    @Test
    void should_successfully_derive_resource_info_with_costing_but_no_cost_content() throws JsonProcessingException {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();
        when(mockSimulationData.getResourceCount()).thenReturn(27L);

        Map<String, Integer> mockRoleToResourceCounts = Map.of(
            "Role_1", 5,
            "Role_2", 10,
            "Role_3", 15
        );
        when(mockSimulationData.getResourceCountsByRole()).thenReturn(mockRoleToResourceCounts);

        Usermetadata usermetadata = new Usermetadata();
        usermetadata.setContent("[{\"perspective\":\"role\",\"currency\":\"AUD\"}]");
        when(userMetadataService.getUserMetadataByLog(anyInt(), eq(UserMetadataTypeEnum.COST_TABLE))).thenReturn(
            Set.of(usermetadata));

        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);
        assertNotNull(processSimulationInfo.getResources());

        Optional<Resource> role1 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_1"))
                .findFirst();
        assertResourceCostPerhour("Role_1", 0, role1);
    }

    @Test
    void should_successfully_derive_resource_info_with_costing_but_null_data() throws JsonProcessingException {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();
        when(mockSimulationData.getResourceCount()).thenReturn(27L);

        Map<String, Integer> mockRoleToResourceCounts = Map.of(
            "Role_1", 5,
            "Role_2", 10,
            "Role_3", 15
        );
        when(mockSimulationData.getResourceCountsByRole()).thenReturn(mockRoleToResourceCounts);

        Usermetadata usermetadata = new Usermetadata();
        usermetadata.setContent(null);
        when(userMetadataService.getUserMetadataByLog(anyInt(), eq(UserMetadataTypeEnum.COST_TABLE))).thenReturn(
            Set.of(usermetadata));

        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);
        assertNotNull(processSimulationInfo.getResources());

        Optional<Resource> role1 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_1"))
                .findFirst();
        assertResourceCostPerhour("Role_1", 0, role1);
    }

    @Test
    void should_successfully_derive_resource_info() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();
        when(mockSimulationData.getResourceCount()).thenReturn(27L);

        Map<String, Integer> mockRoleToResourceCounts = Map.of(
            "Role_1", 5,
            "Role_2", 10,
            "Role_3", 15
        );
        when(mockSimulationData.getResourceCountsByRole()).thenReturn(mockRoleToResourceCounts);

        Usermetadata usermetadata = new Usermetadata();
        usermetadata.setContent(
            "[{\"perspective\":\"role\",\"currency\":\"AUD\""
                + ",\"costRates\":{\"Role_1\":10.0,\"Role_2\":20.0,\"Role_3\":30.0}}]");
        when(userMetadataService.getUserMetadataByLog(anyInt(), eq(UserMetadataTypeEnum.COST_TABLE))).thenReturn(
            Set.of(usermetadata));

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(26784.00, processSimulationInfo);

        assertNotNull(processSimulationInfo.getResources());
        assertEquals(3, processSimulationInfo.getResources().size());

        Optional<Resource> role1 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_1"))
                .findFirst();
        assertResource("Role_1", 5, "A_CUSTOM_TIMETABLE_ID", role1);
        assertResourceCostPerhour("Role_1", 10, role1);

        Optional<Resource> role2 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_2"))
                .findFirst();
        assertResource("Role_2", 10, "A_CUSTOM_TIMETABLE_ID", role2);
        assertResourceCostPerhour("Role_2", 20, role2);

        Optional<Resource> role3 =
            processSimulationInfo.getResources().stream().filter(resource -> resource.getName().equals("Role_3"))
                .findFirst();
        assertResource("Role_3", 15, "A_CUSTOM_TIMETABLE_ID", role3);
        assertResourceCostPerhour("Role_3", 30, role3);
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

    private void assertResourceCostPerhour(
        final String expectedResourceName,
        final double expectedResourceCost,
        final Optional<Resource> actualRole) {
        assertTrue(actualRole.isPresent());
        assertEquals(expectedResourceName, actualRole.get().getName());
        assertEquals(expectedResourceCost, actualRole.get().getCostPerHour());
    }

    @Test
    void should_contain_default_resource_if_no_roles() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();
        when(mockSimulationData.getResourceCount()).thenReturn(19L);
        when(mockSimulationData.getResourceCountsByRole()).thenReturn(null);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(26784.00, processSimulationInfo);

        assertEquals(1, processSimulationInfo.getResources().size());
        assertEquals("QBP_A_DEFAULT_RESOURCE_ID", processSimulationInfo.getResources().get(0).getId());
        assertEquals("The default resource name", processSimulationInfo.getResources().get(0).getName());
        assertEquals("A_CUSTOM_TIMETABLE_ID", processSimulationInfo.getResources().get(0).getTimetableId());
        assertEquals(19, processSimulationInfo.getResources().get(0).getTotalAmount());
    }

    @Test
    void should_successfully_derive_gateway_probabilities() {
        // given
        SimulationData mockSimulationData = mockBasicSimulationData();

        Map<String, List<EdgeFrequency>> mockEdgeFrequencies = Map.of(
            "g1", List.of(
                EdgeFrequency.builder().edgeId("e1").frequency(125).build(),
                EdgeFrequency.builder().edgeId("e2").frequency(12).build(),
                EdgeFrequency.builder().edgeId("e3").frequency(34).build(),
                EdgeFrequency.builder().edgeId("e4").frequency(456).build(),
                EdgeFrequency.builder().edgeId("e5").frequency(6).build()),

            "g2", List.of(
                EdgeFrequency.builder().edgeId("e6").frequency(56).build(),
                EdgeFrequency.builder().edgeId("e7").frequency(987).build(),
                EdgeFrequency.builder().edgeId("e8").frequency(1).build()),

            "g3", List.of(
                EdgeFrequency.builder().edgeId("e9").frequency(56).build(),
                EdgeFrequency.builder().edgeId("e10").frequency(987).build(),
                EdgeFrequency.builder().edgeId("e11").frequency(65).build(),
                EdgeFrequency.builder().edgeId("e12").frequency(654).build(),
                EdgeFrequency.builder().edgeId("e13").frequency(258).build(),
                EdgeFrequency.builder().edgeId("e14").frequency(54).build(),
                EdgeFrequency.builder().edgeId("e15").frequency(78).build(),
                EdgeFrequency.builder().edgeId("e16").frequency(3).build(),
                EdgeFrequency.builder().edgeId("e17").frequency(81).build(),
                EdgeFrequency.builder().edgeId("e18").frequency(1).build()),

            "g4", List.of(
                EdgeFrequency.builder().edgeId("e20").frequency(514).build(),
                EdgeFrequency.builder().edgeId("e21").frequency(411).build(),
                EdgeFrequency.builder().edgeId("e22").frequency(49).build())
        );
        when(mockSimulationData.getEdgeFrequencies()).thenReturn(mockEdgeFrequencies);

        // when
        ProcessSimulationInfo processSimulationInfo =
            simulationInfoService.transformToSimulationInfo(mockSimulationData);

        // then
        assertGeneralSimulationInfo(26784.00, processSimulationInfo);

        assertGatewayProbabilitiesAddTo100Percent(processSimulationInfo, List.of("e1", "e2", "e3", "e4", "e5"));
        assertGatewayProbabilitiesAddTo100Percent(processSimulationInfo, List.of("e6", "e7", "e8"));
        assertGatewayProbabilitiesAddTo100Percent(processSimulationInfo, List.of("e9", "e10", "e11", "e12", "e13",
            "e14", "e15", "e16", "e17", "e18"));
        assertGatewayProbabilitiesAddTo100Percent(processSimulationInfo, List.of("e20", "e21", "e22"));
    }

    private void assertGatewayProbabilitiesAddTo100Percent(
        final ProcessSimulationInfo processSimulationInfo,
        final List<String> gatewayEdgeIds) {

        assertEquals(1.0,
            processSimulationInfo.getSequenceFlows().stream()
                .filter(sequenceFlow -> gatewayEdgeIds.contains(sequenceFlow.getElementId()))
                .map(sequenceFlow -> Double.valueOf(sequenceFlow.getExecutionProbability()))
                .reduce(0.0D, Double::sum));
    }

    private void assertGeneralSimulationInfo(Double expectedArrivalRate,
                                             final ProcessSimulationInfo actualProcessSimulationInfo) {
        assertNotNull(actualProcessSimulationInfo.getId());
        assertNotNull(actualProcessSimulationInfo.getErrors());
        assertEquals(100L, actualProcessSimulationInfo.getProcessInstances());
        assertEquals(expectedArrivalRate, actualProcessSimulationInfo.getArrivalRateDistribution().getArg1());
        assertEquals(0.0, actualProcessSimulationInfo.getArrivalRateDistribution().getArg2());
        assertEquals(0.0, actualProcessSimulationInfo.getArrivalRateDistribution().getMean());
        assertEquals(TimeUnit.HOURS, actualProcessSimulationInfo.getArrivalRateDistribution().getTimeUnit());
        assertEquals(DistributionType.EXPONENTIAL, actualProcessSimulationInfo.getArrivalRateDistribution().getType());
        assertEquals(Instant.ofEpochMilli(1577797200000L).toString(),
            actualProcessSimulationInfo.getStartDateTime());
        assertEquals(Currency.EUR, actualProcessSimulationInfo.getCurrency());
    }

    private SimulationData mockBasicSimulationData() {
        SimulationData mockSimulationData = mock(SimulationData.class);

        when(mockSimulationData.getCaseCount()).thenReturn(100L);
        when(mockSimulationData.getStartTime()).thenReturn(1577797200000L);
        when(mockSimulationData.getEndTime()).thenReturn(1580475600000L);


        CalendarModel mockCalendarModel = new CalendarModelBuilder().withAllDayAllTime().build();
        mockCalendarModel.setName(SimulationData.DEFAULT_CALENDAR_NAME);
        when(mockSimulationData.getCalendarModel()).thenReturn(mockCalendarModel);

        return mockSimulationData;
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
        CalendarModel calendarModel = new CalendarModelBuilder().with5DayWorking().build();
        calendarModel.setZoneId(ZoneId.SHORT_IDS.get("AET"));
        SimulationData simulationData = SimulationData.builder()
            .startTime(startTime)
            .endTime(endTime)
            .calendarModel(calendarModel)
            .caseCount(caseCount)
            .build();

        // when
        ProcessSimulationInfo simulationInfo = simulationInfoService.transformToSimulationInfo(simulationData);

        // then
        assertEquals(expectedTimeUnit, simulationInfo.getArrivalRateDistribution().getTimeUnit());
        Double interArrivalTime = BigDecimal.valueOf(
                simulationInfoService.getInterArrivalTime(simulationData) / TimeUnit.SECONDS.getNumberOfMilliseconds())
            .setScale(2, RoundingMode.HALF_UP).doubleValue();

        assertEquals(interArrivalTime, simulationInfo.getArrivalRateDistribution().getArg1());
    }

    private void assertBpmnGeneralProcessSimulationInfo(String bpmnXmlString)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        Node processSimulationInfoXmlNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo");

        NamedNodeMap processSimulationAttrMap = processSimulationInfoXmlNode.getAttributes();
        assertNotNull(processSimulationAttrMap.getNamedItem("id").getNodeValue());
        assertEquals(Currency.EUR.toString(), processSimulationAttrMap.getNamedItem("currency").getNodeValue());
        assertEquals("100", processSimulationAttrMap.getNamedItem("processInstances").getNodeValue());
        assertEquals(Instant.ofEpochMilli(1577797200000L).toString(),
            processSimulationAttrMap.getNamedItem("startDateTime").getNodeValue());

        Node arrivalDistributionXmlNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/arrivalRateDistribution");
        NamedNodeMap arrivalRateDistributionAttrMap = arrivalDistributionXmlNode.getAttributes();
        assertEquals(26784.00, Double.parseDouble(arrivalRateDistributionAttrMap.getNamedItem("arg1").getNodeValue()));
        assertNotNull(arrivalRateDistributionAttrMap.getNamedItem("arg2"));
        assertNotNull(arrivalRateDistributionAttrMap.getNamedItem("mean"));
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
        assertNotNull(elementNode.getFirstChild().getAttributes().getNamedItem("arg2"));
        assertNotNull(elementNode.getFirstChild().getAttributes().getNamedItem("mean"));
        assertEquals(timeUnit, elementNode.getFirstChild().getFirstChild().getFirstChild().getNodeValue());

    }

    private void assertBpmnTimetableSimulationInfo(String bpmnXmlString)
        throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        Node timeTableNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/timetables/timetable[1]");
        assertEquals("A_CUSTOM_TIMETABLE_ID", timeTableNode.getAttributes().getNamedItem("id").getNodeValue());
        assertEquals("24/7", timeTableNode.getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("true", timeTableNode.getAttributes().getNamedItem("default").getNodeValue());

        Node timeTableRuleNode = TestHelper.getProcessSimulationInfo(bpmnXmlString,
            "/definitions/process/extensionElements/processSimulationInfo/timetables/timetable[1]/rules/rule[1]");
        assertNotNull(timeTableRuleNode.getAttributes().getNamedItem("id").getNodeValue());
        assertEquals("Default Timeslot", timeTableRuleNode.getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("MONDAY", timeTableRuleNode.getAttributes().getNamedItem("fromWeekDay").getNodeValue());
        assertEquals("SUNDAY", timeTableRuleNode.getAttributes().getNamedItem("toWeekDay").getNodeValue());
        assertEquals("00:00:00.000",
            timeTableRuleNode.getAttributes().getNamedItem("fromTime").getNodeValue());
        assertEquals("23:59:59.999", timeTableRuleNode.getAttributes().getNamedItem("toTime").getNodeValue());

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

        assertResources("Role_1", 5, "A_CUSTOM_TIMETABLE_ID", resourceNodeMap);
        assertResources("Role_2", 10, "A_CUSTOM_TIMETABLE_ID", resourceNodeMap);
        assertResources("Role_3", 15, "A_CUSTOM_TIMETABLE_ID", resourceNodeMap);
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

        assertSequenceFlow("edge2", "0.2025", sequenceFlowMap);
        assertSequenceFlow("edge3", "0.3016", sequenceFlowMap);
        assertSequenceFlow("edge4", "0.4959", sequenceFlowMap);

    }

    private void assertSequenceFlow(final String elementId, String executionProbability,
                                    Map<String, Node> sequenceFlowMap) {

        Node seqFlowNode = sequenceFlowMap.get(elementId);

        assertEquals(elementId, seqFlowNode.getAttributes().getNamedItem("elementId").getNodeValue());
        assertEquals(executionProbability,
            seqFlowNode.getAttributes().getNamedItem("executionProbability").getNodeValue());

    }
}