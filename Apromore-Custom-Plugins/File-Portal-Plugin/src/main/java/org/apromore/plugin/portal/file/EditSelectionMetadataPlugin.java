/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.file.impl.EditListMetadataController;
import org.apromore.portal.dialogController.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Messagebox;

public class EditSelectionMetadataPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(EditSelectionMetadataPlugin.class);

    private String label = "Edit metadata";
    private String groupLabel = "File";


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
        return "meta-edit.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            MainController mainC = (MainController) portalContext.getMainController();

            mainC.eraseMessage();
            Map<SummaryType, List<VersionSummaryType>> selectedElements = mainC.getSelectedElementsAndVersions();

            if (selectedElements.size() != 0) {
                new EditListMetadataController(mainC, selectedElements);

            } else {
                mainC.displayMessage("No process versions or event logs selected.");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to edit selection metadata", e);
            Messagebox.show("Unable to edit selection metadata");
        }
    }
}
