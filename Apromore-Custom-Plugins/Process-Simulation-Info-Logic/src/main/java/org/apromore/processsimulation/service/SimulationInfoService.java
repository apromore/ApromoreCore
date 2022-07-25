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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.WorkDayModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.dao.model.Usermetadata;
import org.apromore.processsimulation.config.SimulationInfoConfig;
import org.apromore.processsimulation.dto.EdgeFrequency;
import org.apromore.processsimulation.dto.SimulationData;
import org.apromore.processsimulation.model.CostingData;
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
import org.apromore.service.UserMetadataService;
import org.apromore.util.UserMetadataTypeEnum;
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
    private static final DateTimeFormatter TIMETABLE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0000");
    private static final String CUSTOM_CALENDER_NAME = "Log timetable";


    private JAXBContext jaxbContext;

    private final SimulationInfoConfig config;
    private final CalendarService calendarService;
    private final UserMetadataService userMetadataService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SimulationInfoService(SimulationInfoConfig config, CalendarService calendarService,
                                 UserMetadataService userMetadataService) {
        this.config = config;
        this.calendarService = calendarService;
        this.userMetadataService = userMetadataService;
        this.objectMapper = new ObjectMapper();

        try {
            jaxbContext = JAXBContext.newInstance(ExtensionElements.class);
        } catch (JAXBException e) {
            log.warn("Unable to instantiate Jaxb context");
        }

        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
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

            deriveTimetable(builder, simulationData);

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

        builder.processInstances(simulationData.getCaseCount() > config.getDefaultMaxProcessInstances()
                ? config.getDefaultMaxProcessInstances() : simulationData.getCaseCount())
            .currency(Currency.valueOf(config.getDefaultCurrency().toUpperCase(DOCUMENT_LOCALE)))
            .startDateTime(
                Instant.ofEpochMilli(simulationData.getStartTime()).toString())
            .arrivalRateDistribution(
                Distribution.builder()
                    .timeUnit(timeUnit)
                    .type(DistributionType.valueOf(config.getDefaultDistributionType().toUpperCase(DOCUMENT_LOCALE)))
                    .arg1(getDisplayTimeDuration(interArrivalTimeMillis).doubleValue())
                    .build());
    }

    /**
     * Returns the inter-arrival time of events in seconds, based on the calendar from PD.
     *
     * @param simulationData the raw simulation data from PD
     * @return the inter-arrival time of events (in milliseconds)
     */
    protected double getInterArrivalTime(final SimulationData simulationData) {

        return simulationData.getCalendarModel().getDurationMillis(
            simulationData.getStartTime(), simulationData.getEndTime()) / (double) simulationData.getCaseCount();
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
                    roleName = config.getDefaultResourceName();
                }

                return Element.builder()
                    .elementId(nodeId)
                    .distributionDuration(Distribution.builder()
                        .type(DistributionType.valueOf(
                            config.getDefaultDistributionType().toUpperCase(DOCUMENT_LOCALE)))
                        .arg1(getDisplayTimeDuration(durationMillis).doubleValue())
                        .timeUnit(timeUnit)
                        .build())
                    .resourceIds(List.of(resourceNameToId.get(roleName)))
                    .build();
            })
            .collect(Collectors.toUnmodifiableList());

        builder.tasks(taskList);
    }

    private void deriveTimetable(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData) {

        CalendarModel calendarModel = simulationData.getCalendarModel();

        if (calendarModel.is247()) {
            calendarModel.setName(SimulationData.DEFAULT_CALENDAR_NAME);
            builder.timetables(List.of(createTimetable(
                calendarModel, config.getCustomTimetableId(), true)));
        } else {
            calendarModel.setName(CUSTOM_CALENDER_NAME);
            builder.timetables(
                List.of(
                    createTimetable(calendarModel, config.getCustomTimetableId(), true),
                    createTimetable(getDefaultCalendarModel(),
                        config.getDefaultTimetableId(), false)
                ));
        }
    }

    private CalendarModel getDefaultCalendarModel() {
        CalendarModel defaultCalendar = calendarService.getGenericCalendar();
        defaultCalendar.setName(SimulationData.DEFAULT_CALENDAR_NAME);
        return defaultCalendar;
    }

    private Timetable createTimetable(
        final CalendarModel calendarModel,
        final String timetableId,
        boolean setAsDefault) {
        List<WorkDayModel> workingDays = calendarModel.getOrderedWorkDay().stream()
            .filter(WorkDayModel::isWorkingDay)
            .collect(Collectors.toList());

        // The timetable from PD
        return Timetable.builder()
            .defaultTimetable(setAsDefault)
            .id(timetableId)
            .name(calendarModel.getName())
            .rules(Arrays.asList(Rule.builder()
                .id(UUID.randomUUID().toString())
                .name(config.getDefaultTimeslotName())
                .fromWeekDay(workingDays.get(0).getDayOfWeek())
                .toWeekDay(workingDays.get(workingDays.size() - 1).getDayOfWeek())
                .fromTime(workingDays.get(0).getStartTime().format(TIMETABLE_TIME_FORMATTER))
                .toTime(workingDays.get(workingDays.size() - 1).getEndTime().format(TIMETABLE_TIME_FORMATTER))
                .build()))
            .build();
    }

    private Map<String, String> deriveResourceInfo(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData) {

        Map<String, String> resouceNameToId = new HashMap<>();

        if (ObjectUtils.isEmpty(simulationData.getResourceCountsByRole())) {
            // No role to resource count mapping. Use the QBP_DEFAULT_RESOURCE tag and the total
            // resource count agains it.
            String defaultResourceId = config.getDefaultResourceIdPrefix() + config.getDefaultResourceId();

            builder.resources(List.of(
                Resource.builder()
                    .id(defaultResourceId)
                    .name(config.getDefaultResourceName())
                    .totalAmount(simulationData.getResourceCount())
                    .timetableId(config.getCustomTimetableId())
                    .build()
            ));

            resouceNameToId.put(config.getDefaultResourceName(), defaultResourceId);

        } else {

            Map<String, Double> costingData = retrieveCostData(simulationData.getLogId());
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
                        resourceId = config.getDefaultResourceIdPrefix() + config.getDefaultResourceId();
                        resourceName = config.getDefaultResourceName();
                    } else {
                        resourceId = config.getDefaultResourceIdPrefix() + UUID.randomUUID();
                        resourceName = roleToResourceCount.getKey();
                    }
                    resouceNameToId.put(resourceName, resourceId);

                    return Resource.builder()
                        .id(resourceId)
                        .name(resourceName)
                        .totalAmount(roleToResourceCount.getValue())
                        .timetableId(config.getCustomTimetableId())
                        .costPerHour(costingData.get(resourceName) == null ? 0 : costingData.get(resourceName))
                        .build();
                }).collect(Collectors.toList()));
        }

        return resouceNameToId;
    }


    private Map<String, Double> retrieveCostData(int logId) {
        Map<String, Double> roleToCostRateMap = new HashMap<>();
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<Usermetadata> userMetadata =
                new ArrayList<>(userMetadataService.getUserMetadataByLog(logId, UserMetadataTypeEnum.COST_TABLE));

            if (userMetadata.isEmpty()) {
                return roleToCostRateMap;
            }
            CostingData[] costingDataList =
                objectMapper.readValue(userMetadata.get(0).getContent(), CostingData[].class);
            if (costingDataList != null && costingDataList.length > 0
                && costingDataList[0].getCostRates() != null) {
                roleToCostRateMap = costingDataList[0].getCostRates();
            }
        } catch (JsonProcessingException ex) {
            log.warn("Error in parsing cost usermetadata for log {}", logId);
        } catch (RuntimeException ex) {
            log.warn("Error in retrieving cost data for log {}", logId);
        }
        return roleToCostRateMap;
    }

    private void deriveGatewayProbabilities(
        final ProcessSimulationInfo.ProcessSimulationInfoBuilder builder,
        final SimulationData simulationData) {

        List<SequenceFlow> sequenceFlowList = new ArrayList<>();
        if (simulationData.getEdgeFrequencies() != null && !simulationData.getEdgeFrequencies().isEmpty()) {

            simulationData.getEdgeFrequencies().entrySet().forEach(gatewayEntry -> {

                // Calculate the total outbound edge frequencies for each gateway
                double totalFrequency = gatewayEntry.getValue().stream()
                    .mapToDouble(EdgeFrequency::getFrequency)
                    .sum();

                // Set the percentage for each edge's frequency
                gatewayEntry.getValue().forEach(edgeFrequency ->
                    edgeFrequency.setPercentage(BigDecimal.valueOf(edgeFrequency.getFrequency() / totalFrequency)
                        .setScale(4, RoundingMode.HALF_UP).doubleValue()));

                // Determine if the percentages add up to a 100%
                double totalProbabilities = gatewayEntry.getValue().stream()
                    .mapToDouble(EdgeFrequency::getPercentage)
                    .sum();

                // If the total percentage is less than 100%
                // then add the difference to the gateway with the lowest percentage
                if (totalProbabilities < 1.0) {
                    EdgeFrequency minEdgeFrequency =
                        Collections.min(gatewayEntry.getValue(), Comparator.comparing(EdgeFrequency::getPercentage));

                    minEdgeFrequency.setPercentage(minEdgeFrequency.getPercentage() + (1.0 - totalProbabilities));
                }

                // If the total percentage are greater than 100%
                // then remove the difference from the gateway with the highest percentage
                if (totalProbabilities > 1.0) {
                    EdgeFrequency maxEdgeFrequency =
                        Collections.max(gatewayEntry.getValue(), Comparator.comparing(EdgeFrequency::getPercentage));

                    maxEdgeFrequency.setPercentage(maxEdgeFrequency.getPercentage() - (totalProbabilities - 1.0));
                }

                gatewayEntry.getValue().forEach(edgeFrequency -> sequenceFlowList.add(SequenceFlow.builder()
                    .elementId(edgeFrequency.getEdgeId())
                    .executionProbability(DECIMAL_FORMAT.format(edgeFrequency.getPercentage()))
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
