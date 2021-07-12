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

import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.file.impl.EditListMetadataController;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.notification.Notification;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

public class EditSelectionMetadataPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(EditSelectionMetadataPlugin.class);

    private String label = "Rename"; // "Edit metadata"
    private String groupLabel = "File";

    // PortalPlugin overrides

    @Override
    public String getItemCode(Locale locale) { return label; }

    @Override
    public String getGroup(Locale locale) {
        return "File";
    }

    @Override
    public String getLabel(Locale locale) {
        return Labels.getLabel("plugin_file_rename_text",label);
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return Labels.getLabel("plugin_file_title_text", groupLabel);
    }


    @Override
    public String getIconPath() {
        return "rename.svg"; // "meta-edit.svg"
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            MainController mainC = (MainController) portalContext.getMainController();

            mainC.eraseMessage();
            if (!mainC.getBaseListboxController().isSingleFileSelected()) {
                Notification.error("Please select single file or folder to rename");
                return;
            }
            List<Integer> folderIds = mainC.getPortalSession().getSelectedFolderIds();

            if (folderIds.size() > 0) {
                mainC.getBaseListboxController().renameFolder();
            } else {
                Map<SummaryType, List<VersionSummaryType>> selectedElements = mainC.getSelectedElementsAndVersions();

                if (selectedElements.size() > 0) {
                    new EditListMetadataController(mainC, selectedElements);
                } else {
                    mainC.displayMessage("No folder, process version or event log is selected.");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to edit selection metadata", e);
            Messagebox.show("Unable to edit selection metadata");
        }
    }
}
