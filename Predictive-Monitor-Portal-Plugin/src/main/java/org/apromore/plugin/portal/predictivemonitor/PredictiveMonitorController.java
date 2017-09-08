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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Third party packages
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Iframe;
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
 * In MVC terms, this is a controller whose corresponding model is {@link Dataflow} and corresponding view is <code>setup.zul</code>.
 */
public class PredictiveMonitorController {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictiveMonitorController.class.getCanonicalName());

    private static Dataflow dataflow = null;

    private final Iframe iframe;
    private final Button createDataflowButton;
    private final Button deleteDataflowButton;
    private final Button streamLogButton;


    private Dataflow getDataflow() {
        return dataflow;
    }

    private void setDataflow(Dataflow newDataflow) {
        dataflow = newDataflow;
        updateUI();
        if (dataflow != null) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {}
        }
        iframe.setSrc(getDataflow() == null ? "about:blank" : "http://localhost:8080");
    }

    private void updateUI() {
        createDataflowButton.setDisabled(getDataflow() != null);
        deleteDataflowButton.setDisabled(getDataflow() == null);
        streamLogButton.setDisabled(getDataflow() == null);
    }

    public PredictiveMonitorController(PortalContext portalContext, EventLogService eventLogService, File nirdizatiPath) throws IOException {

        Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/setup.zul", null, null);

        iframe               = (Iframe) window.getFellow("iframe");
        createDataflowButton = (Button) window.getFellow("createDataflow");
        deleteDataflowButton = (Button) window.getFellow("deleteDataflow");
        streamLogButton      = (Button) window.getFellow("streamLog");
        updateUI();
        iframe.setSrc(getDataflow() == null ? "about:blank" : "http://localhost:8080");

        // Find the selected log
        Set<LogSummaryType> logSummaries = findSelectedLogs(portalContext);
        if (logSummaries.size() != 1) {
            Messagebox.show("Select exactly one log", "Attention", Messagebox.OK, Messagebox.ERROR);
            return;
        }
        LogSummaryType logSummary = logSummaries.iterator().next();
        XLog log = eventLogService.getXLog(logSummary.getId());

        // Present the setup panel

        createDataflowButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                setDataflow(new Dataflow("bpi_12", "bpi12", nirdizatiPath));
            }
        });

        deleteDataflowButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                if (getDataflow() != null) {
                    getDataflow().close();
                    setDataflow(null);
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
}
