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
