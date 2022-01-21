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
import org.apromore.processsimulation.dto.SimulationData;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.Distribution;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Element;
import org.apromore.processsimulation.model.Errors;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.Resource;
import org.apromore.processsimulation.model.Rule;
import org.apromore.processsimulation.model.TimeUnit;
import org.apromore.processsimulation.model.Timetable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@UtilityClass
public class TestHelper {

    public static ProcessSimulationInfo createMockProcessSimulationInfo(
        boolean includeTasks, boolean includeTimetable, boolean includeResource) {
        ProcessSimulationInfo.ProcessSimulationInfoBuilder builder = ProcessSimulationInfo.builder()
            .id("some_random_guid")
            .errors(Errors.builder().build())
            .currency(Currency.EUR)
            .startDateTime(Instant.ofEpochMilli(1577797200000L).toString())
            .processInstances(100)
            .arrivalRateDistribution(
                Distribution.builder()
                    .type(DistributionType.EXPONENTIAL)
                    .arg1("26784")
                    .timeUnit(TimeUnit.SECONDS)
                    .build()
            );

        if (includeTasks) {
            List<Element> tasks = new ArrayList<>();
            tasks.add(Element.builder().elementId("node1").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1("34.34")
                    .timeUnit(TimeUnit.SECONDS).build()).build());
            tasks.add(Element.builder().elementId("node2").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1("56.56")
                    .timeUnit(TimeUnit.SECONDS).build()).build());
            tasks.add(Element.builder().elementId("node3").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1("89.89")
                    .timeUnit(TimeUnit.SECONDS).build()).build());

            builder.tasks(tasks);
        }

        if (includeTimetable) {
            builder.timetables(Arrays.asList(Timetable.builder()
                    .id("A_DEFAULT_TIMETABLE_ID")
                    .name("Arrival Timetable")
                    .defaultTimetable(true)
                    .rules(Arrays.asList(Rule.builder()
                            .name("Default Timeslot")
                            .id("DEF_RULE_ID")
                            .fromWeekDay(DayOfWeek.SUNDAY)
                            .toWeekDay(DayOfWeek.SATURDAY)
                            .fromTime("10:00:00.000+00:00")
                            .toTime("15:00:00.000+00:00")
                        .build()))
                .build()));
        }

        if (includeResource) {
            builder.resources(Arrays.asList(Resource.builder()
                .id("A_DEFAULT_RESOURCE_ID")
                .name("The default resource name")
                .timetableId("A_DEFAULT_TIMETABLE_ID")
                .totalAmount(23)
                .build()));
        }

        return builder.build();
    }

    public static SimulationData createMockSimulationData() {

        SimulationData simulationData = SimulationData.builder()
            .caseCount(100)
            .resourceCount(23)
            .startTime(1577797200000L)
            .endTime(1580475600000L)
            .nodeWeights(Map.of("node1",34.34, "node2", 56.56, "node3", 89.89))
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
