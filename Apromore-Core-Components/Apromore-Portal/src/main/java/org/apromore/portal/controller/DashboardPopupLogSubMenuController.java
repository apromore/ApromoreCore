/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
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

package org.apromore.portal.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.PortalUrlWrapper;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.PopupMenuController;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.UserMetadataSummaryType;
import org.apromore.portal.model.UserType;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

public class DashboardPopupLogSubMenuController extends PopupLogSubMenuController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(DashboardPopupLogSubMenuController.class);

    public DashboardPopupLogSubMenuController(PopupMenuController popupMenuController,
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
            subMenu.setLabel(Labels.getLabel("plugin_analyze_dashboard_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_CREATE_NEW_DASHBOARD));
            fetchAndConstructMenuForDb(menuPopup,
                userMetaDataUtilService.getUserMetadataSummariesForDashboard(logSummaryType.getId()), true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void fetchAndConstructMenuForDb(Menupopup menuPopup, List<UserMetadataSummaryType> summaryTypes,
                                            boolean separatorRequired) {
        if (!summaryTypes.isEmpty()) {
            if (separatorRequired) {
                popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            }
            int index = 1;
            for (UserMetadataSummaryType um : summaryTypes) {
                if (index <= SUBMENU_SIZE) {
                    addMenuItemForDb(menuPopup, um, true);
                    if (index == SUBMENU_SIZE && index < summaryTypes.size()) {
                        addOptionToViewMoreMenuItemsForDb(menuPopup);
                        break;
                    }
                }
                index++;
            }
        }
    }

    private void addOptionToViewMoreMenuItemsForDb(Menupopup menuPopup) {
        Menuitem item = new Menuitem();
        item.setLabel("...");
        item.setStyle(CENTRE_ALIGN);
        item.addEventListener(ON_CLICK, event -> {
            try {
                Map<String, Object> attrMap = new HashMap<>();
                attrMap.put("createNewDashboard", false);
                attrMap.put("forwardFromPopup", true);
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_DASHBOARD);
                plugin.setSimpleParams(attrMap);
                plugin.execute(getPortalContext());
            } catch (Exception ex) {
                LOGGER.error("Error in forwarding the request", ex);
            }
        });
        menuPopup.appendChild(item);
    }

    private void addMenuItemForDb(Menupopup popup, UserMetadataSummaryType um, boolean visibleOnLoad) {
        Menuitem item = new Menuitem();
        item.setLabel(um.getName());
        item.setAttribute(USER_META_DATA, um);
        item.addEventListener(ON_CLICK, event -> {
            try {
                UserMetadataSummaryType umData =
                    (UserMetadataSummaryType) event.getTarget().getAttribute(USER_META_DATA);
                Sessions.getCurrent().setAttribute("logSummaries", Collections.singletonList(logSummaryType));
                Sessions.getCurrent()
                    .setAttribute("userMetadata_dash", userMetaDataUtilService.getUserMetaDataById(umData.getId()));
                Clients.evalJavaScript("window.open('" + PortalUrlWrapper.getUrlWithReference("dashboard/index.zul")+"')");
            } catch (Exception e) {
                LOGGER.error("Error in showing the Dashboard", e);
            }
        });
        item.setVisible(visibleOnLoad);
        popup.appendChild(item);
    }

    private String getSubMenuImage() {
        String subMenuImagePath = null;
        try {
            UserType currentUser = UserSessionManager.getCurrentUser();
            if (currentUser == null) {
                return null;
            }
            if (!currentUser.hasAnyPermission(PermissionType.DASH_EDIT, PermissionType.DASH_VIEW)) {
                LOGGER.info("User '{}' does not have permission to access dashboard",
                    currentUser.getUsername());
                return null;
            }
            subMenuImagePath = "~./themes/ap/common/img/icons/dashboard.svg";

        } catch (Exception ex) {
            LOGGER.error("Error in retrieving user permission", ex);
        }
        return subMenuImagePath;
    }

}
