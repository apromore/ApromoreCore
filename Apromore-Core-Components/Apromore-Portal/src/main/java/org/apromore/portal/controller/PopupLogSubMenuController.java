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
import org.apromore.portal.controller.helper.UserMetaDataUtilService;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.PopupMenuController;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.UserMetadataSummaryType;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;

abstract class PopupLogSubMenuController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(PopupLogSubMenuController.class);

    protected PopupMenuController popupMenuController;
    protected MainController mainController;
    protected Menupopup popupMenu;
    protected final LogSummaryType logSummaryType;
    private Map<String, PortalPlugin> portalPluginMap;
    protected UserMetaDataUtilService userMetaDataUtilService;
    private static final String ON_CLICK = "onClick";
    private static final String CENTRE_ALIGN = "vertical-align: middle; text-align:left;color: var(--ap-c-widget)";
    private static final int SUBMENU_SIZE = 5;
    protected static final String SUB_MENU_FOR_PD = "PD";
    protected static final String SUB_MENU_FOR_LOG_FILTER = "LOG_FILTER";
    protected static final String SUB_MENU_FOR_DASHBOARD = "DASHBOARD";
    protected static final String SUB_MENU_FOR_CALENDAR = "CALENDAR";
    private static final String SUB_MENU_FOR = "SUB_MENU_FOR";
    private static final String USER_META_DATA = "USER_META_DATA";
    private static final String CALENDAR_DATA = "CALENDAR_DATA";


    protected PopupLogSubMenuController(PopupMenuController popupMenuController, MainController mainController,
                                     Menupopup popupMenu,
                                     LogSummaryType logSummaryType) {
        this.popupMenuController = popupMenuController;
        this.mainController = mainController;
        this.popupMenu = popupMenu;
        this.logSummaryType = logSummaryType;
        portalPluginMap = PortalPluginResolver.getPortalPluginMap();
        userMetaDataUtilService = new UserMetaDataUtilService();
    }

    protected void fetchAndConstructMenu(Menupopup menuPopup, List<UserMetadataSummaryType> summaryTypes,
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

    protected void fetchAndConstructMenuForCalendar(Menupopup menuPopup, CalendarModel calendarModel, String subMenuFor,
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
    private PortalContext getPortalContext() {
        return (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
    }

}
