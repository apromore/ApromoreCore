/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.plugin.processdiscoverer;

import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.loganimation.LogAnimationPluginInterface;
import org.apromore.plugin.processdiscoverer.service.ProcessDiscovererService;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.bimp_annotation.BIMPAnnotationService;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.service.helper.UserInterfaceHelper;
import org.springframework.stereotype.Component;
import javax.inject.Inject;

import static org.apromore.plugin.processdiscoverer.impl.VisualizationType.DURATION;

import java.util.Locale;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
@Component("performancePlugin")
public class ProcessDiscovererPerformanceVisualizer extends DefaultPortalPlugin {

    private String label = "Mine process performance on process map / BPMN model";
    private String groupLabel = "Analyze";

    @Inject private CanoniserService canoniserService;
    @Inject private DomainService domainService;
    @Inject private ProcessService processService;
    @Inject private BPMNDiagramImporter importerService;
    @Inject private UserInterfaceHelper userInterfaceHelper;
    @Inject private EventLogService eventLogService;
    @Inject private ProcessDiscovererService processDiscovererService;
    @Inject private LogAnimationPluginInterface logAnimationPluginInterface;
    @Inject private BIMPAnnotationService bimpAnnotationService;

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
        try {
            new ProcessDiscovererController(context, eventLogService, processDiscovererService,
                    canoniserService, domainService, processService, importerService,
                    userInterfaceHelper, logAnimationPluginInterface, DURATION, bimpAnnotationService);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
