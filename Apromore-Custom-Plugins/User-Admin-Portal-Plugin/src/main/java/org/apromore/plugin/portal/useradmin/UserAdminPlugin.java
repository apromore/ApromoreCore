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
package org.apromore.plugin.portal.useradmin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.PermissionType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@Component("userAdminPlugin")
public class UserAdminPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(UserAdminPlugin.class);

    private String label = "Manage user permissions";
    private String groupLabel = "Settings";

    @Inject private SecurityService securityService;

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
    public void execute(PortalContext portalContext) {

        try {
            // Deny access if the caller isn't an administrator
            Role adminRole = securityService.findRoleByName("ROLE_ADMIN");
            User currentUser = securityService.getUserById(portalContext.getCurrentUser().getId());
            Set<Role> userRoles = securityService.findRolesByUser(currentUser);
            if (!userRoles.contains(adminRole)) {
                Messagebox.show("Only administrator accounts may manage users.");
                return;
            }

            // Present the user admin window
            Map arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            Window window = (Window) Executions.getCurrent().createComponents("user-admin/zul/index.zul", null, arg);
            window.doModal();

        } catch(Exception e) {
            LOGGER.error("Unable to create user administration dialog", e);
            Messagebox.show("Unable to create user administration dialog");
        }
    }

    @Override
    public Availability getAvailability() {

        // Require that the caller has the "Edit users" permission
        for (PermissionType permission: UserSessionManager.getCurrentUser().getPermissions()) {
            if (Permissions.VIEW_USERS.getRowGuid().equals(permission.getId())) {
                return Availability.AVAILABLE;
            }
        }

        // Otherwise, this UI is unavailable
        return Availability.UNAVAILABLE;
    }
}
