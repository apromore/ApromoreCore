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

package org.apromore.plugin.portal;

import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.metainfo.PageDefinition;

public final class PortalContexts {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(PortalContexts.class);
    private static final String PORTAL_CONTEXT = "portalContext_";

    private PortalContexts() {

    }

    public static PortalContext getActivePortalContext() {
        PortalContext portalContext = null;
        try {
            String referId = getReferId();
            Session current = Sessions.getCurrent();
            if (current != null && referId != null && current.getAttribute(PORTAL_CONTEXT + referId) != null) {
                portalContext = (PortalContext) current.getAttribute(PORTAL_CONTEXT + referId);
            }
        } catch (Exception ex) {
            LOGGER.error("Error in retrieving PortalContext", ex);
        }
        return portalContext;
    }

    /*
     *	Using portal's reference id from the desktop scope, we will us it to retrieve the portalContext object from Session
     */
    private static String getReferId() {
        Desktop desktop = Executions.getCurrent().getDesktop();
        String portalRef = null;
        if (desktop.getAttribute("PORTAL_REF_ID") != null) {
            portalRef = (String) desktop.getAttribute("PORTAL_REF_ID");
        }
        return portalRef;
    }

    public static void removePortalContextReference(Desktop desktop) {
        try {
            Session session = Executions.getCurrent().getSession();
            if (session.getAttribute(PORTAL_CONTEXT + desktop.getId()) != null) {
                session.removeAttribute(PORTAL_CONTEXT + desktop.getId());
                LOGGER.info("Successfully removed from session with ID:{}", desktop.getId());
            }
        } catch (Exception ex) {
            LOGGER.error("Error in removing portalContext", ex);
        }
    }

    public static PageDefinition getPageDefinition(String path) throws IOException {
        Execution current = Executions.getCurrent();
        return current.getPageDefinitionDirectly(
            new InputStreamReader(PageDefinition.class.getClassLoader().getResourceAsStream(path)), "zul"
        );
    }
}