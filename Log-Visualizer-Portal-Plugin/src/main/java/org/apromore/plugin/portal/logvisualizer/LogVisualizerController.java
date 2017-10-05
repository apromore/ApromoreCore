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
import java.util.List;

// Third party packages
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.logvisualizer.LogVisualizerService;
import org.apromore.service.logvisualizer.impl.LogVisualizerServiceImpl;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

// Local packages


public class LogVisualizerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerController.class.getCanonicalName());

    private PortalContext portalContext;
    private LogVisualizerService logVisualizerService;

    private Window slidersWindow;

    private Textbox activitiesText;
    private Textbox arcsText;
    private Slider activities;
    private Slider arcs;

    private Combobutton frequency;
    private Menuitem absolute_frequency;
    private Menuitem max_frequency;
    private Menuitem min_frequency;

    private Combobutton duration;
//    private Menuitem absolute_duration;
//    private Menuitem max_duration;
//    private Menuitem min_duration;

    private int arcs_value;
    private int activities_value;

    private boolean frequency_vs_duration = true;
    private int avg_vs_max_vs_min = 0;

    private boolean visualized = false;
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

            this.activities = (Slider) slidersWindow.getFellow("slider1");
            this.arcs = (Slider) slidersWindow.getFellow("slider2");
            this.activitiesText = (Textbox) slidersWindow.getFellow("textbox1");
            this.arcsText = (Textbox) slidersWindow.getFellow("textbox2");

            this.frequency = (Combobutton) slidersWindow.getFellow("frequency");
            this.absolute_frequency = (Menuitem) slidersWindow.getFellow("absolute_frequency");
            this.max_frequency = (Menuitem) slidersWindow.getFellow("max_frequency");
            this.min_frequency = (Menuitem) slidersWindow.getFellow("min_frequency");

            this.duration = (Combobutton) slidersWindow.getFellow("duration");
//            this.absolute_duration = (Menuitem) slidersWindow.getFellow("absolute_duration");
//            this.max_duration = (Menuitem) slidersWindow.getFellow("max_duration");
//            this.min_duration = (Menuitem) slidersWindow.getFellow("min_duration");

            this.activities.addEventListener("onScroll", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    activitiesText.setText("" + activities.getCurpos());
                    setArcAndActivityRatios();
                }
            });

            this.arcs.addEventListener("onScroll", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    arcsText.setText("" + arcs.getCurpos());
                    setArcAndActivityRatios();
                }
            });

            this.activitiesText.addEventListener("onChange", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    int i = Integer.parseInt(activitiesText.getValue());
                    if(i < 0) i = 0;
                    else if(i > 100) i = 100;
                    activitiesText.setText("" + i);
                    activities.setCurpos(i);
                    setArcAndActivityRatios();
                }
            });
            this.activitiesText.addEventListener("onMouseOver", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    int i = Integer.parseInt(activitiesText.getValue());
                    if(i < 0) i = 0;
                    else if(i > 100) i = 100;
                    activitiesText.setText("" + i);
                    activities.setCurpos(i);
                    setArcAndActivityRatios();
                }
            });

            this.arcsText.addEventListener("onChange", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    int i = Integer.parseInt(arcsText.getValue());
                    if(i < 0) i = 0;
                    else if(i > 100) i = 100;
                    arcsText.setText("" + i);
                    arcs.setCurpos(i);
                    setArcAndActivityRatios();
                }
            });
            this.arcsText.addEventListener("onMouseOver", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    int i = Integer.parseInt(arcsText.getValue());
                    if(i < 0) i = 0;
                    else if(i > 100) i = 100;
                    arcsText.setText("" + i);
                    arcs.setCurpos(i);
                    setArcAndActivityRatios();
                }
            });

            this.frequency.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    frequency_vs_duration = LogVisualizerServiceImpl.FREQUENCY;
                    avg_vs_max_vs_min = LogVisualizerServiceImpl.AVG;
                    visualized = false;
                    setArcAndActivityRatios();
                }
            });
            this.absolute_frequency.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    frequency_vs_duration = LogVisualizerServiceImpl.FREQUENCY;
                    avg_vs_max_vs_min = LogVisualizerServiceImpl.AVG;
                    visualized = false;
                    setArcAndActivityRatios();
                }
            });
            this.max_frequency.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    frequency_vs_duration = LogVisualizerServiceImpl.FREQUENCY;
                    avg_vs_max_vs_min = LogVisualizerServiceImpl.MAX;
                    visualized = false;
                    setArcAndActivityRatios();
                }
            });
            this.min_frequency.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    frequency_vs_duration = LogVisualizerServiceImpl.FREQUENCY;
                    avg_vs_max_vs_min = LogVisualizerServiceImpl.MIN;
                    visualized = false;
                    setArcAndActivityRatios();
                }
            });

            this.duration.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    frequency_vs_duration = LogVisualizerServiceImpl.DURATION;
                    avg_vs_max_vs_min = LogVisualizerServiceImpl.AVG;
                    visualized = false;
                    setArcAndActivityRatios();
                }
            });
//            this.absolute_duration.addEventListener("onClick", new EventListener<Event>() {
//                public void onEvent(Event event) throws Exception {
//                    frequency_vs_duration = LogVisualizerServiceImpl.DURATION;
//                    avg_vs_max_vs_min = LogVisualizerServiceImpl.AVG;
//                    visualized = false;
//                    setArcAndActivityRatios();
//                }
//            });
//            this.max_duration.addEventListener("onClick", new EventListener<Event>() {
//                public void onEvent(Event event) throws Exception {
//                    frequency_vs_duration = LogVisualizerServiceImpl.DURATION;
//                    avg_vs_max_vs_min = LogVisualizerServiceImpl.MAX;
//                    visualized = false;
//                    setArcAndActivityRatios();
//                }
//            });
//            this.min_duration.addEventListener("onClick", new EventListener<Event>() {
//                public void onEvent(Event event) throws Exception {
//                    frequency_vs_duration = LogVisualizerServiceImpl.DURATION;
//                    avg_vs_max_vs_min = LogVisualizerServiceImpl.MIN;
//                    visualized = false;
//                    setArcAndActivityRatios();
//                }
//            });

            this.slidersWindow.addEventListener("onMouseOver", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    setArcAndActivityRatios();
                }
            });

            this.slidersWindow.doModal();
        } catch (IOException e) {
            context.getMessageHandler().displayError("Could not load component ", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setArcAndActivityRatios() throws InterruptedException {
        try {
            if(activities_value != activities.getCurpos() || arcs_value != arcs.getCurpos() || !visualized) {
                visualized = true;
                activities_value = activities.getCurpos();
                arcs_value = arcs.getCurpos();

                JSONArray array = logVisualizerService.generateJSONArrayFromLog(log, 1 - activities.getCurposInDouble() / 100, 1 - arcs.getCurposInDouble() / 100, frequency_vs_duration, avg_vs_max_vs_min);

                String jsonString = array.toString();
                String javascript = "load('" + jsonString + "');";
                Clients.evalJavaScript("reset()");
                Clients.evalJavaScript(javascript);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
