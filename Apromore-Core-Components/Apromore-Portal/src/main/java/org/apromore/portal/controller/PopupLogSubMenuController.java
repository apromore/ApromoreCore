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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.controller.helper.UserMetaDataUtilService;
import org.apromore.portal.context.PortalPluginResolver;
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
import org.zkoss.zul.Messagebox;

public class PopupLogSubMenuController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(PopupLogSubMenuController.class);

    private PopupMenuController popupMenuController;
    private MainController mainController;
    private Menupopup popupMenu;
    private final LogSummaryType logSummaryType;
    private Map<String, PortalPlugin> portalPluginMap;
    private UserMetaDataUtilService userMetaDataUtilService;
    private static final String ON_CLICK = "onClick";
    private static final String CENTRE_ALIGN = "vertical-align: middle; text-align:left;color: var(--ap-c-widget)";
    private static final int SUBMENU_SIZE = 5;
    private static final String SUB_MENU_FOR_PD = "PD";
    private static final String SUB_MENU_FOR_LOG_FILTER = "LOG_FILTER";
    private static final String SUB_MENU_FOR_DASHBOARD = "DASHBOARD";
    private static final String SUB_MENU_FOR_CALENDAR = "CALENDAR";
    private static final String SUB_MENU_FOR = "SUB_MENU_FOR";
    private static final String USER_META_DATA = "USER_META_DATA";
    private static final String CALENDAR_DATA = "CALENDAR_DATA";


    public PopupLogSubMenuController(PopupMenuController popupMenuController, MainController mainController,
                                     Menupopup popupMenu,
                                     LogSummaryType logSummaryType) {
        this.popupMenuController = popupMenuController;
        this.mainController = mainController;
        this.popupMenu = popupMenu;
        this.logSummaryType = logSummaryType;
        portalPluginMap = PortalPluginResolver.getPortalPluginMap();
        userMetaDataUtilService = new UserMetaDataUtilService();
    }

    public void constructSubMenu(String subMenuId) {
        try {
            switch (subMenuId) {
                case PluginCatalog.PLUGIN_DISCOVER_MODEL_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_DISCOVER_MODEL)) {
                        addSubMenuProcessDiscoverModel();
                    }
                    break;
                case PluginCatalog.PLUGIN_DASHBOARD_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_DASHBOARD)) {
                        addSubMenuDashboard();
                    }
                    break;
                case PluginCatalog.PLUGIN_LOG_FILTER_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_FILTER_LOG)) {
                        addSubMenuFilter();
                    }
                    break;
                case PluginCatalog.PLUGIN_APPLY_CALENDAR_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_CALENDAR)) {
                        addSubMenuCalendar();
                    }
                    break;
                default:
            }
        } catch (Exception ex) {
            LOGGER.error("Error in constructing menu sub-menu: " + subMenuId, ex);
        }
    }

    private void addSubMenuProcessDiscoverModel() {
        String subMenuImage = getSubMenuImage(PluginCatalog.PLUGIN_DISCOVER_MODEL_SUB_MENU);
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel(Labels.getLabel("plugin_discover_discoverModel_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_VIEW_FULL_LOG_DISCOVER_MODEL));
            fetchAndConstructMenu(menuPopup,
                userMetaDataUtilService.getUserMetadataSummariesForFilter(logSummaryType.getId()), SUB_MENU_FOR_PD,
                true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void fetchAndConstructMenu(Menupopup menuPopup, List<UserMetadataSummaryType> summaryTypes,
                                       String subMenuFor, boolean separatorRequired) {
        if (!summaryTypes.isEmpty()) {
            if (separatorRequired) {
                popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            }
            int index = 1;
            for (UserMetadataSummaryType um : summaryTypes) {
                if (index <= SUBMENU_SIZE) {
                    addMenuItem(menuPopup, um, true, subMenuFor);
                    if (index == SUBMENU_SIZE && index < summaryTypes.size()) {
                        addOptionToViewMoreMenuItems(menuPopup, subMenuFor);
                        break;
                    }
                }
                index++;
            }
        }
    }

    private void fetchAndConstructMenuForCalendar(Menupopup menuPopup, CalendarModel calendarModel, String subMenuFor,
                                                  boolean separatorRequired) {
        if (calendarModel != null) {
            if (separatorRequired) {
                popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            }
            Menuitem item = new Menuitem();
            item.setLabel(calendarModel.getName());
            item.setAttribute(SUB_MENU_FOR, subMenuFor);
            item.setAttribute(CALENDAR_DATA, calendarModel);
            item.addEventListener(ON_CLICK, event -> {
                try {
                    CalendarModel model = (CalendarModel) event.getTarget().getAttribute(CALENDAR_DATA);
                    Map<String, Object> attrMap = new HashMap<>();
                    attrMap.put("artifactName", model.getName());
                    attrMap.put("logId", logSummaryType.getId());
                    attrMap.put("calendarId", model.getId());
                    attrMap.put("FOWARD_FROM_CONTEXT", true);
                    PortalPlugin calendarPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_CALENDAR);
                    calendarPlugin.setSimpleParams(attrMap);
                    calendarPlugin.execute(getPortalContext());
                } catch (Exception e) {
                    LOGGER.error(Labels.getLabel("portal_failedLaunchCustomCalendar_message"), e);
                    Messagebox.show(Labels.getLabel("portal_failedLaunchCustomCalendar_message"));
                }
            });
            menuPopup.appendChild(item);
        }
    }

    private void addOptionToViewMoreMenuItems(Menupopup menuPopup, String subMenuFor) {
        Menuitem item = new Menuitem();
        item.setLabel("...");
        item.setStyle(CENTRE_ALIGN);
        item.setAttribute(SUB_MENU_FOR, subMenuFor);
        item.addEventListener(ON_CLICK, event -> {
            try {
                if (SUB_MENU_FOR_PD.equals(event.getTarget().getAttribute(SUB_MENU_FOR))) {
                    viewPdOrLogFilter(null, false);
                } else if (SUB_MENU_FOR_LOG_FILTER.equals(event.getTarget().getAttribute(SUB_MENU_FOR))) {
                    viewPdOrLogFilter(null, true);
                } else if (SUB_MENU_FOR_DASHBOARD.equals(event.getTarget().getAttribute(SUB_MENU_FOR))) {
                    viewAllExistingDashboard();
                }
            } catch (Exception ex) {
                LOGGER.error("Error in forwarding the request", ex);
            }
        });
        menuPopup.appendChild(item);
    }

    private void viewAllExistingDashboard() {
        try {
            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put("createNewDashboard", false);
            attrMap.put("forwardFromPopup", true);
            PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_DASHBOARD);
            plugin.setSimpleParams(attrMap);
            plugin.execute(getPortalContext());
        } catch (Exception e) {
            LOGGER.error("Error in showing the Dashboard", e);
        }
    }

    private void viewExistingDashboard(UserMetadataSummaryType um) {
        try {
            Sessions.getCurrent().setAttribute("logSummaries", Collections.singletonList(logSummaryType));
            Sessions.getCurrent()
                .setAttribute("userMetadata_dash", userMetaDataUtilService.getUserMetaDataById(um.getId()));
            Clients.evalJavaScript("window.open('dashboard/index.zul')");
        } catch (Exception e) {
            LOGGER.error("Error in showing the Dashboard", e);
        }
    }

    private void addMenuItem(Menupopup popup, UserMetadataSummaryType um, boolean visibleOnLoad, String subMenuFor) {
        Menuitem item = new Menuitem();
        item.setLabel(um.getName());
        item.setAttribute(SUB_MENU_FOR, subMenuFor);
        item.setAttribute(USER_META_DATA, um);
        item.addEventListener(ON_CLICK, event -> {
            try {
                if (SUB_MENU_FOR_PD.equals(event.getTarget().getAttribute(SUB_MENU_FOR))) {
                    viewPdOrLogFilter((UserMetadataSummaryType) event.getTarget().getAttribute(USER_META_DATA), false);
                } else if (SUB_MENU_FOR_LOG_FILTER.equals(event.getTarget().getAttribute(SUB_MENU_FOR))) {
                    viewPdOrLogFilter((UserMetadataSummaryType) event.getTarget().getAttribute(USER_META_DATA), true);
                } else if (SUB_MENU_FOR_DASHBOARD.equals(event.getTarget().getAttribute(SUB_MENU_FOR))) {
                    viewExistingDashboard((UserMetadataSummaryType) event.getTarget().getAttribute(USER_META_DATA));
                }
            } catch (Exception ex) {
                LOGGER.error("Error in forwarding the request", ex);
            }
        });
        item.setVisible(visibleOnLoad);
        popup.appendChild(item);
    }

    private void viewPdOrLogFilter(UserMetadataSummaryType um, boolean edit) {
        try {
            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put("FORWARD_FROM_CONTEXT_MENU", true);
            attrMap.put("EDIT_FILTER", edit);
            if (um != null) {
                attrMap.put("USER_METADATA_SUM", um.getId());
            }
            PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
            plugin.setSimpleParams(attrMap);
            plugin.execute(getPortalContext());
        } catch (Exception e) {
            LOGGER.error("Error in showing the filter log discover model", e);
        }
    }


    private void addSubMenuDashboard() {
        String subMenuImage = getSubMenuImage(PluginCatalog.PLUGIN_DASHBOARD_SUB_MENU);
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel(Labels.getLabel("plugin_analyze_dashboard_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_CREATE_NEW_DASHBOARD));
            fetchAndConstructMenu(menuPopup,
                userMetaDataUtilService.getUserMetadataSummariesForDashboard(logSummaryType.getId()),
                SUB_MENU_FOR_DASHBOARD, true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void addSubMenuFilter() {
        String subMenuImage = getSubMenuImage(PluginCatalog.PLUGIN_LOG_FILTER_SUB_MENU);
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel(Labels.getLabel("plugin_create_Edit_filter_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_CREATE_NEW_LOG_FILTER));
            fetchAndConstructMenu(menuPopup,
                userMetaDataUtilService.getUserMetadataSummariesForFilter(logSummaryType.getId()),
                SUB_MENU_FOR_LOG_FILTER,
                true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void addSubMenuCalendar() {
        String subMenuImage = getSubMenuImage(PluginCatalog.PLUGIN_APPLY_CALENDAR_SUB_MENU);
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel(Labels.getLabel("plugin_apply_calendar_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_CREATE_NEW_CALENDAR));
            CalendarModel calendarModel = null;
            if (mainController.getEventLogService().getCalendarIdFromLog(logSummaryType.getId()) > 0) {
                calendarModel = mainController.getEventLogService().getCalendarFromLog(logSummaryType.getId());
            }
            fetchAndConstructMenuForCalendar(menuPopup, calendarModel, SUB_MENU_FOR_CALENDAR, true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }


    }

    private String getSubMenuImage(String subMenuId) {
        String subMenuImagePath = null;
        try {
            UserType currentUser = UserSessionManager.getCurrentUser();
            if (currentUser == null) {
                return null;
            }
            if (PluginCatalog.PLUGIN_DISCOVER_MODEL_SUB_MENU.equals(subMenuId)) {
                if (!currentUser
                    .hasAnyPermission(PermissionType.MODEL_DISCOVER_EDIT, PermissionType.MODEL_DISCOVER_VIEW)) {
                    LOGGER.info("User '{}' does not have permission to access process discoverer",
                        currentUser.getUsername());
                    return null;
                }
                subMenuImagePath = "~./themes/ap/common/img/icons/model-discover.svg";
            } else if (PluginCatalog.PLUGIN_DASHBOARD_SUB_MENU.equals(subMenuId)) {
                if (!currentUser.hasAnyPermission(PermissionType.DASH_EDIT, PermissionType.DASH_VIEW)) {
                    LOGGER.info("User '{}' does not have permission to access dashboard",
                        currentUser.getUsername());
                    return null;
                }
                subMenuImagePath = "~./themes/ap/common/img/icons/dashboard.svg";
            } else if (PluginCatalog.PLUGIN_LOG_FILTER_SUB_MENU.equals(subMenuId)) {
                if (!currentUser
                    .hasAnyPermission(PermissionType.FILTER_VIEW, PermissionType.FILTER_EDIT)) {
                    LOGGER.info("User '{}' does not have permission to access log filter",
                        currentUser.getUsername());
                    return null;
                }
                subMenuImagePath = "~./themes/ap/common/img/icons/filter.svg";
            } else if (PluginCatalog.PLUGIN_APPLY_CALENDAR_SUB_MENU.equals(subMenuId)) {
                if (!currentUser.hasAnyPermission(PermissionType.CALENDAR)) {
                    LOGGER.info("User '{}' does not have permission to access calendar",
                        currentUser.getUsername());
                    return null;
                }
                subMenuImagePath = "~./themes/ap/common/img/icons/calendar.svg";
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOGGER.error("Error in retrieving user permission", ex);
        }
        return subMenuImagePath;
    }

    private boolean pluginAvailable(String pluginId) {
        PortalPlugin plugin = portalPluginMap.get(pluginId);
        if (plugin == null) {
            LOGGER.warn("Missing menu item or plugin ");
            return false;
        }
        PortalPlugin.Availability availability = plugin.getAvailability();
        return !(availability == PortalPlugin.Availability.UNAVAILABLE || availability == PortalPlugin.Availability.HIDDEN);
    }

    private PortalContext getPortalContext() {
        return (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
    }

}
