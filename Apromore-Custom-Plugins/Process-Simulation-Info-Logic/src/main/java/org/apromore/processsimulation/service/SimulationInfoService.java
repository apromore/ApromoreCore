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

import lombok.extern.slf4j.Slf4j;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.abstraction.AbstractAbstraction;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.Distribution;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Element;
import org.apromore.processsimulation.model.Errors;
import org.apromore.processsimulation.model.ExtensionElements;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
public class SimulationInfoService {

    private static final String XML_START_TAG = "<?xml";
    private static final String XML_START_EXTENSION_ELEMENTS_TAG = "<extensionElements>";
    private static final String XML_END_PROCESS_TAG = "</process>";
    private static final String XML_END_BPMN_PROCESS_TAG = "</bpmn:process>";
    private static final String XML_START_DEFINITIONS_TAG = "<definitions";
    private static final String XML_START_BPMN_DEFINITIONS_TAG = "<bpmn:definitions";
    private static final String XML_QBP_NAMESPACE = "\n xmlns:qbp=\"http://www.qbp-simulator.com/Schema201212\"\n";
    private static final String NAN = "NaN";

    private JAXBContext jaxbContext;

    private static class SimulationInfoServiceHolder {
        private static final SimulationInfoService INSTANCE = new SimulationInfoService();
    }

    public static SimulationInfoService getInstance() {
        return SimulationInfoServiceHolder.INSTANCE;
    }

    private SimulationInfoService() {
        try {
            jaxbContext = JAXBContext.newInstance(ExtensionElements.class);
        } catch (JAXBException e) {
            log.warn("Unable to instantiate Jaxb context");
        }
    }

    public ProcessSimulationInfo deriveSimulationInfo(
            final Abstraction abstraction) {

        ProcessSimulationInfo processSimulationInfo = null;
        if (abstraction instanceof AbstractAbstraction &&
                abstraction != null &&
                ((AbstractAbstraction) abstraction).getLog() != null) {

            AttributeLogSummary logSummary = ((AbstractAbstraction) abstraction).getLog().getLogSummary();

            if (logSummary != null) {
                ProcessSimulationInfo.ProcessSimulationInfoBuilder builder =
                        ProcessSimulationInfo.builder()
                                .id("qbp_" + Locale.getDefault().getLanguage() + UUID.randomUUID())
                                .errors(Errors.builder().build());

                deriveGeneralSimulationInfo(builder, logSummary);
                deriveTaskSimulationInfo(builder, abstraction);

                processSimulationInfo = builder.build();
            }
        }

        return processSimulationInfo;
    }

    private void deriveGeneralSimulationInfo(
            ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
            final AttributeLogSummary logSummary) {

        long startTimeMillis = logSummary.getStartTime();
        long endTimeMillis = logSummary.getEndTime();
        long interArrivalTime = Math.round(
                ((double) (endTimeMillis - startTimeMillis) / (double) 1000) / (double) logSummary.getCaseCount());

        builder.processInstances(logSummary.getCaseCount())
                .currency(Currency.EUR)
                .startDateTime(Instant.ofEpochMilli(logSummary.getStartTime()).toString())
                .arrivalRateDistribution(
                        Distribution.builder()
                                .timeUnit(TimeUnit.SECONDS)
                                .type(DistributionType.EXPONENTIAL)
                                .arg1(Long.toString(interArrivalTime))
                                .mean(NAN)
                                .arg2(NAN)
                                .build());
    }

