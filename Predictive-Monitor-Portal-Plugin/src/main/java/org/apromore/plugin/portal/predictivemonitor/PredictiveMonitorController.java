/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Edition
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Third party packages
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;

/**
 * In MVC terms, this is a controller whose corresponding model is {@link Dataflow} and corresponding view is <code>predictive_monitor.zul</code>.
 */
public class PredictiveMonitorController implements EventListener<DataflowEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictiveMonitorController.class.getCanonicalName());

    /** This is static only because I've been too lazy to implement proper persistence for it yet. */
    private static Dataflow dataflow = null;

    private Execution execution = Executions.getCurrent();
    private final Window  window;
    private final Listbox eventsListbox;
    //private final Listitem eventListitem;

    private final Button  createPredictorButton;
    private final Button  createDataflowButton;
    private final Button  deleteDataflowButton;
    private final Button  streamLogButton;

    private final Label   runningCasesLabel;
    private final Label   completedCasesLabel;
    private final Label   completedEventsLabel;
    private final Label   averageCaseLengthLabel;
    private final Label   averageCaseDurationLabel;

    private final NumberFormat numberFormat = new DecimalFormat("0.##");

    private final List<Predictor> predictors = new LinkedList<>();

    public PredictiveMonitorController(PortalContext portalContext, EventLogService eventLogService, String kafkaHost, File nirdizatiPath, String pythonPath) throws IOException {

        window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/predictive_monitor.zul", null, null);
        eventsListbox = (Listbox) window.getFellow("events");
        //eventListitem = (Listitem) window.getFellow("event");

        createPredictorButton = (Button) window.getFellow("createPredictor");
        createDataflowButton  = (Button) window.getFellow("createDataflow");
        deleteDataflowButton  = (Button) window.getFellow("deleteDataflow");
        streamLogButton       = (Button) window.getFellow("streamLog");

        runningCasesLabel        = (Label) window.getFellow("runningCases");
        completedCasesLabel      = (Label) window.getFellow("completedCases");
        completedEventsLabel     = (Label) window.getFellow("completedEvents");
        averageCaseLengthLabel   = (Label) window.getFellow("averageCaseLength");
        averageCaseDurationLabel = (Label) window.getFellow("averageCaseDuration");
        
        updateUI();

        // Find the selected log
        Set<LogSummaryType> logSummaries = findSelectedLogs(portalContext);
        if (logSummaries.size() != 1) {
            Messagebox.show("Select exactly one log", "Attention", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        LogSummaryType logSummary = logSummaries.iterator().next();
        XLog log = eventLogService.getXLog(logSummary.getId());

        // Bind window components
        createPredictorButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                LOGGER.info("Create predictors");
                predictors.add(new CaseOutcomePredictor("Slow?", "label", "slow_probability"));
                //predictors.add(new CaseOutcomePredictor("Rejected?", "label2", "rejected_probability"));
                predictors.add(new RemainingTimePredictor());
            }
        });

        createDataflowButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                setDataflow(new Dataflow("bpi_12", "bpi12", kafkaHost, nirdizatiPath, pythonPath, window.getDesktop(), PredictiveMonitorController.this, predictors));
            }
        });

        deleteDataflowButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                if (getDataflow() != null) {
                    getDataflow().close();
                    setDataflow(null);
                    predictors.clear();
                } else {
                    Messagebox.show("No dataflow to delete.", "Attention", Messagebox.OK, Messagebox.ERROR);
                }
            }
        });

        streamLogButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                if (getDataflow() != null) {
                    getDataflow().exportLog(log);
                } else {
                    Messagebox.show("Cannot export log because dataflow has not been created yet.", "Attention", Messagebox.OK, Messagebox.ERROR);
                }
            }
        });

        window.doModal();
    }

    private Dataflow getDataflow() {
        return dataflow;
    }

    private void setDataflow(Dataflow newDataflow) {

        // TODO: remove any existing dynamic columns

        dataflow = newDataflow;
        updateUI();
        if (dataflow != null) {
            //eventsListbox.getListhead().appendChild(new Listheader("Slow"));
            for (Predictor predictor: predictors) {
                predictor.addHeaders(eventsListbox.getListhead());
            }
            //eventListitem.appendChild(new Listcell("Dummy"));
            eventsListbox.setRows(15);  // TODO: figure out how to make vflex work with this
            eventsListbox.setItemRenderer(new ListitemRenderer<DataflowEvent> () {
                public void render(Listitem item, DataflowEvent event, int index) {
                    item.setStyle(event.isLast() ? "background-color: #EEFFEE" : "");
                    item.appendChild(new Listcell(event.getCaseId()));
                    item.appendChild(new Listcell(event.isLast() ? "Yes" : "No"));
                    item.appendChild(new Listcell(Integer.toString(event.getIndex())));
                    item.appendChild(new Listcell(formatTime(event.getStartTime())));
                    item.appendChild(new Listcell(formatTime(event.getTime())));
                    item.appendChild(new Listcell(formatTime(event.getEndTime())));
                    item.appendChild(new Listcell(event.getFormattedDuration()));

                    // Populate the columns added by predictors
                    for (Predictor predictor: predictors) {
                        predictor.addCells(item, event);
                    }
                }

                final DateFormat dataFormat = new SimpleDateFormat("yyyy-MMM-dd h:mm:ss a");

                private String formatTime(Date date) {
                    return date == null ? "" : dataFormat.format(date);
                }
            });
            eventsListbox.setModel(dataflow.eventsModel);
        }
    }

    private void updateUI() {
        createPredictorButton.setDisabled(getDataflow() != null);
        createDataflowButton.setDisabled(getDataflow() != null);
        deleteDataflowButton.setDisabled(getDataflow() == null);
        streamLogButton.setDisabled(getDataflow() == null);

        Dataflow dataflow = getDataflow();
        if (dataflow != null) {
            runningCasesLabel.setValue(Integer.toString(dataflow.caseCount - dataflow.completedCaseCount));
            completedEventsLabel.setValue(Integer.toString(dataflow.completedEventCount));
            completedCasesLabel.setValue(Integer.toString(dataflow.completedCaseCount));
            if (dataflow.completedCaseCount > 0) {
                averageCaseLengthLabel.setValue(numberFormat.format((dataflow.completedCaseEventCount / (double) dataflow.completedCaseCount)));
                averageCaseDurationLabel.setValue(DataflowEvent.format(dataflow.totalCompletedCaseDuration.dividedBy(dataflow.completedCaseCount)));
            }
        }
    }

    private static Set<LogSummaryType> findSelectedLogs(PortalContext context) {
        Map<SummaryType, List<VersionSummaryType>> elements = context.getSelection().getSelectedProcessModelVersions();
        Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
        Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions = new HashMap<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof LogSummaryType) {
                selectedLogSummaryType.add((LogSummaryType) entry.getKey());
            }
        }
        return selectedLogSummaryType;
    }

    // Implementation of EventListener<DataflowEvent>

    public void onEvent(DataflowEvent event) {
        if (dataflow != null) {
            dataflow.eventsModel.add(0, event);
        }
        updateUI();
    }
}
