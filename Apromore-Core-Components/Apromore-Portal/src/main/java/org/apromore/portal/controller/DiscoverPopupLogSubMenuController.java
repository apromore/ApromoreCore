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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
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
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

public class DiscoverPopupLogSubMenuController extends PopupLogSubMenuController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(DiscoverPopupLogSubMenuController.class);

    public DiscoverPopupLogSubMenuController(PopupMenuController popupMenuController,
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
            subMenu.setLabel(Labels.getLabel("plugin_discover_discoverModel_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_VIEW_FULL_LOG_DISCOVER_MODEL));
            fetchAndConstructMenuForPd(menuPopup,
                userMetaDataUtilService.getUserMetadataSummariesForFilter(logSummaryType.getId()), true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void fetchAndConstructMenuForPd(Menupopup menuPopup, List<UserMetadataSummaryType> summaryTypes,
                                            boolean separatorRequired) {
        if (!summaryTypes.isEmpty()) {
            if (separatorRequired) {
                popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            }
            int index = 1;
            for (UserMetadataSummaryType um : summaryTypes) {
                if (index <= SUBMENU_SIZE) {
                    addMenuItemForPd(menuPopup, um, true);
                    if (index == SUBMENU_SIZE && index < summaryTypes.size()) {
                        addOptionToViewMoreMenuItemsForPd(menuPopup);
                        break;
                    }
                }
                index++;
            }
        }
    }

    private void addOptionToViewMoreMenuItemsForPd(Menupopup menuPopup) {
        Menuitem item = new Menuitem();
        item.setLabel("...");
        item.setStyle(CENTRE_ALIGN);
        item.addEventListener(ON_CLICK, event -> {
            viewProcessDiscovery(null);
        });
        menuPopup.appendChild(item);
    }

    private void viewProcessDiscovery(UserMetadataSummaryType umData) {
        try {
            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put("FORWARD_FROM_CONTEXT_MENU", true);
            attrMap.put("EDIT_FILTER", false);
            if (umData != null) {
                attrMap.put("USER_METADATA_SUM", umData.getId());
            } else {
                attrMap.put("USER_METADATA_SUM", null);
            }
            PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
            plugin.setSimpleParams(attrMap);
            plugin.execute(getPortalContext());
        } catch (Exception e) {
            LOGGER.error("Error in showing the filter log discover model", e);
        }
    }

    private void addMenuItemForPd(Menupopup popup, UserMetadataSummaryType um, boolean visibleOnLoad) {
        Menuitem item = new Menuitem();
        item.setLabel(um.getName());
        item.setAttribute(USER_META_DATA, um);
        item.addEventListener(ON_CLICK, event -> {
            try {
                viewProcessDiscovery((UserMetadataSummaryType) event.getTarget().getAttribute(USER_META_DATA));
            } catch (Exception e) {
                LOGGER.error("Error in showing the filter log discover model", e);
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
            if (!currentUser
                .hasAnyPermission(PermissionType.MODEL_DISCOVER_EDIT, PermissionType.MODEL_DISCOVER_VIEW)) {
                LOGGER.info("User '{}' does not have permission to access process discoverer",
                    currentUser.getUsername());
                return null;
            }
            subMenuImagePath = "~./themes/ap/common/img/icons/model-discover.svg";

        } catch (Exception ex) {
            LOGGER.error("Error in retrieving user permission", ex);
        }
        return subMenuImagePath;
    }

}
