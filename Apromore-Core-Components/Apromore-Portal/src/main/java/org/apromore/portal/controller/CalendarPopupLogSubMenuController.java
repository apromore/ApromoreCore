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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
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
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;

public class CalendarPopupLogSubMenuController extends PopupLogSubMenuController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(CalendarPopupLogSubMenuController.class);

    public CalendarPopupLogSubMenuController(PopupMenuController popupMenuController, MainController mainController,
                                             Menupopup popupMenu, LogSummaryType logSummaryType) {
        super(popupMenuController, mainController, popupMenu, logSummaryType);
        constructMenu();
    }

    private void constructMenu() {
        String subMenuImage = getSubMenuImage();
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel(Labels.getLabel("plugin_apply_calendar_text"));
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_CREATE_NEW_CALENDAR));
            List<CalendarModel> calendarModels=null;
            calendarModels=mainController.getEventLogService().getAllCustomCalendars();
            if(calendarModels==null){
                calendarModels=new ArrayList<>();
            }
            CalendarModel selectedCalendar=null;
            if(mainController.getEventLogService().getCalendarIdFromLog(logSummaryType.getId())>0){
                selectedCalendar=mainController.getEventLogService().getCalendarFromLog(logSummaryType.getId());
            }
            fetchAndConstructMenuForCalendar(menuPopup, calendarModels,selectedCalendar);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void fetchAndConstructMenuForCalendar(Menupopup menuPopup,List<CalendarModel> calendarModels, CalendarModel selectedCalendar) {
        if (!calendarModels.isEmpty()) {
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            int index = 1;
            if(selectedCalendar!=null){
                addMenuItemForCalendar(menuPopup, selectedCalendar, true);
                index++;
            }
            for (CalendarModel calendar : calendarModels) {
                if (index <= SUBMENU_SIZE) {
                    if (selectedCalendar != null && calendar.getId().equals(selectedCalendar.getId())){
                        continue; //skip same calendar
                    }else{
                        addMenuItemForCalendar(menuPopup, calendar, false);
                    }
                    if (index == SUBMENU_SIZE && index < calendarModels.size()) {
                        addOptionToViewMoreMenuItemsForCalendar(menuPopup);
                        break;
                    }
                }
                index++;
            }

        }
    }
    private void addOptionToViewMoreMenuItemsForCalendar(Menupopup menuPopup) {
        Menuitem item = new Menuitem();
        item.setLabel("...");
        item.setStyle(CENTRE_ALIGN);
        item.addEventListener(ON_CLICK, event -> {
            try {
                Long calendarId = mainController.getEventLogService().getCalendarIdFromLog(logSummaryType.getId());
                Map<String, Object> attrMap = new HashMap<>();
                attrMap.put("portalContext", getPortalContext());
                attrMap.put("artifactName", logSummaryType.getName());
                attrMap.put("logId", logSummaryType.getId());
                attrMap.put("calendarId", calendarId);
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

    private void addMenuItemForCalendar(Menupopup popup, CalendarModel calendarModel, boolean selected) {
            Menuitem item = new Menuitem();
            item.setLabel(calendarModel.getName());
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
            if(selected) {
                item.setImage("~./themes/ap/common/img/icons/check-circle.svg");
            }
          popup.appendChild(item);
    }



    private String getSubMenuImage() {
        String subMenuImagePath = null;
        try {
            UserType currentUser = UserSessionManager.getCurrentUser();
            if (currentUser == null) {
                return null;
            }
            if (!currentUser.hasAnyPermission(PermissionType.CALENDAR)) {
                LOGGER.info("User '{}' does not have permission to access calendar", currentUser.getUsername());
                return null;
            }
            subMenuImagePath = "~./themes/ap/common/img/icons/calendar.svg";

        } catch (Exception ex) {
            LOGGER.error("Error in retrieving user permission", ex);
        }
        return subMenuImagePath;
    }

}

