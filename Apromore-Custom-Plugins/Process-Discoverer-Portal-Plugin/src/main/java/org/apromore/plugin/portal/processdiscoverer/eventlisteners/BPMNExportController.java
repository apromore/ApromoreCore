/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.processdiscoverer.eventlisteners;

import static org.apromore.commons.item.Constants.HOME_FOLDER_NAME;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.components.AbstractController;
import org.apromore.plugin.portal.processdiscoverer.utils.InputDialog;
import org.apromore.plugin.portal.processdiscoverer.vis.InvalidOutputException;
import org.apromore.portal.helper.Version;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.plugins.bpmn.BpmnDefinitions;
import org.apromore.processsimulation.dto.SimulationData;
import org.apromore.processsimulation.model.ProcessSimulationInfo;
import org.apromore.processsimulation.service.SimulationInfoService;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Window;

/**
 * This class provides a facility to export BPMN models from ProcessDiscoverer and save
 * the models to the portal.
 * It shows a progress window with progress bar indicating the completion percent for long-running task
 * To do that, it uses an EventQueue at the session scope where other parties can post events to
 * or listen to events during this process.
 * Note that instances of this class can only be used once at a time, i.e. one instance is used
 * and then finished, then another instance is used and then finished.
 * If multiple instances concurrently exist and run, it may happen that one instance may respond
 * to events occuring in another instance.
 *
 * @author Simon Rabozi
 * @modified Bruce Nguyen
 */
