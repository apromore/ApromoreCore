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
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;

@Component
public class EditSelectionPlugin extends DefaultPortalPlugin implements LabelSupplier {

  private static Logger LOGGER = PortalLoggerFactory.getLogger(EditSelectionPlugin.class);

  private String label = "Edit model";

  @Override
  public String getBundleName() {
    return "file";
  }

  // PortalPlugin overrides

  @Override
  public String getLabel(Locale locale) {
    String labelKey = UserSessionManager.getCurrentUser().hasAnyPermission(PermissionType.MODEL_EDIT)
        ? "plugin_discover_editModel_text" : "plugin_discover_viewModel_text";
    return Labels.getLabel(labelKey, label);
  }

  @Override
  public String getIconPath() {
    return UserSessionManager.getCurrentUser().hasAnyPermission(PermissionType.MODEL_EDIT)
        ? "model-edit.svg" : "bpmn-model.svg";
  }

  @Override
  public void execute(PortalContext portalContext) {
    if (!portalContext.getCurrentUser().hasAnyPermission(PermissionType.MODEL_EDIT, PermissionType.MODEL_VIEW)) {
      LOGGER.info("User '{}' does not have permission to view or edit models",
              portalContext.getCurrentUser().getUsername());
      return;
    }

    try {
      MainController mainC = (MainController) portalContext.getMainController();
      mainC.eraseMessage();

      Map<SummaryType, List<VersionSummaryType>> selectedProcesses =
          mainC.getSelectedElementsAndVersions();
      if (selectedProcesses.isEmpty() || selectedProcesses.size() > 1) {
        Notification.info(getLabel("selectOnlyOneModel"));
        return;
      } else {
        ProcessSummaryType process =
            (ProcessSummaryType) selectedProcesses.keySet().iterator().next();
        if (selectedProcesses.get(process).size() > 1) {
          Notification.info(getLabel("selectOnlyOneModelVersion"));
          return;
        } else {
          VersionSummaryType version = selectedProcesses.get(process).get(0);
          mainC.openProcess(process, version);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to edit selection", e);
      Notification.error(getLabel("unableEdit"));
    }
  }

  @Override
  public Availability getAvailability() {
    return UserSessionManager.getCurrentUser().hasAnyPermission(PermissionType.MODEL_EDIT, PermissionType.MODEL_VIEW) ?
            Availability.AVAILABLE : Availability.UNAVAILABLE;
  }
}
