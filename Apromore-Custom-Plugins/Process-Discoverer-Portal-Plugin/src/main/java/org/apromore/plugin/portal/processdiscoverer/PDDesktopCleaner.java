/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.plugin.portal.processdiscoverer;

import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;

/**
 * This class is registered with ZK as a DesktopCleanup and will be called when the desktop timeouts.
 * It is in charged of cleaning up memory resources used by this plugin.
 * Note that ZK is assumed to automatically destroy all ZK-specific objects (e.g. session, desktop, execution, etc.)
 * 
 * @author Bruce Nguyen
 *
 */
public class PDDesktopCleaner implements DesktopCleanup {
    private static final Logger LOGGER = LoggerFactory.getLogger(PDDesktopCleaner.class.getCanonicalName());

    @Override
    public void cleanup(Desktop desktop) throws Exception {
        LOGGER.info("PD cleanup starts for desktopID = " + desktop.getId());
        
        // Clean up this plugin
        if (desktop.hasAttribute("processDiscoverer")) {
            ((ProcessDiscoverer)desktop.getAttribute("processDiscoverer")).cleanUp();
        }
        if (desktop.hasAttribute("processVisualizer")) {
            ((ProcessVisualizer)desktop.getAttribute("processVisualizer")).cleanUp();
        }

        // Clean up the Portal session as it doesn't control plugin session
        // TEMPORARY: remove this step to keep PD keeps working from refreshing the page after session timeout  
        //UserSessionManager.removeEditSession(desktop.getAttribute("pluginSessionId").toString());

        LOGGER.info("PD cleanup is done for desktopID = " + desktop.getId());
    }
}
