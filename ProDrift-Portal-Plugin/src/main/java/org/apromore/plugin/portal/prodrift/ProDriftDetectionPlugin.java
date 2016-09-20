/*
 * Copyright Â© 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.prodrift;

// Java 2 Standard Edition packages
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apromore.dao.LogRepository;
import org.apromore.dao.model.Log;
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.prodrift.ProDriftDetectionService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Messagebox;

/**
 * A user interface to the process drift detection service.
 */
@Component("plugin")
public class ProDriftDetectionPlugin extends DefaultPortalPlugin {

    private final ProDriftDetectionService proDriftDetectionService;
    private final EventLogService eventLogService;

    private String label = "Detect Process Drifts";
    private String groupLabel = "Analyze";

    @Inject
    public ProDriftDetectionPlugin(final ProDriftDetectionService proDriftDetectionService, final EventLogService eventLogService) {
        this.proDriftDetectionService = proDriftDetectionService;
        this.eventLogService = eventLogService;
    }

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

    @Override
    public void execute(PortalContext portalContext) {

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet())
        {
            if(entry.getKey() instanceof LogSummaryType)
            {
                selectedLogSummaryType.add((LogSummaryType) entry.getKey());
            }
        }

        Map<XLog, String> logs = new HashMap<>();
        for(LogSummaryType logType : selectedLogSummaryType)
        {
            logs.put(eventLogService.getXLog(logType.getId()), logType.getName());
        }


        portalContext.getMessageHandler().displayInfo("Executed process drift detection plug-in!");

        try {

            new ProDriftController(portalContext, this.proDriftDetectionService, logs);
        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }



}
