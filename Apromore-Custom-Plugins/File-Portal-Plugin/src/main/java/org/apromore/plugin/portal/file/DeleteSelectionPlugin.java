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

import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

public class DeleteSelectionPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(DeleteSelectionPlugin.class);

    private String label = "Delete";
    private String groupLabel = "File";

    @Override
    public String getItemCode(Locale locale) { return label; }

    // PortalPlugin overrides

    @Override
    public String getGroup(Locale locale) {
        return "File";
    }

    @Override
    public String getLabel(Locale locale) {
        return Labels.getLabel("plugin_file_delete_text",label);
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return Labels.getLabel("plugin_file_title_text", groupLabel);
    }


    @Override
    public String getIconPath() {
        return "trash.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        MainController mainC = (MainController) portalContext.getMainController();

        try {
            mainC.eraseMessage();
            Map<SummaryType, List<VersionSummaryType>> elements = mainC.getSelectedElementsAndVersions();
            if (elements.size() != 0) {
                mainC.deleteElements(elements);
                mainC.clearProcessVersions();
            } else {
                mainC.displayMessage("No process version selected.");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to delete selection", e);
            Messagebox.show("Unable to delete selection");
        }
    }
}
