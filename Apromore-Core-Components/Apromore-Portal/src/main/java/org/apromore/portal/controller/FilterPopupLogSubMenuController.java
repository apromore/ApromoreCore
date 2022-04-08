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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

public class FilterPopupLogSubMenuController extends PopupLogSubMenuController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(FilterPopupLogSubMenuController.class);
    private static final String USER_METADATA_SUM = "USER_METADATA_SUM";

    public FilterPopupLogSubMenuController(PopupMenuController popupMenuController,
                                           MainController mainController,
                                           Menupopup popupMenu,
                                           LogSummaryType logSummaryType) {
        super(popupMenuController, mainController, popupMenu, logSummaryType);
        constructMenu();
    }

    private void constructMenu() {
        String subMenuImage = getFilterSubMenuImage();
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel(Labels.getLabel("plugin_create_Edit_filter_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            addNewFilterMenuItem(menuPopup);
            fetchAndConstructMenuForFilter(menuPopup,
                userMetaDataUtilService.getUserMetadataSummariesForFilter(logSummaryType.getId()),
                true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void addNewFilterMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("plugin_new_filter_text"));
        item.setImage("~./themes/ap/common/img/icons/add-filter.svg");
        item.addEventListener(ON_CLICK, event -> {
            try {
                Map<String, Object> attrMap = new HashMap<>();
                attrMap.put("FORWARD_FROM_CONTEXT_MENU", true);
                attrMap.put(USER_METADATA_SUM, 0);
                attrMap.put("EDIT_FILTER", true);
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
                plugin.setSimpleParams(attrMap);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                LOGGER.error("Error in showing filter", e);
            }

        });
        popup.appendChild(item);
    }

    private void fetchAndConstructMenuForFilter(Menupopup menuPopup, List<UserMetadataSummaryType> summaryTypes,
                                                   boolean separatorRequired) {
        if (!summaryTypes.isEmpty()) {
            if (separatorRequired) {
                popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            }
            int index = 1;
            for (UserMetadataSummaryType um : summaryTypes) {
                if (index <= SUBMENU_SIZE) {
                    addMenuItemForFilter(menuPopup, um, true);
                    if (index == SUBMENU_SIZE && index < summaryTypes.size()) {
                        addOptionToViewMoreMenuItemsForFilter(menuPopup);
                        break;
                    }
                }
                index++;
            }
        }
    }

    private void addOptionToViewMoreMenuItemsForFilter(Menupopup menuPopup) {
        Menuitem item = new Menuitem();
        item.setLabel("...");
        item.setStyle(CENTRE_ALIGN);
        item.addEventListener(ON_CLICK, event -> viewFilter(null));
        menuPopup.appendChild(item);
    }

    private void viewFilter(UserMetadataSummaryType umData) {
        try {
            Executions.getCurrent().getDesktop().setAttribute("DEFAULT_VIEW",false);
            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put("FORWARD_FROM_CONTEXT_MENU", true);
            attrMap.put("EDIT_FILTER", true);
            if (umData != null) {
                attrMap.put(USER_METADATA_SUM, umData.getId());
            } else {
                attrMap.put(USER_METADATA_SUM, null);
            }
            PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
            plugin.setSimpleParams(attrMap);
            plugin.execute(getPortalContext());
        } catch (Exception e) {
            LOGGER.error("Error in showing filter", e);
        }
    }

    private void addMenuItemForFilter(Menupopup popup, UserMetadataSummaryType um, boolean visibleOnLoad) {
        Menuitem item = new Menuitem();
        item.setLabel(um.getName());
        item.setAttribute(USER_META_DATA, um);
        item.addEventListener(ON_CLICK, event -> {
            try {
                viewFilter((UserMetadataSummaryType) event.getTarget().getAttribute(USER_META_DATA));
            } catch (Exception ex) {
                LOGGER.error("Error in showing the filter log discover model", ex);
            }
        });
        item.setVisible(visibleOnLoad);
        popup.appendChild(item);
    }

    private String getFilterSubMenuImage() {
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
            subMenuImagePath = "~./themes/ap/common/img/icons/filter-edit.svg";

        } catch (Exception ex) {
            LOGGER.error("Error in retrieving user permission", ex);
        }
        return subMenuImagePath;
    }

}