    private void deriveTaskSimulationInfo(
            ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
            final Abstraction abstraction) {

        if (abstraction.getDiagram() != null && abstraction.getDiagram().getNodes() != null) {
            List<Element> taskList = new ArrayList<>();

            abstraction.getDiagram().getNodes()
                    .stream()
                    .filter(bpmnNode -> bpmnNode instanceof Activity)
                    .forEach(bpmnNode -> {
                        BigDecimal nodeAvgDuration = new BigDecimal(((AbstractAbstraction) abstraction).getLog().getGraphView()
                                .getNodeWeight(bpmnNode.getLabel(), MeasureType.DURATION,
                                        MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE) / 1000)
                                .setScale(2, RoundingMode.HALF_UP);

                        taskList.add(Element.builder()
                                .elementId(bpmnNode.getId().toString())
                                .distributionDuration(Distribution.builder()
                                        .type(DistributionType.EXPONENTIAL)
                                        .arg1(nodeAvgDuration.toString())
                                        .arg2(NAN)
                                        .mean(NAN)
                                        .timeUnit(TimeUnit.SECONDS)
                                        .build())
                                .build());
                    });

            builder.tasks(taskList);
        }
    }


    /**
     * Enrich the bpmn xml model with the additional extension elements
     * including the process simulation information that was derived from the
     * bpmn model
     *
     * @param bpmnModelXml          the discovered bpmn model
     * @param processSimulationInfo the process simulation information derived from the discovered model
     * @return a bpmn model enriched with the additional process simulation information set in the extensionElements
     */
    public String enrichWithSimulationInfo(
            final String bpmnModelXml, final ProcessSimulationInfo processSimulationInfo) {

        String enrichedBpmnXml = bpmnModelXml;
        if (processSimulationInfo != null) {

            ExtensionElements extensionElements = ExtensionElements.builder()
                    .processSimulationInfo(processSimulationInfo).build();

            // Inject process simulation information to bpmn export xml
            enrichedBpmnXml = injectExtensionElements(
                    bpmnModelXml, stringifyExtensionElements(extensionElements, jaxbContext));
        }

        return enrichedBpmnXml;
    }

    private String injectExtensionElements(String exportedBpmnXml, String extensionElementsXml) {
        StringBuilder enrichedBpmnXml = new StringBuilder(exportedBpmnXml);

        if (extensionElementsXml != null && !extensionElementsXml.isEmpty()) {
            // remove the <?xml> tags if present from the extension elements xml
            if (extensionElementsXml.contains(XML_START_TAG)) {
                extensionElementsXml = extensionElementsXml
                        .substring(extensionElementsXml.indexOf(XML_START_EXTENSION_ELEMENTS_TAG));
            }

            // inject the qbp namespace and the extension elements
            if (enrichedBpmnXml.indexOf(XML_START_DEFINITIONS_TAG) > 0) {
                enrichedBpmnXml.insert(
                        exportedBpmnXml.indexOf(XML_START_DEFINITIONS_TAG) + XML_START_DEFINITIONS_TAG.length(),
                        XML_QBP_NAMESPACE);

            } else if (enrichedBpmnXml.indexOf(XML_START_BPMN_DEFINITIONS_TAG) > 0) {
                enrichedBpmnXml.insert(
                        exportedBpmnXml.indexOf(XML_START_BPMN_DEFINITIONS_TAG) + XML_START_BPMN_DEFINITIONS_TAG.length(),
                        XML_QBP_NAMESPACE);
            }


            if (enrichedBpmnXml.indexOf(XML_END_PROCESS_TAG) > 0) {
                enrichedBpmnXml.insert(enrichedBpmnXml.indexOf(XML_END_PROCESS_TAG), extensionElementsXml);

            } else if (enrichedBpmnXml.indexOf(XML_END_BPMN_PROCESS_TAG) > 0) {
                enrichedBpmnXml.insert(enrichedBpmnXml.indexOf(XML_END_BPMN_PROCESS_TAG), extensionElementsXml);
            }
        }

        return enrichedBpmnXml.toString();
    }

    private String stringifyExtensionElements(
            final ExtensionElements extensionElements,
            final JAXBContext jaxbContext) {
        StringWriter extElementsStringWriter = new StringWriter();
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(extensionElements, extElementsStringWriter);

        } catch (JAXBException e) {
            log.warn("Unable to unmarshall extension elements");
        }

        return extElementsStringWriter.toString();
    }
}
