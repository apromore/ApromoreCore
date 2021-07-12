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
package org.apromore.plugin.portal.file;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apromore.portal.common.notification.Notification;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

public class EditSelectionPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(EditSelectionPlugin.class);

    private String label = "Edit model";
    private String groupLabel = "Discover";

    @Override
    public String getGroup(Locale locale) {
        return "Discover";
    }

    @Override
    public String getItemCode(Locale locale) { return "Edit model"; }

    // PortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return Labels.getLabel("plugin_discover_editModel_text",label);
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return Labels.getLabel("plugin_discover_title_text", groupLabel);
    }

    @Override
    public String getIconPath() {
        return "model-edit.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            MainController mainC = (MainController) portalContext.getMainController();
            mainC.eraseMessage();
            
            Map<SummaryType, List<VersionSummaryType>> selectedProcesses = mainC.getSelectedElementsAndVersions();
            if (selectedProcesses.isEmpty()) {
                Notification.info("Please select one process model.");
                return;
            }
            else if (selectedProcesses.size() > 1) {
                Notification.info("Please select only one process model.");
                return;
            }
            else {
                ProcessSummaryType process = (ProcessSummaryType)selectedProcesses.keySet().iterator().next();
                if (selectedProcesses.get(process).size() > 1) {
                    Notification.info("Please select only one process model version.");
                    return;
                }
                else {
                    VersionSummaryType version = selectedProcesses.get(process).get(0);
                    mainC.openProcess(process, version);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to edit selection", e);
            Notification.error("Unable to edit selection");
        }
    }
}
