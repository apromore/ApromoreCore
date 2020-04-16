/*
 * Copyright © 2009-2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.useradmin;

import java.util.Locale;
import javax.inject.Inject;
import org.apromore.model.PermissionType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zul.Messagebox;

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
            new UserAdminController(portalContext, securityService);

        } catch(Exception e) {
            LOGGER.error("Unable to create user administration dialog", e);
            Messagebox.show("Unable to create user administration dialog");
        }
    }

    @Override
    public Availability getAvailability(PortalContext portalContext) {

        // Require that the caller has the "Edit users" permission
        for (PermissionType permission: portalContext.getCurrentUser().getPermissions()) {
            if (Permissions.VIEW_USERS.getRowGuid().equals(permission.getId())) {
                return Availability.AVAILABLE;
            }
        }

        // Otherwise, this UI is unavailable
        return Availability.UNAVAILABLE;
    }
}
