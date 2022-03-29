/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.plugin.portal.loganimation;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;

/**
 * This class is registered with ZK as a DesktopCleanup and will be called when the session timeouts.
 * It is in charged of cleaning up memory resources used by this plugin.
 * Note that ZK is assumed to automatically destroy all ZK-specific objects (e.g. session, desktop, execution, etc.)
 * 
 * @author Bruce Nguyen
 *
 */
public class LogAnimationCleaner implements DesktopCleanup {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(LogAnimationCleaner.class);

    @Override
    public void cleanup(Desktop desktop) throws Exception {
        LOGGER.info("LogAnimation cleanup starts for desktopID = {}", desktop.getId());
        
        // Clean up this plugin
        // Log animation logic has no tricky resources to be cleaned up, auto cleaned by the GC

        // Clean up the Portal session as it doesn't control plugin session
        if (desktop.hasAttribute("pluginSessionId")) {
            UserSessionManager.removeEditSession(desktop.getAttribute("pluginSessionId").toString());
        }
        
        LOGGER.debug("LogAnimation cleanup is done for desktopID = {}", desktop.getId());
    }
}
