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

// Java 2 Standard Edition packages
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apromore.plugin.portal.stagemining.Visualization_cytoscape;
import org.apromore.service.EventLogService;
import org.apromore.service.logvisualizer.LogVisualizerService;
import org.json.JSONException;
import org.processmining.stagemining.models.DecompositionTree;
import org.processmining.stagemining.models.graph.WeightedDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.model.SummaryType;
import org.apromore.model.LogSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;

@Component("plugin")
public class LogVisualizerPlugin extends DefaultPortalPlugin {

    private String label = "Visualize Log";
    private String groupLabel = "Discover";

    @Inject private EventLogService eventLogService;
    @Inject private LogVisualizerService logVisualizerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerPlugin.class.getCanonicalName());

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

//    @Override
//    public void execute(PortalContext context) {
//        LOGGER.info("Executing");
//
//        Map<SummaryType, List<VersionSummaryType>> elements = context.getSelection().getSelectedProcessModelVersions();
//        if (elements.size() != 1) {
//            Messagebox.show("Please, select exactly one log.", "Wrong Log Selection", Messagebox.OK, Messagebox.INFORMATION);
//            return;
//        }
//        SummaryType summary = elements.keySet().iterator().next();
//        if (!(summary instanceof LogSummaryType)) {
//            Messagebox.show("Please, select exactly one log.", "Wrong Log Selection", Messagebox.OK, Messagebox.INFORMATION);
//            return;
//        }
//        LogSummaryType logSummary = (LogSummaryType) summary;
//
//        try {
//            Window window = (Window) context.getUI().createComponent(getClass().getClassLoader(), "zul/logvisualizer.zul", null, null);
//
//            window.setAttribute("logVisualizerService", logVisualizerService);
//            window.setAttribute("logId", logSummary.getId());
//            window.setAttribute("log", eventLogService.getXLog(logSummary.getId()));
//            window.doModal();
//        } catch (IOException e) {
//            context.getMessageHandler().displayError("Could not load component ", e);
//        }
//    }

    @Override
    public void execute(PortalContext context) {
        LOGGER.info("Executing");

        new LogVisualizerController(context, eventLogService, logVisualizerService);


    }
}
