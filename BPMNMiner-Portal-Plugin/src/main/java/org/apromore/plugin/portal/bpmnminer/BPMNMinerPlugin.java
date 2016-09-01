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

package org.apromore.plugin.portal.bpmnminer;

// Java 2 Standard Edition packages
import java.util.Locale;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apromore.service.EventLogService;
import org.springframework.stereotype.Component;

// Local packages
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.bpmnminer.BPMNMinerService;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.ProcessService;
import org.apromore.service.helper.UserInterfaceHelper;

/**
 * A user interface to the BPMN miner service.
 */
@Component("plugin")
public class BPMNMinerPlugin extends DefaultPortalPlugin {

    private final BPMNMinerService    bpmnMinerService;
    private final CanoniserService    canoniserService;
    private final DomainService       domainService;
    private final ProcessService      processService;
    private final EventLogService     eventLogService;
    private final UserInterfaceHelper userInterfaceHelper;

    @Inject
    public BPMNMinerPlugin(final BPMNMinerService bpmnMinerService,
                           final CanoniserService canoniserService,
                           final DomainService domainService,
                           final ProcessService processService,
                           final EventLogService eventLogService,
                           final UserInterfaceHelper userInterfaceHelper) {

        this.bpmnMinerService    = bpmnMinerService;
        this.canoniserService    = canoniserService;
        this.domainService       = domainService;
        this.processService      = processService;
        this.eventLogService = eventLogService;
        this.userInterfaceHelper = userInterfaceHelper;
    }

    @Override
    public String getLabel(Locale locale) {
        return "Discover Process Model";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Discover";
    }

    @Override
    public void execute(PortalContext context) {

        context.getMessageHandler().displayInfo("Executed BPMN miner plug-in!");

        new BPMNMinerController(context, bpmnMinerService, canoniserService, domainService, processService, eventLogService, userInterfaceHelper);
    }

}
