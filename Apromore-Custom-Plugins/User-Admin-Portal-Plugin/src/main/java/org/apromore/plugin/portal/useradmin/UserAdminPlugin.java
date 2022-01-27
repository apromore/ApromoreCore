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
package org.apromore.plugin.portal.useradmin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.inject.Inject;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.zk.notification.Notification;
import org.apromore.service.SecurityService;
import org.apromore.service.WorkspaceService;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zul.Window;

@Component("userAdminPlugin")
public class UserAdminPlugin extends DefaultPortalPlugin {

  private static Logger LOGGER = PortalLoggerFactory.getLogger(UserAdminPlugin.class);

  private String label = "Manage user permissions";

  @Inject
  private SecurityService securityService;
  @Inject
  private WorkspaceService workspaceService;

  public ResourceBundle getLabels() {
    Locale locale = (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE);
    return ResourceBundle.getBundle("useradmin", locale, UserAdminPlugin.class.getClassLoader());
  }

  public String getLabel(String key) {
    return getLabels().getString(key);
  }

  @Override
  public String getLabel(Locale locale) {
    return label;
  }

  @Override
  public void execute(PortalContext portalContext) {

    try {
      // Deny access if the caller isn't an administrator
      Role adminRole = securityService.findRoleByName("ROLE_SUPER_ADMIN");
      User currentUser = securityService.getUserById(portalContext.getCurrentUser().getId());
      Set<Role> userRoles = securityService.findRolesByUser(currentUser);
      if (!userRoles.contains(adminRole)) {
        Notification.info(getLabel("onlyAdmin_message"));
        return;
      }

      // Present the user admin window˙
      Map arg = new HashMap<>();
      arg.put("portalContext", portalContext);
      arg.put("securityService", securityService);
      arg.put("workspaceService", workspaceService);
      Window window = (Window) Executions.getCurrent()
          .createComponents(getPageDefination("static/zul/index.zul"), null, arg);
      window.doModal();

    } catch (Exception e) {
      LOGGER.error("Unable to create user administration dialog", e);
      Notification.error(getLabel("failedLaunch_message"));
    }
  }

  private PageDefinition getPageDefination(String uri) throws IOException {
    Execution current = Executions.getCurrent();
    PageDefinition pageDefinition = current.getPageDefinitionDirectly(
        new InputStreamReader(getClass().getClassLoader().getResourceAsStream(uri)), "zul");
    return pageDefinition;
  }

  @Override
  public Availability getAvailability() {
    return Availability.HIDDEN; // Hide from user menu
    // Require that the caller has the "Edit users" permission
    /*
     * UserType user = UserSessionManager.getCurrentUser(); if (user != null) { for (PermissionType
     * permission: user.getPermissions()) { if
     * (Permissions.VIEW_USERS.getRowGuid().equals(permission.getId())) { return
     * Availability.AVAILABLE; } } } // Otherwise, this UI is unavailable return
     * Availability.UNAVAILABLE;
     */
  }
}
