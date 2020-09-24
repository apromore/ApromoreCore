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

import java.util.*;

import org.apromore.etlplugin.logic.services.ETLPluginLogic;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import javax.inject.Inject;

/**
 * ETL plugin portal is an adaptor class between the Apromore-Core portal
 * and the complete ETL plugin system. We use this create ui information
 * and attach it to an event listener in MenuController.
 */
public class ETLPluginPortal extends DefaultPortalPlugin {

    @Inject
    private ETLPluginLogic etlPluginLogic;
    private static Logger LOGGER = LoggerFactory
            .getLogger(ETLPluginPortal.class);
    private String label = "ETLPlugin";
    private String groupLabel = "File";
    static final String SESSION_ATTRIBUTE_KEY = "etlplugin";

    /**
     * Inject ETL plugin logic bean.
     *
     * @param newEtlPluginLogic is the injected bean from the context.
     */
    public void setEtlPluginLogic(ETLPluginLogic newEtlPluginLogic) {
        this.etlPluginLogic = newEtlPluginLogic;
    }

    /**
     * Get the label value.
     *
     * @param locale none.
     * @return Label Name.
     */
    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    /**
     * Get the group label info.
     *
     * @param locale none.
     * @return Group Label.
     */
    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    /**
     * Get the icon path.
     *
     * @return icon path.
     */
    @Override
    public String getIconPath() {
        return "etlPluginLogo.svg";
    }

    /**
     * Manages the ETL adaptor logic.
     *
     * @param portalContext of the entire system.
     */
    @Override
    public void execute(PortalContext portalContext) {

        // Configure the arguments to pass the ETL logic bean.
        Map inputMap = new HashMap<>();
        inputMap.put("etlPluginLogic", etlPluginLogic);
        Sessions.getCurrent().setAttribute(
                ETLPluginPortal.SESSION_ATTRIBUTE_KEY, inputMap);

        String zul = "/etlplugin/etl.zul";
        try {
            // Create the page.
            Executions.getCurrent().sendRedirect(zul, "etlPlugin");
        } catch (Exception e) {
            Messagebox.show("Error! cant open ETL plugin.");
            e.printStackTrace();
        }
    }
}
