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
package org.apromore.plugin.portal.file;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.file.impl.EditListMetadataController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

@Component
public class EditSelectionMetadataPlugin extends DefaultPortalPlugin implements LabelSupplier {

  private static Logger LOGGER = PortalLoggerFactory.getLogger(EditSelectionMetadataPlugin.class);

  private String label = "Rename"; // "Edit metadata"

  @Override
  public String getBundleName() {
    return "file";
  }

  // PortalPlugin overrides

  @Override
  public String getLabel(Locale locale) {
    return Labels.getLabel("plugin_file_rename_text", label);
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
        Notification.error(getLabel("selectOnlyOneFileOrFolderRename"));
        return;
      }
      List<Integer> folderIds = mainC.getPortalSession().getSelectedFolderIds();

      if (folderIds.size() > 0) {
        mainC.getBaseListboxController().renameFolder();
      } else {
        Map<SummaryType, List<VersionSummaryType>> selectedElements =
            mainC.getSelectedElementsAndVersions();

        if (selectedElements.size() > 0) {
          new EditListMetadataController(mainC, selectedElements);
        } else {
          mainC.displayMessage(getLabel("selectOnlyOneFileOrFolderRename"));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to edit selection metadata", e);
      Messagebox.show(getLabel("unableRename"));
    }
  }
}
