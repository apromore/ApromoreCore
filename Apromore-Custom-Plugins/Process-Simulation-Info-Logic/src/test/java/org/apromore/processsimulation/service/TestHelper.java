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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.processsimulation.dto.EdgeFrequency;
import org.apromore.processsimulation.dto.SimulationData;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.Distribution;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Element;
import org.apromore.processsimulation.model.Errors;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.Resource;
import org.apromore.processsimulation.model.Rule;
import org.apromore.processsimulation.model.SequenceFlow;
import org.apromore.processsimulation.model.TimeUnit;
import org.apromore.processsimulation.model.Timetable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@UtilityClass
public class TestHelper {

    public static ProcessSimulationInfo createMockProcessSimulationInfo(
        boolean includeTasks,
        boolean includeTimetable,
        boolean includeResource,
        boolean includeGatewayProbabilities) {
        ProcessSimulationInfo.ProcessSimulationInfoBuilder builder = ProcessSimulationInfo.builder()
            .id("some_random_guid")
            .errors(Errors.builder().build())
            .currency(Currency.EUR)
            .startDateTime(Instant.ofEpochMilli(1577797200000L).toString())
            .processInstances(100)
            .arrivalRateDistribution(
                Distribution.builder()
                    .type(DistributionType.EXPONENTIAL)
                    .arg1(26784.00)
                    .timeUnit(TimeUnit.HOURS)
                    .build()
            );

        if (includeTasks) {
            List<Element> tasks = new ArrayList<>();
            tasks.add(Element.builder().elementId("node1").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1(34.34)
                    .timeUnit(TimeUnit.SECONDS).build()).build());
            tasks.add(Element.builder().elementId("node2").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1(56.56)
                    .timeUnit(TimeUnit.SECONDS).build()).build());
            tasks.add(Element.builder().elementId("node3").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1(89.89)
                    .timeUnit(TimeUnit.MINUTES).build()).build());

            builder.tasks(tasks);
        }

        if (includeTimetable) {
            builder.timetables(Arrays.asList(Timetable.builder()
                .id("A_CUSTOM_TIMETABLE_ID")
                .name("24/7")
                .defaultTimetable(true)
                .rules(Arrays.asList(Rule.builder()
                    .name("Default Timeslot")
                    .id("DEF_RULE_ID")
                    .fromWeekDay(DayOfWeek.MONDAY)
                    .toWeekDay(DayOfWeek.SUNDAY)
                    .fromTime("00:00:00.000")
                    .toTime("23:59:59.999")
                    .build()))
                .build()));
        }

        if (includeResource) {
            builder.resources(List.of(
                Resource.builder()
                    .id("QBP_1").name("Role_1").totalAmount(5).timetableId("A_CUSTOM_TIMETABLE_ID").build(),
                Resource.builder()
                    .id("QBP_2").name("Role_2").totalAmount(10).timetableId("A_CUSTOM_TIMETABLE_ID").build(),
                Resource.builder()
                    .id("QBP_3").name("Role_3").totalAmount(15).timetableId("A_CUSTOM_TIMETABLE_ID").build()
            ));
        }

        if (includeGatewayProbabilities) {
            builder.sequenceFlows(Arrays.asList(
                SequenceFlow.builder()
                    .elementId("edge2")
                    .executionProbability("0.2025")
                    .build(),
                SequenceFlow.builder()
                    .elementId("edge3")
                    .executionProbability("0.3016")
                    .build(),
                SequenceFlow.builder()
                    .elementId("edge4")
                    .executionProbability("0.4959")
                    .build()));
        }

        return builder.build();
    }

    public static SimulationData createMockSimulationData() {

        CalendarModel mockCalendarModel = new CalendarModelBuilder().withAllDayAllTime().build();
        mockCalendarModel.setName("24/7");

        SimulationData simulationData = SimulationData.builder()
            .caseCount(100)
            .resourceCount(23)
            .startTime(1577797200000L)
            .endTime(1580475600000L)
            .nodeWeights(Map.of("node1", 34340.00, "node2", 56560.00, "node3", 89890.00))
            .resourceCountByRole(Map.of("Role_1", 5, "Role_2", 10, "Role_3", 15))
            .nodeIdToRoleName(Map.of("node1", "Role_1", "node2", "Role_2", "node3", "Role_3"))
            .edgeFrequencies(Map.of("node9", List.of(
                EdgeFrequency.builder().edgeId("edge2").frequency(2025).build(),
                EdgeFrequency.builder().edgeId("edge3").frequency(3016).build(),
                EdgeFrequency.builder().edgeId("edge4").frequency(4959).build())))
            .calendarModel(mockCalendarModel)
            .build();
        return simulationData;
    }

    public static Node getProcessSimulationInfo(String bpmnXml, String xpathExpression)
        throws ParserConfigurationException, XPathExpressionException, IOException, SAXException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (Node) xpath.compile(xpathExpression).evaluate(xmlDocument, XPathConstants.NODE);
    }


    public static String readBpmnFile(final String fileName) throws IOException {
        return IOUtils.toString(
            TestHelper.class.getResourceAsStream(fileName),
            StandardCharsets.UTF_8);
    }
}
