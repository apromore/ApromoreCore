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
import org.deckfour.xes.model.XAttribute;
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
 * In MVC terms, this is a controller whose corresponding model is {@link Dataflow} and corresponding view is <code>setup.zul</code>.
 */
public class PredictorsController {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictorsController.class.getCanonicalName());

    private final Window  window;

    private final Listbox predictorsListbox;
    private final Button  createPredictorButton;
    private final Button  deletePredictorButton;

    public PredictorsController(PortalContext portalContext, EventLogService eventLogService, String kafkaHost, File nirdizatiPath, String pythonPath) throws IOException {

        window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/predictors.zul", null, null);

        predictorsListbox     = (Listbox) window.getFellow("predictors");
        createPredictorButton = (Button) window.getFellow("createPredictor");
        deletePredictorButton = (Button) window.getFellow("deletePredictor");

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
                new CreatePredictorController(portalContext, Persistent.predictors, eventLogService, nirdizatiPath, pythonPath);
            }
        });

        deletePredictorButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                for (Predictor predictor: Persistent.predictors.getSelection()) {
                    predictor.delete();
                }
                Persistent.predictors.removeAll(Persistent.predictors.getSelection());
            }
        });

        if (Persistent.predictors == null) {
            Persistent.initPredictors(nirdizatiPath, pythonPath);
        }
        assert Persistent.predictors != null;

        predictorsListbox.setModel(Persistent.predictors);

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
}
