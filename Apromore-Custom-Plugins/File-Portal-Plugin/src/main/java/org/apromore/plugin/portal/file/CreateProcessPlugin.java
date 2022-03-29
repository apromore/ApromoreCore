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

import java.util.Locale;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.PermissionType;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

@Component
public class CreateProcessPlugin extends DefaultPortalPlugin {

  private static Logger LOGGER = PortalLoggerFactory.getLogger(CreateProcessPlugin.class);

  private String label = "Create model";

  // PortalPlugin overrides

  @Override
  public String getLabel(Locale locale) {
    return Labels.getLabel("plugin_discover_createModel_text", label);
  }

  @Override
  public String getIconPath() {
    return "bpmn-add.svg";
  }

  @Override
  public void execute(PortalContext portalContext) {
    MainController mainC = (MainController) portalContext.getMainController();
    mainC.eraseMessage();
    try {
      mainC.openNewProcess();
    } catch (Exception e) {
      Messagebox.show(e.getMessage(), "Apromore", Messagebox.OK, Messagebox.ERROR);
    }

  }

  @Override
  public Availability getAvailability() {
    return UserSessionManager.getCurrentUser().hasAnyPermission(PermissionType.MODEL_CREATE) ?
            Availability.AVAILABLE : Availability.UNAVAILABLE;
  }

}
