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

package org.apromore.plugin.portal.accesscontrol;

import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;

import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.dialogController.MainController;
import org.apromore.plugin.portal.accesscontrol.controllers.SecuritySetupController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.apromore.dao.model.User;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.notification.Notification;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.service.SecurityService;

@Component("accessControlPlugin")
public class AccessControlPlugin extends DefaultPortalPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlPlugin.class);

    private String ID = "ACCESS_CONTROL_PLUGIN";
    private String label = "Manage access control";
    private String groupLabel = "Security";

    @Inject private SecurityService securityService;

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getLabel(final Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(final Locale locale) {
        return groupLabel;
    }

    @Override
    public String getIconPath() {
        return "/share-icon.svg";
    }

    @Override
    public Availability getAvailability() {
        return Availability.HIDDEN;
    }

    @Override
    public void execute(final PortalContext portalContext) {
        LOGGER.info("execute");
        Map arg = getSimpleParams();
        try {
            boolean canShare = false;
            Object selectedItem = arg.get("selectedItem");
            User currentUser = securityService.getUserById(portalContext.getCurrentUser().getId());
            if (selectedItem != null) {
                canShare = ItemHelpers.isShareable(selectedItem, currentUser);
            }
            boolean withFolderTree = (boolean) arg.getOrDefault("withFolderTree", false);
            if (withFolderTree) {
                new SecuritySetupController((MainController) portalContext.getMainController(),
                        UserSessionManager.getCurrentUser(), selectedItem, canShare);
            } else {
                if (canShare) {
                    Window window = (Window) Executions.getCurrent().createComponents("/accesscontrol/zul/share.zul", null, arg);
                    window.doModal();
                } else {
                    Notification.error("Only Owner can share an item");
                }
            }
        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