@Slf4j
public class BPMNExportController extends AbstractController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(PDController.class);
    private static final String EVENT_QUEUE = BPMNExportController.class.getCanonicalName();
    private static final String CHANGE_DESCRIPTION = "CHANGE_DESCRIPTION";
    private static final String CHANGE_FRACTION_COMPLETE = "CHANGE_FRACTION_COMPLETE";
    private static final String MINING_COMPLETE = "MINING_COMPLETE";
    private static final String MINING_EXCEPTION = "MINING_EXCEPTION";
    private static final String ANNOTATION_EXCEPTION = "ANNOTATION_EXCEPTION";
    private static final String MESSAGE_BOX_TITLE = "Apromore";

    private EventQueue<Event> eventQueue;

    private PDController controller;
    private String minedModel = null;

    //Progress window
    private Window window;
    private Label descriptionLabel;
    private Progressmeter fractionCompleteProgressmeter;
    private ProgressEventListener progressListener;
    private boolean showProgressBar;

    private SimulationInfoService simulationInfoService;

    public BPMNExportController(PDController controller, boolean showProgressBar) {
        super(controller);
        this.controller = controller;
        this.showProgressBar = showProgressBar;
        this.progressListener = new ProgressEventListener();
        eventQueue = EventQueues.lookup(EVENT_QUEUE, EventQueues.SESSION, true);

        this.simulationInfoService = (SimulationInfoService) SpringUtil.getBean("simulationInfoService");
    }

    /*
     * Note: forget to unsubscribe may lead to unexpected behavior
     * if multiple ExportBPMNHander objects exist.
     */
    class ProgressEventListener implements EventListener<Event> {
        @Override
        public void onEvent(Event event) throws Exception {
            if (!parent.prepareCriticalServices()) {
                return;
            }

            switch (event.getName()) {
                case CHANGE_DESCRIPTION:
                    if (descriptionLabel != null) {
                        descriptionLabel.setValue((String) event.getData());
                    }
                    break;

                case CHANGE_FRACTION_COMPLETE:
                    if (fractionCompleteProgressmeter != null) {
                        fractionCompleteProgressmeter.setValue((int) Math.round(100.0 * (Double) event.getData()));
                    }
                    break;

                case MINING_COMPLETE:
                    if (fractionCompleteProgressmeter != null) {
                        fractionCompleteProgressmeter.setValue(100);
                    }
                    if (descriptionLabel != null) {
                        descriptionLabel.setValue(parent.getLabel("savingBPMN_message"));
                    }
                    try {
                        BPMNExportController.this.save();
                        if (window != null) {
                            window.detach();
                        }
                        eventQueue.unsubscribe(this); //unsubscribe after finishing work
                    } catch (Exception exception) {
                        log.error(parent.getLabel("Process mining failed"), exception);
                        Messagebox.show(parent.getLabel("failedProcessMining_message"), MESSAGE_BOX_TITLE,
                            Messagebox.OK,
                            Messagebox.ERROR);
                        eventQueue.unsubscribe(this); //unsubscribe after finishing work even fails
                    }
                    break;

                case ANNOTATION_EXCEPTION:
                    log.error(parent.getLabel("Unable to annotate BPMN model"), (Exception) event.getData());
                    Messagebox.show(parent.getLabel("failedAnnotateBPMN_message"), MESSAGE_BOX_TITLE, Messagebox.OK,
                        Messagebox.EXCLAMATION);
                    eventQueue.unsubscribe(this); //unsubscribe after finishing work even fails
                    break;

                case MINING_EXCEPTION:
                default:
                    log.error("Process mining failed", (Exception) event.getData());
                    if (window != null) {
                        window.detach();
                    }
                    Messagebox.show(parent.getLabel("failedProcessMining_message"), MESSAGE_BOX_TITLE, Messagebox.OK,
                        Messagebox.ERROR);
                    eventQueue.unsubscribe(this); //unsubscribe after finishing work even fails
                    break;
            }
        }
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (this.showProgressBar) {
            window = (Window) Executions.createComponents("mineAndSave.zul", null, null);
            window.getFellow("cancel").addEventListener("onClick", event1 -> window.detach());

            descriptionLabel = (Label) window.getFellow("description");
            fractionCompleteProgressmeter = (Progressmeter) window.getFellow("fractionComplete");
            window.doModal();
        }

        eventQueue.subscribe(progressListener); // subscribe to start listening to events

        new Thread() {
            @Override
            public void run() {
                try {
                    mine();
                    eventQueue.publish(new Event(MINING_COMPLETE, null, null));

                } catch (Exception e) {
                    e.printStackTrace();
                    eventQueue.publish(new Event(MINING_EXCEPTION, null, e));
                }
            }
        }.start();
    }

    private void mine() throws Exception {
        if (controller.getOutputData() == null) {
            throw new InvalidOutputException("Output data is not available yet!");
        }
        Abstraction abs = controller.getOutputData().getAbstraction();
        if (abs.getLayout() == null) {
            throw new InvalidOutputException("Missing layout of the process map for exporting BPMN diagram.");
        }

        // Prepare diagram for export
        BPMNDiagram d = abs.getValidBPMNDiagram();
        BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder;
        Map<ContainableDirectedGraphElement, String> labelMapping;
        labelMapping = cleanDiagramBeforeExport(d);
        if (!controller.getUserOptions().getBPMNMode()) {
            definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(d); // recreate layout
        } else {
            definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(d, abs.getLayout().getGraphLayout());
        }

        // Export to text
        BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

        minedModel = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n "
            + "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n "
            + "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n "
            + "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n "
            + "targetNamespace=\"http://www.omg.org/bpmn20\"\n "
            + "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">")
            + definitions.exportElements()
            + "</definitions>";

        restoreDiagramAfterExport(d, labelMapping);
    }

    private Map<ContainableDirectedGraphElement, String> cleanDiagramBeforeExport(BPMNDiagram d) {
        Map<ContainableDirectedGraphElement, String> labelMapping = new HashMap<>();

        for (BPMNEdge edge : d.getEdges()) {
            labelMapping.put(edge, edge.getLabel());
            edge.setLabel("");
        }

        for (org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event event1 : d.getEvents()) {
            labelMapping.put(event1, event1.getLabel());
            event1.getAttributeMap().put("ProM_Vis_attr_label", ""); // there is no event1.setLabel().
        }
        return labelMapping;
    }

    private void restoreDiagramAfterExport(BPMNDiagram d, Map<ContainableDirectedGraphElement, String> labelMapping) {
        for (ContainableDirectedGraphElement ele : labelMapping.keySet()) {
            if (ele instanceof BPMNEdge) {
                ((BPMNEdge) ele).setLabel(labelMapping.get(ele));
            } else if (ele instanceof org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event) {
                ele.getAttributeMap().put("ProM_Vis_attr_label", labelMapping.get(ele));
            }
        }
    }

    private void save() throws Exception {
        String defaultProcessName = null;
        if (parent.getContextData().getLogName() != null) {
            defaultProcessName = parent.getContextData().getLogName().split("\\.")[0];
        }

        String conditionText = null;
        String footnoteMessage = null;
        if (simulationInfoService.isFeatureEnabled()) {
            conditionText = parent.getLabel("includeSimulationParams_text");
        } else {
            footnoteMessage = parent.getLabel("warnNoSimulationParams_text");
            log.warn(
                "Exporting simulation info feature is disabled. Model will be exported without simulation parameters");
        }

        InputDialog.showInputDialog(
            parent.getLabel("saveBPMN_message"),
            parent.getLabel("saveBPMNName_message"),
            defaultProcessName,
            conditionText,
            footnoteMessage,
            event -> {
                if (event.getName().equals("onOK") || event.getName().equals("onOKChecked")) {
                    if (event.getName().equals("onOKChecked")) {

                        SimulationData simulationData = controller.getProcessAnalyst()
                            .getSimulationData(controller.getOutputData().getAbstraction());

                        // Derive process simulation information
                        ProcessSimulationInfo simulationInfo =
                            simulationInfoService.transformToSimulationInfo(simulationData);

                        // Enrich the exported bpmn with process simulation info
                        minedModel = simulationInfoService.enrichWithSimulationInfo(minedModel, simulationInfo);
                    }

                    String modelName = (String) event.getData();
                    String user = controller.getContextData().getUsername();
                    Version version = new Version(1, 0);
                    String now =
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString();
                    boolean publicModel = false;

                    try {
                        ProcessModelVersion pmv = controller.getProcessService().importProcess(user,
                            controller.getContextData().getFolderId(),
                            modelName,
                            version,
                            "BPMN 2.0",
                            new ByteArrayInputStream(minedModel.getBytes()),
                            "",
                            "Model generated by the Apromore BPMN process mining service.",
                            now,  // creation timestamp
                            now,  // last update timestamp
                            publicModel);
                        Folder folder = pmv.getProcessBranch().getProcess().getFolder();
                        String folderName = folder == null ? HOME_FOLDER_NAME : folder.getName();
                        String notif = MessageFormat.format(
                            parent.getLabel("successSaveBPMN_message"),
                            "<strong>" + modelName + "</strong>",
                            "<strong>" + folderName + "</strong>"
                        );
                        Notification.info(notif);
                        controller.refreshPortal();
                    } catch (Exception ex) {
                        Messagebox.show(
                            parent.getLabel("failedSaveModel_message")
                        );
                        LOGGER.error("Error in saving model: ", ex);
                    }
                }
            });
    }
}
