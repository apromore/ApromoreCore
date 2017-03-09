/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

package org.apromore.plugin.portal.stagemining;

// Java 2 Standard Edition packages
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;

// Third party packages
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.stagemining.StageMiningService;
import org.deckfour.xes.model.XLog;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Messagebox;

// Local packages
/*
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.ProcessService;
*/

/**
 * A user interface to the process drift detection service.
 */
@Component("plugin")
public class StageMiningPlugin extends DefaultPortalPlugin {

    private final StageMiningService stageMiningService;
    private final EventLogService eventLogService;
    /*
    private final CanoniserService canoniserService;
    private final DomainService    domainService;
    private final ProcessService   processService;
    */

    @Inject
    public StageMiningPlugin(final StageMiningService stageMiningService, final EventLogService eventLogService) {

        this.stageMiningService = stageMiningService;
        this.eventLogService = eventLogService;
    }

    @Override
    public String getLabel(Locale locale) {
        return "Mine Process Stages";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Analyze";
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

        portalContext.getMessageHandler().displayInfo("Executed stage mining plug-in!");

        try {
            new StageMiningController(portalContext, this.stageMiningService, logs);
        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
