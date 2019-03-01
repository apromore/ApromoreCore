/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.loganimation2.LogAnimationPluginInterface;
import org.apromore.service.CanoniserService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.logvisualizer.LogVisualizerService;
import org.apromore.service.logvisualizer.impl.LogVisualizerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Locale;

// Java 2 Enterprise Edition packages
// Third party packages
// Local packages

@Component("performancePlugin")
public class DiscoverProcessPerformanceProcessMapPlugin extends DefaultPortalPlugin {

    private String label = "Mine process performance on process map";
    private String groupLabel = "Analyze";

    @Inject private EventLogService eventLogService;
    @Inject private LogVisualizerService logVisualizerService;

    @Inject private ProcessService processService;
    @Inject private CanoniserService canoniserService;
    @Inject private UserInterfaceHelper userInterfaceHelper;

    @Inject private LogAnimationPluginInterface logAnimationPluginInterface;

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverProcessPerformanceProcessMapPlugin.class.getCanonicalName());

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
    public void execute(PortalContext context) {
        LOGGER.info("Executing");

        new DiscoverProcessMapController(context, eventLogService, logVisualizerService, processService, canoniserService, userInterfaceHelper, logAnimationPluginInterface, LogVisualizerServiceImpl.DURATION);


    }
}
