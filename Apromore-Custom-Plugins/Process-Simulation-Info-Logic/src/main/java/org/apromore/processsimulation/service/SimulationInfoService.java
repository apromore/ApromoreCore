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

import static org.apromore.processsimulation.config.SimulationInfoConfig.CONFIG_DEFAULT_ID_KEY;
import static org.apromore.processsimulation.config.SimulationInfoConfig.CONFIG_DEFAULT_NAME_KEY;
import static org.apromore.processsimulation.config.SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_FROM_TIME;
import static org.apromore.processsimulation.config.SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_FROM_WEEKDAY_KEY;
import static org.apromore.processsimulation.config.SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_NAME_KEY;
import static org.apromore.processsimulation.config.SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_TO_TIME;
import static org.apromore.processsimulation.config.SimulationInfoConfig.CONFIG_DEFAULT_TIMESLOT_TO_WEEKDAY_KEY;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.abstraction.AbstractAbstraction;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processsimulation.config.SimulationInfoConfig;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.Distribution;
import org.apromore.processsimulation.model.DistributionType;
import org.apromore.processsimulation.model.Element;
import org.apromore.processsimulation.model.Errors;
import org.apromore.processsimulation.model.ExtensionElements;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.model.Resource;
import org.apromore.processsimulation.model.Rule;
import org.apromore.processsimulation.model.TimeUnit;
import org.apromore.processsimulation.model.Timetable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SimulationInfoService {

    private static final String XML_START_TAG = "<?xml";
    private static final String XML_START_EXTENSION_ELEMENTS_TAG = "<extensionElements>";
    private static final String XML_END_PROCESS_TAG = "</process>";
    private static final String XML_END_BPMN_PROCESS_TAG = "</bpmn:process>";
    private static final String XML_START_DEFINITIONS_TAG = "<definitions";
    private static final String XML_START_BPMN_DEFINITIONS_TAG = "<bpmn:definitions";
    private static final String XML_QBP_NAMESPACE = "\n xmlns:qbp=\"http://www.qbp-simulator.com/Schema201212\"\n";
    private static final Locale DOCUMENT_LOCALE = Locale.ENGLISH;

    private JAXBContext jaxbContext;

    private SimulationInfoConfig config;

    @Autowired
    public SimulationInfoService(SimulationInfoConfig config) {
        this.config = config;
        try {
            jaxbContext = JAXBContext.newInstance(ExtensionElements.class);
        } catch (JAXBException e) {
            log.warn("Unable to instantiate Jaxb context");
        }
    }

    public ProcessSimulationInfo deriveSimulationInfo(
        final Abstraction abstraction) {

        ProcessSimulationInfo processSimulationInfo = null;
        if (isFeatureEnabled()
            && abstraction != null
            && abstraction instanceof AbstractAbstraction
            && ((AbstractAbstraction) abstraction).getLog() != null) {

            final AbstractAbstraction abstractAbstraction = (AbstractAbstraction) abstraction;

            AttributeLogSummary logSummary = ((AbstractAbstraction) abstraction).getLog().getLogSummary();

            if (logSummary != null) {
                ProcessSimulationInfo.ProcessSimulationInfoBuilder builder =
                    ProcessSimulationInfo.builder()
                        .id("qbp_" + Locale.getDefault().getLanguage() + UUID.randomUUID())
                        .errors(Errors.builder().build());

                deriveGeneralInfo(builder, logSummary);

                deriveTaskInfo(builder, abstractAbstraction);

                deriveTimetable(builder);

                deriveResourceInfo(builder, abstractAbstraction);

                processSimulationInfo = builder.build();
            }
        }

        return processSimulationInfo;
    }

    private void deriveGeneralInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final AttributeLogSummary logSummary) {

        long startTimeMillis = logSummary.getStartTime();
        long endTimeMillis = logSummary.getEndTime();
        long interArrivalTime = Math.round(
             (endTimeMillis - startTimeMillis) / (double) (1000 * logSummary.getCaseCount()));

        builder.processInstances(logSummary.getCaseCount())
            .currency(Currency.valueOf(config.getDefaultCurrency().toUpperCase(DOCUMENT_LOCALE)))
            .startDateTime(Instant.ofEpochMilli(logSummary.getStartTime()).toString())
            .arrivalRateDistribution(
                Distribution.builder()
                    .timeUnit(TimeUnit.valueOf(config.getDefaultTimeUnit().toUpperCase(DOCUMENT_LOCALE)))
                    .type(DistributionType.valueOf(config.getDefaultDistributionType().toUpperCase(DOCUMENT_LOCALE)))
                    .arg1(Long.toString(interArrivalTime))
                    .build());
    }

    private void deriveTaskInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final AbstractAbstraction abstraction) {

        if (abstraction.getDiagram() != null && abstraction.getDiagram().getNodes() != null) {
            List<Element> taskList = new ArrayList<>();

            abstraction.getDiagram().getNodes()
                .stream()
                .filter(Activity.class::isInstance)
                .forEach(bpmnNode -> {
                    BigDecimal nodeAvgDuration =
                        BigDecimal.valueOf(abstraction.getLog().getGraphView()
                                .getNodeWeight(bpmnNode.getLabel(), MeasureType.DURATION,
                                    MeasureAggregation.MEAN, MeasureRelation.ABSOLUTE) / 1000)
                            .setScale(2, RoundingMode.HALF_UP);

                    taskList.add(Element.builder()
                        .elementId(bpmnNode.getId().toString())
                        .distributionDuration(Distribution.builder()
                            .type(DistributionType.valueOf(
                                config.getDefaultDistributionType().toUpperCase(DOCUMENT_LOCALE)))
                            .arg1(nodeAvgDuration.toString())
                            .timeUnit(TimeUnit.valueOf(config.getDefaultTimeUnit().toUpperCase(DOCUMENT_LOCALE)))
                            .build())
                        .build());
                });

            builder.tasks(taskList);
        }
    }

    private void deriveTimetable(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder) {

        builder.timetables(
            Arrays.asList(Timetable.builder()
                .defaultTimetable(true)
                .id(config.getDefaultTimetable().get(CONFIG_DEFAULT_ID_KEY))
                .name(config.getDefaultTimetable().get(CONFIG_DEFAULT_NAME_KEY))
                .rules(Arrays.asList(Rule.builder()
                    .id(UUID.randomUUID().toString())
                    .name(config.getDefaultTimetable().get(CONFIG_DEFAULT_TIMESLOT_NAME_KEY))
                    .fromWeekDay(DayOfWeek.valueOf(
                        config.getDefaultTimetable().get(CONFIG_DEFAULT_TIMESLOT_FROM_WEEKDAY_KEY)
                            .toUpperCase(DOCUMENT_LOCALE)))
                    .toWeekDay(DayOfWeek.valueOf(
                        config.getDefaultTimetable().get(CONFIG_DEFAULT_TIMESLOT_TO_WEEKDAY_KEY)
                            .toUpperCase(DOCUMENT_LOCALE)))
                    .fromTime(config.getDefaultTimetable().get(CONFIG_DEFAULT_TIMESLOT_FROM_TIME))
                    .toTime(config.getDefaultTimetable().get(CONFIG_DEFAULT_TIMESLOT_TO_TIME))
                    .build()))
                .build()));
    }

    private void deriveResourceInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final AbstractAbstraction abstraction) {
        if (abstraction.getLog() != null
            && abstraction.getLog().getFullLog() != null
            && abstraction.getLog().getFullLog().getAttributeStore() != null) {

            IndexableAttribute resourcesAttribute =
                abstraction.getLog().getFullLog().getAttributeStore().getStandardEventResource();

            if (resourcesAttribute != null) {
                builder.resources(Arrays.asList(
                    Resource.builder()
                        .id(config.getDefaultResource().get(CONFIG_DEFAULT_ID_KEY))
                        .name(config.getDefaultResource().get(CONFIG_DEFAULT_NAME_KEY))
                        .totalAmount(resourcesAttribute.getValueSize())
                        .timetableId(config.getDefaultTimetable().get(CONFIG_DEFAULT_ID_KEY))
                        .build()));
            }
        }
    }


    /**
     * Enrich the bpmn xml model with the additional extension elements
     * including the process simulation information that was derived from the
     * bpmn model.
     *
     * @param bpmnModelXml          the discovered bpmn model
     * @param processSimulationInfo the process simulation information derived from the discovered model
     * @return a bpmn model enriched with the additional process simulation information set in the extensionElements
     */
    public String enrichWithSimulationInfo(
        final String bpmnModelXml, final ProcessSimulationInfo processSimulationInfo) {

        String enrichedBpmnXml = bpmnModelXml;
        if (isFeatureEnabled() && processSimulationInfo != null) {

            ExtensionElements extensionElements = ExtensionElements.builder()
                .processSimulationInfo(processSimulationInfo).build();

            // Inject process simulation information to bpmn export xml
            enrichedBpmnXml = injectExtensionElements(
                bpmnModelXml, stringifyExtensionElements(extensionElements, jaxbContext));
        }

        return enrichedBpmnXml;
    }

    private boolean isFeatureEnabled() {
        return Boolean.valueOf(config.isEnable());
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
