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

package org.apromore.plugin.portal.logvisualizer;

// Java 2 Standard Edition
import java.io.IOException;
import java.util.*;

// Third party packages
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.logvisualizer.LogVisualizerService;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

// Local packages


public class LogVisualizerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerController.class.getCanonicalName());

    private PortalContext portalContext;
    private LogVisualizerService logVisualizerService;

    private Window slidersWindow;
    private Button cancelButton;
    private Button okButton;

    private XLog log;

    public LogVisualizerController(PortalContext context, EventLogService eventLogService, LogVisualizerService logVisualizerService) {

        this.portalContext          = context;
        this.logVisualizerService   = logVisualizerService;

        Map<SummaryType, List<VersionSummaryType>> elements = context.getSelection().getSelectedProcessModelVersions();
        if (elements.size() != 1) {
            Messagebox.show("Please, select exactly one log.", "Wrong Log Selection", Messagebox.OK, Messagebox.INFORMATION);
            return;
        }
        SummaryType summary = elements.keySet().iterator().next();
        if (!(summary instanceof LogSummaryType)) {
            Messagebox.show("Please, select exactly one log.", "Wrong Log Selection", Messagebox.OK, Messagebox.INFORMATION);
            return;
        }
        LogSummaryType logSummary = (LogSummaryType) summary;
        log = eventLogService.getXLog(logSummary.getId());

        try {
            this.slidersWindow = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/logvisualizer.zul", null, null);
            this.cancelButton = (Button) slidersWindow.getFellow("cancelButton");
            this.okButton = (Button) slidersWindow.getFellow("okButton");

            this.cancelButton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            this.okButton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    setArcAndActivityRatios();
                }
            });

            slidersWindow.doModal();
//            setArcAndActivityRatios();
        } catch (IOException e) {
            context.getMessageHandler().displayError("Could not load component ", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() throws InterruptedException {
        slidersWindow.detach();
    }

    public void setArcAndActivityRatios() throws InterruptedException {
        Slider activities = (Slider) slidersWindow.getFellow("slider1");
        Slider arcs = (Slider) slidersWindow.getFellow("slider2");

        try {
            JSONArray array = logVisualizerService.generateJSONArrayFromLog(log, 1 - activities.getCurposInDouble() / 100, 1 - arcs.getCurposInDouble() / 100);

            String jsonString = array.toString();
            String javascript = "load('" + jsonString + "');";
            System.out.println(javascript);
            Clients.evalJavaScript("reset()");
            Clients.evalJavaScript(javascript);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
