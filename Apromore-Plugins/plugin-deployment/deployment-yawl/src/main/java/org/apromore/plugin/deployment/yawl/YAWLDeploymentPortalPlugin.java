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
package org.apromore.plugin.deployment.yawl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;

import org.zkoss.zul.Messagebox;

/**
 *
 * Implementation of your new Plugin API. This class extends the default implementation, that is often provided by APIs.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 *
 */
@Component("yawlDeploymentPortalPlugin")
public class YAWLDeploymentPortalPlugin extends DefaultPortalPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(YAWLDeploymentPortalPlugin.class);

    @Override
    public String getLabel(Locale locale) {
        return "Deploy Process to YAWL";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Implement";
    }

    @Override
    public void execute(PortalContext portalContext) {
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = portalContext.getSelection().getSelectedProcessModelVersions();
        if (selectedProcessVersions.size() == 1 && selectedProcessVersions.keySet().iterator().next() instanceof ProcessSummaryType) {
            Map<ProcessSummaryType, List<VersionSummaryType>> processSummaryVersions = new HashMap<>();
            processSummaryVersions.put((ProcessSummaryType) selectedProcessVersions.keySet().iterator().next(), selectedProcessVersions.get(selectedProcessVersions.keySet().iterator().next()));
            try {
                new DeployProcessModelController(portalContext, processSummaryVersions.entrySet().iterator().next());
            } catch (InterruptedException | IOException e) {
                Messagebox.show("Unable to deploy process model: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
                LOGGER.error("Unable to deploy process model", e);
            }
        } else {
            Messagebox.show("Please select exactly one process model!");
        }
    }
}
