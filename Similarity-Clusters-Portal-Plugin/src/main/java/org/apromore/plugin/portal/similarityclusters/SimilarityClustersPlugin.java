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

package org.apromore.plugin.portal.similarityclusters;

// Java 2 Standard Edition packages
import java.io.IOException;
import java.util.Locale;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zul.Messagebox;

// Local packages
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;
import org.apromore.service.ClusterService;

@Component("plugin")
public class SimilarityClustersPlugin extends PluginCustomGui {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarityClustersPlugin.class);

    @Inject private ClusterService clusterService;

    @Override
    public String getLabel(Locale locale) {
        return "Detect Clones";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Analysis";
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            new SimilarityClustersController(portalContext, clusterService);
        } catch (InterruptedException | IOException e) {
            Messagebox.show("Something went wrong (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
