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
package org.apromore.etlplugin.portal;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import org.apromore.etlplugin.logic.services.ETLPluginLogic;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
//import org.apromore.service.csvimporter.CSVImporterLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.zkoss.util.media.Media;
//import org.zkoss.zk.ui.Executions;
//import org.zkoss.zk.ui.Sessions;
//import org.zkoss.zul.Window;
//import org.apromore.etlplugin.logic.services.ETLPluginLogic;
import org.zkoss.json.JSONObject;
import org.zkoss.zhtml.Map;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import javax.inject.Inject;

public class ETLPluginPortal extends DefaultPortalPlugin {

    @Inject
    private ETLPluginLogic etlPluginLogic;
    private static Logger LOGGER = LoggerFactory.getLogger(ETLPluginPortal.class);
    private String label = "ETLPlugin";
    private String groupLabel = "File";
//    private MainController mainC;
    static final String SESSION_ATTRIBUTE_KEY = "etlplugin";


    public void setEtlPluginLogic(ETLPluginLogic newEtlPluginLogic) {
        System.out.println("ETLPluginPortal has recieved");
        this.etlPluginLogic = newEtlPluginLogic;
    }

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
        return "";
    }

    @Override
    public void execute(PortalContext portalContext) {
        System.out.println("!!!ETL Plugin Portal Execute: " + etlPluginLogic.getTest());

        // Configure the arguments to pass to the CSV importer view
        Map arg = new HashMap<>();
        arg.put("etlPluginLogic", etlPluginLogic);
        Sessions.getCurrent().setAttribute(ETLPluginPortal.SESSION_ATTRIBUTE_KEY, arg);

        String zul = "/etlplugin/invalidData.zul";
        try {
            Executions.getCurrent().sendRedirect(zul, "etlPlugin");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in ETL plugin portal");
        }
    }
}
