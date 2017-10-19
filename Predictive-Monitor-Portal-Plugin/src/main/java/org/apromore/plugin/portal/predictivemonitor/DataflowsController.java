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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import static java.util.concurrent.TimeUnit.SECONDS;

// Third party packages
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
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
import org.zkoss.zul.Textbox;
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
public class DataflowsController {

    private static Logger LOGGER = LoggerFactory.getLogger(DataflowsController.class.getCanonicalName());

    private final Window  window;

    private final Listbox dataflowsListbox;
    private final Button  createDataflowButton;
    private final Button  deleteDataflowButton;
    private final Button  streamLogButton;
    private final Button  showDashboardButton;

    public DataflowsController(PortalContext portalContext, EventLogService eventLogService, String kafkaHost, File nirdizatiPath, String pythonPath) throws IOException {

        window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/dataflows.zul", null, null);

        dataflowsListbox      = (Listbox) window.getFellow("dataflows");
        createDataflowButton  = (Button) window.getFellow("createDataflow");
        deleteDataflowButton  = (Button) window.getFellow("deleteDataflow");
        streamLogButton       = (Button) window.getFellow("streamLog");
        showDashboardButton   = (Button) window.getFellow("showDashboard");

        // Find the selected log
        Set<LogSummaryType> logSummaries = findSelectedLogs(portalContext);
        if (logSummaries.size() != 1) {
            Messagebox.show("Select exactly one log", "Attention", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        LogSummaryType logSummary = logSummaries.iterator().next();
        XLog log = eventLogService.getXLog(logSummary.getId());

        // Bind window components
        createDataflowButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                new CreateDataflowController(portalContext, eventLogService, kafkaHost, nirdizatiPath, pythonPath);
            }
        });

        deleteDataflowButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                List<Dataflow> selectedDataflows = new LinkedList<>();
                for (Dataflow dataflow: Persistent.dataflows) {
                    if (Persistent.dataflows.isSelected(dataflow)) {
                        dataflow.close();
                        selectedDataflows.add(dataflow);
                    }
                }
                Persistent.dataflows.removeAll(selectedDataflows);
            }
        });

        streamLogButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                for (Dataflow dataflow: Persistent.dataflows) {
                    if (Persistent.dataflows.isSelected(dataflow)) {
                        dataflow.exportLog(log);
                    }
                }
            }
        });

        showDashboardButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                for (Dataflow dataflow: Persistent.dataflows) {
                    if (Persistent.dataflows.isSelected(dataflow)) {
                        Desktop desktop = window.getDesktop();
                        window.detach();
                        new PredictiveMonitorController(portalContext, dataflow);
                        return;
                    }
                }
            }
        });

        dataflowsListbox.setModel(Persistent.dataflows);

        window.doModal();
    }

    static Set<LogSummaryType> findSelectedLogs(PortalContext context) {
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

    private static void export(XLog log, File file) throws FileNotFoundException {
        LOGGER.info("Exporting log to " + file);
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("case_id,Resource,AMOUNT_REQ,proctime,time,elapsed,activity_name,label,event_nr,last,remtime");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            for (XTrace trace: log) {
                XEvent lastEvent = trace.get(trace.size() - 1);
                Date lastTime = ((XAttributeTimestamp) lastEvent.getAttributes().get("time:timestamp")).getValue();
                for (XEvent event: trace) {
                    Date time = ((XAttributeTimestamp) event.getAttributes().get("time:timestamp")).getValue();
                    writer.println(
                        trace.getAttributes().get("concept:name") + "," +
                        event.getAttributes().get("Resource") + "," +
                        event.getAttributes().get("AMOUNT_REQ") + "," +
                        event.getAttributes().get("proctime") + "," +
                        dateFormat.format(time) + "," +
                        event.getAttributes().get("elapsed") + "," +
                        event.getAttributes().get("activity_name") + "," +
                        event.getAttributes().get("label") + "," +
                        event.getAttributes().get("event_nr") + "," +
                        event.getAttributes().get("last") + "," +
                        Long.toString((lastTime.getTime() - time.getTime()) / 1000)
                    );
                }
            }
        }
        LOGGER.info("Exported log to " + file);
    }
}
