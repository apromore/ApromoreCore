/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.zk;

import java.util.Collection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopCleanup;

/**
 * OSGi support for ZK's per-desktop cleanup hook.
 *
 * Use it by exporting {@link DesktopCleanup} instances as OSGi services.
 * When the Apromore Portal closes a desktop, each cleanup service will be invoked.
 */
public class ApromoreDesktopCleanup implements DesktopCleanup {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApromoreDesktopCleanup.class);

    @Override
    public void cleanup(Desktop desktop) throws InvalidSyntaxException {
        LOGGER.debug("Cleaning desktop " + desktop);
        BundleContext bundleContext = (BundleContext) desktop.getWebApp().getServletContext().getAttribute("osgi-bundlecontext");
        for (ServiceReference serviceReference: bundleContext.getServiceReferences(DesktopCleanup.class.getName(), null)) {
            DesktopCleanup desktopCleanup = (DesktopCleanup) bundleContext.getService((ServiceReference) serviceReference);

            try {
                LOGGER.debug("Invoking cleanup service " + desktopCleanup);
                desktopCleanup.cleanup(desktop);
                LOGGER.debug("Invoked cleanup service " + desktopCleanup);

            } catch (Throwable throwable) {
                LOGGER.error("Unable to clean up desktop " + desktop, throwable);
            }
        }
        LOGGER.debug("Cleaned desktop " + desktop);
    }
}
