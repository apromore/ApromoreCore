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
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.processsimulation.model.Currency;
import org.apromore.processsimulation.model.Errors;
import org.apromore.processsimulation.model.ExtensionElements;
import org.apromore.processsimulation.model.ProcessSimulationInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.Instant;
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

    public ProcessSimulationInfo deriveSimulationInfo(final AttributeLog attributeLog) {

        ProcessSimulationInfo processSimulationInfo = null;
        if(attributeLog != null && attributeLog.getLogSummary() != null){
            AttributeLogSummary logSummary = attributeLog.getLogSummary();

            processSimulationInfo =
                    ProcessSimulationInfo.builder()
                            .id("qbp_de" + UUID.randomUUID())
                            .processInstances(logSummary.getCaseCount())
                            .currency(Currency.EUR.toString())
                            .startDateTime(Instant.ofEpochMilli(logSummary.getStartTime()).toString())
                            .errors(Errors.builder().build())
                            .build();
        }

        return processSimulationInfo;
    }

    /**
     * Enrich the bpmn xml model with the additional extension elements
     * including the process simulation information that was derived from the
     * bpmn model
     *
     * @param bpmnModelXml the discovered bpmn model
     * @param processSimulationInfo the process simulation information derived from the discovered model
     *
     * @return a bpmn model enriched with the additional process simulation information set in the extensionElements
     */
    public String enrichWithSimulationInfo(
            final String bpmnModelXml, final ProcessSimulationInfo processSimulationInfo) {

        String enrichedBpmnXml = bpmnModelXml;
        if(processSimulationInfo != null) {

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

        if(extensionElementsXml != null && !extensionElementsXml.isEmpty()){
            // remove the <?xml> tags if present from the extension elements xml
            if (extensionElementsXml.contains(XML_START_TAG)) {
                extensionElementsXml = extensionElementsXml
                        .substring(extensionElementsXml.indexOf(XML_START_EXTENSION_ELEMENTS_TAG));
            }

            // inject the qbp namespace and the extension elements
            if(enrichedBpmnXml.indexOf(XML_START_DEFINITIONS_TAG) > 0){
                enrichedBpmnXml.insert(
                        exportedBpmnXml.indexOf(XML_START_DEFINITIONS_TAG) + XML_START_DEFINITIONS_TAG.length(),
                        XML_QBP_NAMESPACE);

            } else if(enrichedBpmnXml.indexOf(XML_START_BPMN_DEFINITIONS_TAG) > 0){
                enrichedBpmnXml.insert(
                        exportedBpmnXml.indexOf(XML_START_BPMN_DEFINITIONS_TAG) + XML_START_BPMN_DEFINITIONS_TAG.length(),
                        XML_QBP_NAMESPACE);
            }


            if(enrichedBpmnXml.indexOf(XML_END_PROCESS_TAG) > 0){
                enrichedBpmnXml.insert(enrichedBpmnXml.indexOf(XML_END_PROCESS_TAG), extensionElementsXml);

            } else if(enrichedBpmnXml.indexOf(XML_END_BPMN_PROCESS_TAG) > 0){
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
