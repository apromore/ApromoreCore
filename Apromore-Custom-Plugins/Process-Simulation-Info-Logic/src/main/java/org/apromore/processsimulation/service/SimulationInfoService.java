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
import static org.apromore.processsimulation.config.SimulationInfoConfig.CONFIG_DEFAULT_ID_PREFIX_KEY;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.processsimulation.config.SimulationInfoConfig;
import org.apromore.processsimulation.dto.EdgeFrequency;
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
import org.apromore.processsimulation.model.SequenceFlow;
import org.apromore.processsimulation.model.TimeUnit;
import org.apromore.processsimulation.model.Timetable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

    /**
     * Transform the raw simulation data obtained in PD and convert it to an xml serialisable @ProcessSimulationInfo
     * object.
     *
     * @param simulationData the raw simulation data obtained from Process Discoverer
     * @return simulation data converted into a @ProcessSimulationInfo object
     */
    public ProcessSimulationInfo transformToSimulationInfo(
        final SimulationData simulationData) {

        ProcessSimulationInfo processSimulationInfo = null;
        if (isFeatureEnabled() && simulationData != null) {

            ProcessSimulationInfo.ProcessSimulationInfoBuilder builder =
                ProcessSimulationInfo.builder()
                    .id("qbp_" + Locale.getDefault().getLanguage() + UUID.randomUUID())
                    .errors(Errors.builder().build());

            deriveGeneralInfo(builder, simulationData);

            Map<String, String> resourceNameToId = deriveResourceInfo(builder, simulationData);

            deriveTaskInfo(builder, simulationData, resourceNameToId);

            deriveTimetable(builder);

            deriveGatewayProbabilities(builder, simulationData);

            processSimulationInfo = builder.build();
        }

        return processSimulationInfo;
    }

    private void deriveGeneralInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData) {

        double interArrivalTimeMillis = getInterArrivalTime(simulationData);
        TimeUnit timeUnit = getDisplayTimeUnit(interArrivalTimeMillis);

        builder.processInstances(simulationData.getCaseCount())
            .currency(Currency.valueOf(config.getDefaultCurrency().toUpperCase(DOCUMENT_LOCALE)))
            .startDateTime(
                Instant.ofEpochMilli(simulationData.getStartTime()).toString())
            .arrivalRateDistribution(
                Distribution.builder()
                    .timeUnit(timeUnit)
                    .type(DistributionType.valueOf(config.getDefaultDistributionType().toUpperCase(DOCUMENT_LOCALE)))
                    .arg1(getDisplayTimeDuration(interArrivalTimeMillis).toString())
                    .build());
    }

    /**
     * Returns the inter-arrival time of events in seconds, based on a default 9 - 5 business calendar.
     *
     * @param simulationData the raw simulation data from PD
     * @return the inter-arrival time of events (in milliseconds)
     */
    protected double getInterArrivalTime(final SimulationData simulationData) {
        CalendarModel arrivalCalendar = null;
        if (simulationData.getCalendarModel() == null) {
            arrivalCalendar = new CalendarModelBuilder().with5DayWorking().build();
        } else {
            arrivalCalendar = simulationData.getCalendarModel();
        }

        return arrivalCalendar.getDurationMillis(simulationData.getStartTime(), simulationData.getEndTime())
               / (double) simulationData.getCaseCount();
    }

    private void deriveTaskInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData,
        final Map<String, String> resourceNameToId) {

        List<Element> taskList = simulationData.getDiagramNodeIDs().stream()
            .map(nodeId -> {
                double durationMillis = simulationData.getDiagramNodeDuration(nodeId);
                TimeUnit timeUnit = getDisplayTimeUnit(durationMillis);

                String roleName = simulationData.getRoleNameByNodeId(nodeId);
                if (roleName.equals(SimulationData.DEFAULT_ROLE)) {
                    roleName = config.getDefaultResource().get(CONFIG_DEFAULT_NAME_KEY);
                }

                return Element.builder()
                    .elementId(nodeId)
                    .distributionDuration(Distribution.builder()
                        .type(DistributionType.valueOf(
                            config.getDefaultDistributionType().toUpperCase(DOCUMENT_LOCALE)))
                        .arg1(getDisplayTimeDuration(durationMillis).toString())
                        .timeUnit(timeUnit)
                        .build())
                    .resourceIds(List.of(resourceNameToId.get(roleName)))
                    .build();
            })
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

    private Map<String, String> deriveResourceInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData) {

        Map<String, String> resouceNameToId = new HashMap<>();

        if (ObjectUtils.isEmpty(simulationData.getResourceCountsByRole())) {
            // No role to resource count mapping. Use the QBP_DEFAULT_RESOURCE tag and the total
            // resource count agains it.
            String defaultResourceId = config.getDefaultResource().get(CONFIG_DEFAULT_ID_PREFIX_KEY)
                                       + config.getDefaultResource().get(CONFIG_DEFAULT_ID_KEY);

            builder.resources(List.of(
                Resource.builder()
                    .id(defaultResourceId)
                    .name(config.getDefaultResource().get(CONFIG_DEFAULT_NAME_KEY))
                    .totalAmount(simulationData.getResourceCount())
                    .timetableId(config.getDefaultTimetable().get(CONFIG_DEFAULT_ID_KEY))
                    .build()
            ));

            resouceNameToId.put(config.getDefaultResource().get(CONFIG_DEFAULT_NAME_KEY), defaultResourceId);

        } else {

            builder.resources(simulationData.getResourceCountsByRole().entrySet().stream()
                .map(roleToResourceCount -> {
                    String resourceId;
                    String resourceName;

                    /*
                     * It gets a bit confusing here as in QBP, the DEFAULT_ROLE (from PD, when there is an activity
                     * with an empty role) is treated as a QBP_DEFAULT_RESOURCE.
                     *
                     * In QBP, resource == role
                     */
                    if (roleToResourceCount.getKey().equals(SimulationData.DEFAULT_ROLE)) {
                        // key -> QBP_DEFAULT_RESOURCE (i.e. no associated role)
                        resourceId = config.getDefaultResource().get(CONFIG_DEFAULT_ID_PREFIX_KEY)
                                     + config.getDefaultResource().get(CONFIG_DEFAULT_ID_KEY);
                        resourceName = config.getDefaultResource().get(CONFIG_DEFAULT_NAME_KEY);
                    } else {
                        resourceId = config.getDefaultResource().get(CONFIG_DEFAULT_ID_PREFIX_KEY) + UUID.randomUUID();
                        resourceName = roleToResourceCount.getKey();
                    }

                    resouceNameToId.put(resourceName, resourceId);

                    return Resource.builder()
                        .id(resourceId)
                        .name(resourceName)
                        .totalAmount(roleToResourceCount.getValue())
                        .timetableId(config.getDefaultTimetable().get(CONFIG_DEFAULT_ID_KEY))
                        .build();
                }).collect(Collectors.toList()));
        }

        return resouceNameToId;
    }

    private void deriveGatewayProbabilities(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData) {

        List<SequenceFlow> sequenceFlowList = new ArrayList<>();
        if (simulationData.getEdgeFrequencies() != null && !simulationData.getEdgeFrequencies().isEmpty()) {

            simulationData.getEdgeFrequencies().entrySet().forEach(gatewayEntry -> {

                // Calculate the total outbound edge frequencies for each gateway
                double totalFrequency = gatewayEntry.getValue().stream()
                    .map(EdgeFrequency::getFrequency)
                    .reduce(0.0D, Double::sum);

                gatewayEntry.getValue().forEach(edgeFrequency -> sequenceFlowList.add(SequenceFlow.builder()
                    .elementId(edgeFrequency.getEdgeId())
                    .executionProbability(
                        BigDecimal.valueOf(edgeFrequency.getFrequency() / totalFrequency)
                            .setScale(4, RoundingMode.HALF_UP).doubleValue())
                    .build()));
            });

            builder.sequenceFlows(sequenceFlowList);
        }
    }

    private TimeUnit getDisplayTimeUnit(double timeDurationMillis) {
        TimeUnit timeUnit;

        if (timeDurationMillis <= 60000) {
            timeUnit = TimeUnit.SECONDS;
        } else if (timeDurationMillis > 60000 && timeDurationMillis <= 3600000) {
            timeUnit = TimeUnit.MINUTES;
        } else if (timeDurationMillis > 3600000) {
            timeUnit = TimeUnit.HOURS;
        } else {
            timeUnit = TimeUnit.valueOf(config.getDefaultTimeUnit().toUpperCase(DOCUMENT_LOCALE));
        }
        return timeUnit;
    }

    /**
     * Converts milliseconds into the time duration based on the desired TimeUnit.
     * For now the Simulator and BPMN Editor only accept seconds as the value, and therefore
     * we convert milliseconds to seconds.
     *
     * @param timeDuration the time duration in milliseconds
     * @return the converted rounded time duration
     */
    private BigDecimal getDisplayTimeDuration(double timeDuration) {
        return BigDecimal.valueOf(timeDuration / TimeUnit.SECONDS.getNumberOfMilliseconds())
            .setScale(2, RoundingMode.HALF_UP);
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
