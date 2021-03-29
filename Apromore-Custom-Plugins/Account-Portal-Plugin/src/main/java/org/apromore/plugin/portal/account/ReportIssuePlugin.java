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

package org.apromore.plugin.portal.account;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.util.Clients;

import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.common.UserSessionManager;

@Component
public class ReportIssuePlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(ReportIssuePlugin.class);

    private String label = "Report issue";
    private String groupLabel = "Account";

    // PortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    @Override
    public String getIconPath() {
        return "report-issue.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        String email = portalContext.getMainController().getContactEmail();
        String userName = UserSessionManager.getCurrentUser().getUsername();
        LOGGER.info("launch mail client");
        Clients.evalJavaScript("Ap.common.reportIssue('" + email + "', '" + userName + "');");
    }
}
