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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.plugins.bpmn.plugins.BpmnImportPlugin;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.Distribution;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Element;
import org.apromore.processsimulation.model.Errors;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.TimeUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@UtilityClass
public class TestHelper {

    public static ProcessSimulationInfo createMockProcessSimulationInfo(boolean includeTasks) {
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
                    .arg2("NaN")
                    .mean("NaN")
                    .timeUnit(TimeUnit.SECONDS)
                    .build()
            );

        if (includeTasks) {
            List<Element> tasks = new ArrayList<>();
            tasks.add(Element.builder().elementId("node1").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1("34.34")
                    .arg2("NaN")
                    .mean("NaN")
                    .timeUnit(TimeUnit.SECONDS).build()).build());
            tasks.add(Element.builder().elementId("node2").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1("56.56")
                    .arg2("NaN")
                    .mean("NaN")
                    .timeUnit(TimeUnit.SECONDS).build()).build());
            tasks.add(Element.builder().elementId("node3").distributionDuration(
                Distribution.builder().type(DistributionType.EXPONENTIAL)
                    .arg1("89.89")
                    .arg2("NaN")
                    .mean("NaN")
                    .timeUnit(TimeUnit.SECONDS).build()).build());

            builder.tasks(tasks);
        }
        return builder.build();
    }

    public static Node getProcessSimulationInfo(String bpmnXml, String xpathExpression)
        throws ParserConfigurationException, XPathExpressionException, IOException, SAXException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document xmlDocument = builder.parse(new ByteArrayInputStream(bpmnXml.getBytes()));
        XPath xpath = XPathFactory.newInstance().newXPath();
        return (Node) xpath.compile(xpathExpression).evaluate(xmlDocument, XPathConstants.NODE);
    }


    public static String readBpmnFile(final String fileName) throws IOException {
        return IOUtils.toString(
            TestHelper.class.getResourceAsStream(fileName),
            StandardCharsets.UTF_8);
    }

    public static BPMNDiagram readBpmnDiagram(final String fileName) throws Exception {
        BpmnImportPlugin bpmnImport = new BpmnImportPlugin();
        return bpmnImport.importFromStreamToDiagram(TestHelper.class.getResourceAsStream(fileName), fileName);
    }
}
