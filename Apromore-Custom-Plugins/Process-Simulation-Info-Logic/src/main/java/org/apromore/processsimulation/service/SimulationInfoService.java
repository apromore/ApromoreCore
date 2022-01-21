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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.apromore.processsimulation.config.SimulationInfoConfig;
import org.apromore.processsimulation.dto.SimulationData;
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

    private final SimulationInfoConfig config;

    @Autowired
    public SimulationInfoService(SimulationInfoConfig config) {
        this.config = config;
        try {
            jaxbContext = JAXBContext.newInstance(ExtensionElements.class);
        } catch (JAXBException e) {
            log.warn("Unable to instantiate Jaxb context");
        }
    }

    public boolean isFeatureEnabled() {
        return config.isEnable();
    }

    public ProcessSimulationInfo transformToSimulationInfo(
        final SimulationData simulationData) {

        ProcessSimulationInfo processSimulationInfo = null;
        if (isFeatureEnabled() && simulationData != null) {

            ProcessSimulationInfo.ProcessSimulationInfoBuilder builder =
                ProcessSimulationInfo.builder()
                    .id("qbp_" + Locale.getDefault().getLanguage() + UUID.randomUUID())
                    .errors(Errors.builder().build());

            deriveGeneralInfo(builder, simulationData);

            deriveTaskInfo(builder, simulationData);

            deriveTimetable(builder);

            deriveResourceInfo(builder, simulationData);

            processSimulationInfo = builder.build();

        }

        return processSimulationInfo;
    }

    private void deriveGeneralInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData) {

        long startTimeMillis = simulationData.getStartTime();
        long endTimeMillis = simulationData.getEndTime();
        long interArrivalTime = Math.round(
            (endTimeMillis - startTimeMillis) / (double) (1000 * simulationData.getCaseCount()));

        builder.processInstances(simulationData.getCaseCount())
            .currency(Currency.valueOf(config.getDefaultCurrency().toUpperCase(DOCUMENT_LOCALE)))
            .startDateTime(Instant.ofEpochMilli(simulationData.getStartTime()).toString())
            .arrivalRateDistribution(
                Distribution.builder()
                    .timeUnit(TimeUnit.valueOf(config.getDefaultTimeUnit().toUpperCase(DOCUMENT_LOCALE)))
                    .type(DistributionType.valueOf(config.getDefaultDistributionType().toUpperCase(DOCUMENT_LOCALE)))
                    .arg1(Long.toString(interArrivalTime))
                    .build());
    }

    private void deriveTaskInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData) {

        List<Element> taskList = simulationData.getDiagramNodeIDs().stream()
            .map(nodeId -> Element.builder()
                .elementId(nodeId)
                .distributionDuration(Distribution.builder()
                    .type(DistributionType.valueOf(
                        config.getDefaultDistributionType().toUpperCase(DOCUMENT_LOCALE)))
                    .arg1(BigDecimal.valueOf((simulationData.getDiagramNodeDuration(nodeId)))
                        .setScale(2, RoundingMode.HALF_UP).toString())
                    .timeUnit(TimeUnit.valueOf(config.getDefaultTimeUnit().toUpperCase(DOCUMENT_LOCALE)))
                    .build())
                .build())
            .collect(Collectors.toUnmodifiableList());

        builder.tasks(taskList);
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
        final SimulationData simulationData) {

        if (simulationData.getResourceCount() > 0) {
            builder.resources(Arrays.asList(
                Resource.builder()
                    .id(config.getDefaultResource().get(CONFIG_DEFAULT_ID_KEY))
                    .name(config.getDefaultResource().get(CONFIG_DEFAULT_NAME_KEY))
                    .totalAmount(simulationData.getResourceCount())
                    .timetableId(config.getDefaultTimetable().get(CONFIG_DEFAULT_ID_KEY))
                    .build()));
        }

    }

    /**
     * Enrich the bpmn xml model with the additional extension elements
     * including the process simulation information that was derived from the
     * bpmn model.
     *
     * @param bpmnModelXml   the discovered bpmn model
     * @param simulationData the process simulation data derived from the discovered model
     * @return a bpmn model enriched with the additional process simulation information set in the extensionElements
     */
    public String enrichWithSimulationInfo(
        final String bpmnModelXml, final SimulationData simulationData) {
        return enrichWithSimulationInfo(bpmnModelXml, transformToSimulationInfo(simulationData));
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
