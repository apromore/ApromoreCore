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

package org.apromore.plugin.portal.perfmining;

// Java 2 Standard Edition packages
import java.io.IOException;
import java.util.Locale;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.perfmining.PerfMiningService;
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
public class PerfMiningPlugin extends DefaultPortalPlugin {

    private final PerfMiningService perfMiningService;
    /*
    private final CanoniserService canoniserService;
    private final DomainService    domainService;
    private final ProcessService   processService;
    */

    @Inject
    public PerfMiningPlugin(final PerfMiningService perfMiningService /*,
                           final CanoniserService canoniserService,
                           final DomainService    domainService,
                           final ProcessService   processService */) {

        this.perfMiningService = perfMiningService;
        /*
        this.canoniserService = canoniserService;
        this.domainService    = domainService;
        this.processService   = processService;
        */
    }

    @Override
    public String getLabel(Locale locale) {
        return "Mine Process Performance";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Analyze";
    }

    @Override
    public void execute(PortalContext portalContext) {

        portalContext.getMessageHandler().displayInfo("Executed process performance mining plug-in!");

        try {
            new PerfMiningController(portalContext, this.perfMiningService);
        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
