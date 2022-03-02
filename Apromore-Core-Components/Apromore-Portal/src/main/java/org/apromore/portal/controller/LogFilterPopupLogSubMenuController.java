/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
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

package org.apromore.portal.controller;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.PopupMenuController;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.UserType;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menupopup;

public class LogFilterPopupLogSubMenuController extends PopupLogSubMenuController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(LogFilterPopupLogSubMenuController.class);

    public LogFilterPopupLogSubMenuController(PopupMenuController popupMenuController,
                                              MainController mainController,
                                              Menupopup popupMenu,
                                              LogSummaryType logSummaryType) {
        super(popupMenuController, mainController, popupMenu, logSummaryType);
        constructMenu();
    }

    private void constructMenu() {
        String subMenuImage = getSubMenuImage();
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel(Labels.getLabel("plugin_create_log_Edit_filter_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_CREATE_NEW_LOG_FILTER));
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private String getSubMenuImage() {
        String subMenuImagePath = null;
        try {
            UserType currentUser = UserSessionManager.getCurrentUser();
            if (currentUser == null) {
                return null;
            }
            if (!currentUser
                .hasAnyPermission(PermissionType.FILTER_VIEW, PermissionType.FILTER_EDIT)) {
                LOGGER.info("User '{}' does not have permission to access log filter",
                    currentUser.getUsername());
                return null;
            }
            subMenuImagePath = "~./themes/ap/common/img/icons/filter.svg";

        } catch (Exception ex) {
            LOGGER.error("Error in retrieving user permission", ex);
        }
        return subMenuImagePath;
    }

}
