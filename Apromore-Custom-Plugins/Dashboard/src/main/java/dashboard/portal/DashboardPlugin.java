/*
 * Copyright © 2009-2019 The Apromore Initiative.
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

package dashboard.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

public class DashboardPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(DashboardPlugin.class);
    public static Map<String, List<LogSummaryType>> sessionMap = new HashMap<>();

    private String label = "Dashboard";
    private String groupLabel = "Monitor";
    private String sessionId;


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
    public void execute(PortalContext portalContext) {
        LOGGER.info("Execute dashboard");

        List<LogSummaryType> logSummaries = new ArrayList<>();

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        for (SummaryType selection: elements.keySet()) {
            if (selection instanceof LogSummaryType) {
                logSummaries.add((LogSummaryType) selection);
            } else {
                Messagebox.show("Please select only logs.", "Wrong Log Selection", Messagebox.OK, Messagebox.INFORMATION);
                return;
            }
        }
        if (logSummaries.size() < 1) {
            Messagebox.show("Please select one or more logs.", "Wrong Log Selection", Messagebox.OK, Messagebox.INFORMATION);
            return;
        }

        // Prepare a session to transmit to the other web application
        sessionId = UUID.randomUUID().toString();
        sessionMap.put(sessionId, logSummaries);

        // Redirect to the dashboard web application
        Clients.evalJavaScript("window.open('../dashboard/index.zul?id=" + sessionId + "')");
    }
}
