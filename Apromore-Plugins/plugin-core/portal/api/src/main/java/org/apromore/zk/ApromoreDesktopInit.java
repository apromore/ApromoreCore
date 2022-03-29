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

package org.apromore.zk;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.util.DesktopInit;

public class ApromoreDesktopInit implements DesktopInit {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreDesktopInit.class);

    @Override
    public void init(Desktop desktop, Object request) throws Exception {
        LOGGER.debug("Init desktop " + desktop + " with request " + request);

        if (desktop.getExecution().getParameterMap().containsKey("REFER_ID")) {
            try {
                String referId = (desktop.getExecution().getParameterMap().get("REFER_ID"))[0];
                desktop.setAttribute("PORTAL_REF_ID", referId);
                LOGGER.info("New Desktop Created with REFER_ID:{}", referId);
            } catch (Exception ex) {
                LOGGER.error("Error in Setting desktop Attribute", ex);
            }
        }

        Object notice = desktop.getWebApp().getAttribute("org.zkoss.zk.ui.client.notice");
        if (notice != null) {
            desktop.getWebApp().removeAttribute("org.zkoss.zk.ui.client.notice");
        }
    }

}
