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

package org.apromore.portal.dialogController;

import static org.apromore.portal.menu.PluginCatalog.PLUGIN_PREDICTOR_MANAGER_SUB_MENU;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalContexts;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.LabelConstants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.controller.CalendarPopupLogSubMenuController;
import org.apromore.portal.controller.DashboardPopupLogSubMenuController;
import org.apromore.portal.controller.DiscoverPopupLogSubMenuController;
import org.apromore.portal.controller.FilterPopupLogSubMenuController;
import org.apromore.portal.controller.LogFilterPopupLogSubMenuController;
import org.apromore.portal.controller.PredictorTrainerSubMenuController;
import org.apromore.portal.menu.MenuConfig;
import org.apromore.portal.menu.MenuConfigLoader;
import org.apromore.portal.menu.MenuGroup;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Messagebox;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Strings;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class PopupMenuController extends SelectorComposer<Menupopup> {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(BaseMenuController.class);

    protected transient MenuConfigLoader menuConfigLoader;
    protected transient Map<String, PortalPlugin> portalPluginMap;
    private static final String GROUP = "group";
    private static final String ON_CLICK = "onClick";
    private static final String ICON_PLUS = "z-icon-plus-circle";
    private static final String LIST_ICON = "~./themes/ap/common/img/icons/list.svg";
    private boolean popUpOnTree = false;
    private FolderType selectedFolder = null;
    private String popupType;
    private static final String POPUP_MENU_PROCESS = "PROCESS";
    private static final String POPUP_MENU_LOG = "LOG";
    private MainController mainController;
    private  int countLog = 0;
    private static final String FAILED_LOAD_SUB_MENU="Failed to load sub menu";
    @Override
    public void doAfterCompose(Menupopup menuPopup) {
        try {
            Object parent = Executions.getCurrent().getArg().get("PARENT_CONTROLLER");
            if(parent instanceof MainController ) {
                mainController = (MainController)parent;
            }
            if(mainController==null){
                mainController=(MainController)getPortalContext().getMainController();
            }
            portalPluginMap = PortalPluginResolver.getPortalPluginMap();
            popupType = (String) Executions.getCurrent().getArg().get("POPUP_TYPE");
            if (getBaseListboxController().getSelection().size() > 1 &&
                (popupType.equals(POPUP_MENU_LOG) || popupType.equals(POPUP_MENU_PROCESS)) &&
                handleMenuForMultipleSelection(menuPopup)
                ) {
                return; // Customize Menu will Open for multiple selection
            }
            if (popupType != null && !PortalPluginResolver.resolve().isEmpty()) {
                switch (popupType) {
                    case POPUP_MENU_PROCESS:
                        loadPopupMenu(menuPopup, "process-popup-menu");
                        break;
                    case POPUP_MENU_LOG:
                        loadPopupMenu(menuPopup, "log-popup-menu");
                        break;
                    case "CANVAS":
                        loadPopupMenu(menuPopup, "canvas-popup-menu");
                        break;
                    case "FOLDER":
                        selectedFolder = (FolderType) Executions.getCurrent().getArg().get("SELECTED_FOLDER");
                        loadPopupMenu(menuPopup, "folder-popup-menu");
                        break;
                    case "FOLDER_TREE":
                        popUpOnTree = true;
                        selectedFolder = (FolderType) Executions.getCurrent().getArg().get("SELECTED_FOLDER");
                        loadPopupMenu(menuPopup, "folder-popup-menu");
                        break;
                    case "ROOT_FOLDER_TREE":
                        popUpOnTree = true;
                        selectedFolder = (FolderType) Executions.getCurrent().getArg().get("SELECTED_FOLDER");
                        loadPopupMenu(menuPopup, "root-folder-popup-menu");
                        break;
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Error in Rendering popup menu", ex);
        }
    }

    public void addMenuitem(Menupopup popup, MenuItem menuItem) {
        switch (menuItem.getId()) {
            case PluginCatalog.PLUGIN_CUT:
                addCutMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_COPY:
                addCopyMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_PASTE:
                addPasteMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_SHARE:
                addShareMenuItem(popup);
                return;
            case PluginCatalog.ITEM_SEPARATOR:
                addMenuSeparator(popup);
                return;
            case PluginCatalog.PLUGIN_DELETE_MENU:
                addDeleteMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_RENAME_MENU:
                addRenameMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_CREATE_NEW_CALENDAR:
                addNewCalendarMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_EXISTING_CALENDAR:
                addExistingCalendarMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_CREATE_NEW_DASHBOARD:
                addNewDashboardMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_VIEW_EXISTING_DASHBOARD:
                addExistingDashboardMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_VIEW_FULL_LOG_DISCOVER_MODEL:
                addFullLogDiscoverModelMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_VIEW_FILTER_LOG_DISCOVER_MODEL:
                addViewLogFilterMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_CREATE_NEW_LOG_FILTER:
                addNewLogFilterMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_CREATE_NEW_FILTER:
                addNewFilterMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_VIEW_EXISTING_LOG_FILTER:
                addExistingLogFilterViewMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_CREATE_NEW_PREDICTOR:
                addNewPredictorMenuItem(popup);
                return;
            case PluginCatalog.PLUGIN_DISCOVER_MODEL_SUB_MENU:
            case PluginCatalog.PLUGIN_DASHBOARD_SUB_MENU:
            case PluginCatalog.PLUGIN_LOG_FILTER_SUB_MENU:
            case PluginCatalog.PLUGIN_FILTER_SUB_MENU:
            case PluginCatalog.PLUGIN_APPLY_CALENDAR_SUB_MENU:
            case PLUGIN_PREDICTOR_MANAGER_SUB_MENU:
                addSubMenuItem(popup,menuItem.getId());
                return;
        }
        addPluginMenuitem(popup, menuItem);  // handle null or unknown menuitem id
    }

    private void addNewPredictorMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel("Create new predictor");
        item.setImage("~./icons/predictor-add-top.svg");
        item.addEventListener(ON_CLICK, event -> createNewPredictorTrainer());
        popup.appendChild(item);
    }

    private void createNewPredictorTrainer() {
        try {
            PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_PREDICTOR_TRAINER);
            plugin.execute(getPortalContext());
        } catch (Exception e) {
            LOGGER.error("Error in showing New Predictor trainer", e);
        }
    }

    private void addExistingLogFilterViewMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("plugin_existing_log_filter_text"));
        item.setImage(LIST_ICON);
        item.addEventListener(ON_CLICK, event -> {
            try {
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
                Map<String, Object> attrMap = new HashMap<>();
                attrMap.put("FORWARD_FROM_CONTEXT_MENU", true);
                attrMap.put("EDIT_FILTER", true);
                plugin.setSimpleParams(attrMap);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                LOGGER.error("Error in showing the log filter", e);
            }
        });
        popup.appendChild(item);
    }

    private boolean handleMenuForMultipleSelection(Menupopup menuPopup) {
        int countProcess = 0;
        for (Object obj : getBaseListboxController().getListModel().getSelection()) {
            if (obj instanceof FolderType) {
                return false; // if there is any folder selected then right-click menu will work normally
            } else if (obj instanceof LogSummaryType) {
                countLog++;
            } else if (obj instanceof ProcessSummaryType) {
                countProcess++;
            }
        }
        List<MenuItem> menuItems = new ArrayList<>();
        if (countProcess == 2 && countLog == 0) {
            //When two models are selected, and the user right-clicks on one of them, show a menu with:
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_COMPARE_MODELS));
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_MERGE_MODELS));
            menuItems.add(new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_DOWNLOAD));
        } else if (countProcess == 1 && countLog == 1) {
            //when a model and a log are selected, and the user right-clicks on one of them, show a menu with:
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_ANIMATE_LOG));// Will be Singular

            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_CHECK_CONFORMANCE));
            menuItems.add(new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_DOWNLOAD));
        } else if (countProcess == 0 && (countLog >= 2 && countLog <= 5)) {
            // when two or more logs are selected (up to 5), and the user right-clicks on one of them, show a menu with:
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_DASHBOARD)); //Need to modify it
            menuItems.add(new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_DOWNLOAD));
        } else if (countProcess == 1 && (countLog >= 1 && countLog <= 5)) {
            // when a model and up to 5 logs are selected, and the user right-clicks on one of them, show a menu with
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_ANIMATE_LOG));
            menuItems.add(new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_DOWNLOAD));
        }else{
            menuItems.add(new MenuItem(PluginCatalog.PLUGIN_DOWNLOAD));
        }
        for (MenuItem menuItem : menuItems) {
            addMenuitem(menuPopup, menuItem);
        }
        return !menuItems.isEmpty();
    }

    private void addViewLogFilterMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("portal_filter_log_discover_model"));
        item.setImage(LIST_ICON);
        item.addEventListener(ON_CLICK, event -> {
            try {
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
                Map<String, Object> attrMap = new HashMap<>();
                attrMap.put("FORWARD_FROM_CONTEXT_MENU", true);
                attrMap.put("EDIT_FILTER", false);
                plugin.setSimpleParams(attrMap);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                LOGGER.error("Error in showing the filter log discover model", e);
            }
        });
        popup.appendChild(item);
    }

    private void addNewLogFilterMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("plugin_new_log_filter_text"));
        item.setImage("~./themes/ap/common/img/icons/add-filtered-log.svg");
        item.addEventListener(ON_CLICK, event -> {
            try {
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
                plugin.setSimpleParams(null);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                LOGGER.error("Error in showing log filter", e);
            }
        });
        popup.appendChild(item);
    }
    private void addNewFilterMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("plugin_new_filter_text"));
        item.setIconSclass(ICON_PLUS);
        item.addEventListener(ON_CLICK, event -> {
            try {
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
                plugin.setSimpleParams(null);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                LOGGER.error("Error in showing log filter", e);
            }
        });
        popup.appendChild(item);
    }

    private void addFullLogDiscoverModelMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("portal_full_log_discover_model"));
        item.setImage("~./themes/ap/common/img/icons/log.svg");
        item.addEventListener(ON_CLICK, event -> {
            try {
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_DISCOVER_MODEL);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                LOGGER.error("Error in showing the full log discover model", e);
            }
        });
        popup.appendChild(item);
    }

    private void addExistingCalendarMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("portal_existing_calendars"));
        item.setImage(LIST_ICON);
        item.addEventListener(ON_CLICK, event -> {
            Set<Object> selections = getBaseListboxController().getSelection();
            if (selections.size() != 1 || !selections.iterator().next().getClass().equals(LogSummaryType.class)) {
                Notification.error(Labels.getLabel("portal_selectOneLog_message"));
                return;
            }
            LogSummaryType selectedItem = (LogSummaryType) selections.iterator().next();
            getBaseListboxController().launchCalendar(selectedItem.getName(), selectedItem.getId());
        });
        popup.appendChild(item);

    }

    private void addNewCalendarMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("portal_create_new_calendar"));
        item.setImage("~./themes/ap/common/img/icons/add-calendar.svg");
        item.addEventListener(ON_CLICK, event -> createNewCalendar());
        popup.appendChild(item);
    }

    private void addNewDashboardMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.addSclass("ap-context-menu-item");
        item.setLabel(Labels.getLabel("portal_create_new_dashboard"));
        item.setImage("~./themes/ap/common/img/icons/add-dashboard.svg");
        item.addEventListener(ON_CLICK, event -> createNewDashboard());
        popup.appendChild(item);
    }

    private void addExistingDashboardMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("portal_existing_dashboards"));
        item.setImage(LIST_ICON);
        item.addEventListener(ON_CLICK, event -> viewExistingDashboard());
        popup.appendChild(item);
    }


    private void createNewCalendar() {
        try {
            Set<Object> selections = getBaseListboxController().getSelection();
            if (selections.size() != 1 || !selections.iterator().next().getClass().equals(LogSummaryType.class)) {
                Notification.error(Labels.getLabel("portal_selectOneLog_message"));
                return;
            }
            LogSummaryType selectedItem = (LogSummaryType) selections.iterator().next();
            boolean canEdit = ItemHelpers.canModifyCalendar(this.mainController.getUserService()
                .findUserByRowGuid(UserSessionManager.getCurrentUser().getId()), selectedItem.getId());
            if (canEdit) {
                PortalContext portalContext = getPortalContext();
                Map<String, Object> attrMap = new HashMap<>();
                attrMap.put("portalContext", portalContext);
                attrMap.put("artifactName", selectedItem.getName());
                attrMap.put("logId", selectedItem.getId());
                attrMap.put("FOWARD_FROM_CONTEXT", true);
                attrMap.put("calendarId", 0L);
                PortalPlugin calendarPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_CALENDAR);
                calendarPlugin.setSimpleParams(attrMap);
                calendarPlugin.execute(portalContext);
            }else{
                Notification.error(Labels.getLabel("portal_unauthorizedRoleAccess_message"));
            }
        } catch (Exception e) {
            LOGGER.error(Labels.getLabel("portal_failedLaunchCustomCalendar_message"), e);
            Messagebox.show(Labels.getLabel("portal_failedLaunchCustomCalendar_message"));
        }
    }

    private void createNewDashboard() {
        try {
            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put("createNewDashboard", true);
            attrMap.put("forwardFromPopup", true);
            PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_DASHBOARD);
            plugin.setSimpleParams(attrMap);
            plugin.execute(getPortalContext());
        } catch (Exception e) {
            LOGGER.error("Error in showing New Dashboard", e);
        }
    }

    private void viewExistingDashboard() {
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

    private void addSubMenuItem(Menupopup popup, String menuId) {
        try {
            Set<Object> selections = getSelections();
            if (selections.isEmpty()) {
                return;
            }
            switch (menuId) {
                case PluginCatalog.PLUGIN_DISCOVER_MODEL_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_DISCOVER_MODEL)) {
                        new DiscoverPopupLogSubMenuController(this, mainController, popup,
                            (LogSummaryType) selections.iterator().next());
                    }
                    return;
                case PluginCatalog.PLUGIN_DASHBOARD_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_DASHBOARD)) {
                        new DashboardPopupLogSubMenuController(this, mainController, popup,
                            (LogSummaryType) selections.iterator().next());
                    }
                    return;
                case PluginCatalog.PLUGIN_LOG_FILTER_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_FILTER_LOG)) {
                        new LogFilterPopupLogSubMenuController(this, mainController, popup,
                            (LogSummaryType) selections.iterator().next());
                    }
                    return;
                case PluginCatalog.PLUGIN_FILTER_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_FILTER_LOG)) {
                        new FilterPopupLogSubMenuController(this, mainController, popup,
                            (LogSummaryType) selections.iterator().next());
                    }
                    return;
                case PluginCatalog.PLUGIN_APPLY_CALENDAR_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_CALENDAR)) {
                        new CalendarPopupLogSubMenuController(this, mainController, popup,
                            (LogSummaryType) selections.iterator().next());
                    }
                    return;
                case PLUGIN_PREDICTOR_MANAGER_SUB_MENU:
                    if (pluginAvailable(PluginCatalog.PLUGIN_PREDICTOR_TRAINER)) {
                        // New log sub menu controller
                        new PredictorTrainerSubMenuController(this, mainController, popup,
                            (LogSummaryType) selections.iterator().next());
                    }
                    return;
                default:
                    return;
            }

        } catch (Exception e) {
            LOGGER.error(FAILED_LOAD_SUB_MENU, e);
        }
    }
    private Set<Object> getSelections() {
        Set<Object> selections = getBaseListboxController().getSelection();
        if (selections.size() != 1 || !selections.iterator().next().getClass().equals(LogSummaryType.class)) {
            return Collections.emptySet();
        }
        return selections;
    }


    private void addPluginMenuitem(Menupopup popup, MenuItem menuItem) {
        String itemId = menuItem.getId();
        PortalPlugin plugin = portalPluginMap.get(itemId);
        if (plugin == null) {
            LOGGER.warn("Missing menu item or plugin " + itemId);
            return;
        }
        PortalPlugin.Availability availability = plugin.getAvailability();
        if (availability == PortalPlugin.Availability.UNAVAILABLE || availability == PortalPlugin.Availability.HIDDEN) {
            return;
        }
        Menuitem item = new Menuitem();
        if (plugin.getResourceAsStream(plugin.getIconPath()) != null) {
            item.setClientDataAttribute("itemId", itemId);
            item.setImage("/portalPluginResource/" + plugin.getIconPath());
        } else {
            item.setImageContent(plugin.getIcon());
        }
        String label = plugin.getLabel(Locale.getDefault());
        item.setLabel(label);
        if (POPUP_MENU_PROCESS.equals(popupType) || (PluginCatalog.PLUGIN_ANIMATE_LOG.equals(itemId) && countLog==1 )) {
            //Modify Label for Model Popup Menu
            getModifiedMenuLabel(plugin, itemId, item);
        }
        item.setDisabled(plugin.getAvailability() == PortalPlugin.Availability.DISABLED);
        item.addEventListener(ON_CLICK, event -> plugin.execute(getPortalContext()));
        popup.appendChild(item);
    }

    private void getModifiedMenuLabel(PortalPlugin plugin, String itemId, Menuitem item) {
        try {
            String popupLabel = null;
            switch (itemId) {
                case PluginCatalog.PLUGIN_EDIT_MODEL:
                    popupLabel = UserSessionManager.getCurrentUser().hasAnyPermission(PermissionType.MODEL_EDIT)
                        ? "plugin_discover_editModelShort_text" : "plugin_discover_viewModelShort_text";
                    break;
                case PluginCatalog.PLUGIN_SIMULATE_MODEL:
                    popupLabel = "plugin_analyze_simulateModelShort_text";
                    break;
                case PluginCatalog.PLUGIN_SEARCH_MODELS:
                    popupLabel = "plugin_redesign_searchModelsShort_text";
                    break;
                case PluginCatalog.PLUGIN_PUBLISH_MODEL:
                    if (plugin.getIconPath() != null && plugin.getIconPath().contains("unlink")) {
                        popupLabel = "plugin_process_publishShort_text";
                    } else {
                        popupLabel = "plugin_process_unpublishShort_text";
                    }
                    break;
                case PluginCatalog.PLUGIN_ANIMATE_LOG:
                    popupLabel = "plugin_analyze_animateLog_text";
                    break;
                default:
                    popupLabel = null;
            }
            if (popupLabel != null) {
                item.setLabel(Labels.getLabel(popupLabel));
            }
        } catch (Exception ex) {
            LOGGER.error("Error occurred in getting label", ex);
        }
    }

    private void addRenameMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("portal_rename_hint"));
        item.setImage("~./themes/ap/common/img/icons/rename.svg");
        item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().rename(selectedFolder) :
            event -> getBaseListboxController().rename());
        popup.appendChild(item);
    }

    private void addDeleteMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("portal_delete_hint"));
        item.setImage("~./themes/ap/common/img/icons/trash.svg");
        item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().removeFolder(selectedFolder) :
            event -> getBaseListboxController().removeFolder());
        popup.appendChild(item);
    }

    private void addCutMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("common_cut_text"));
        item.setImage("~./themes/ap/common/img/icons/cut.svg");
        item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().cut(selectedFolder) :
            event -> getBaseListboxController().cut());
        popup.appendChild(item);
    }

    private void addCopyMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("common_copy_text"));
        item.setImage("~./themes/ap/common/img/icons/copy.svg");
        item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().copy(selectedFolder) :
            event -> getBaseListboxController().copy());
        popup.appendChild(item);
    }

    private void addPasteMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        if ("FOLDER".equals(popupType) || "FOLDER_TREE".equals(popupType) || "ROOT_FOLDER_TREE".equals(popupType)) {
            item.setLabel(Labels.getLabel("common_paste_within_text"));
        } else {
            item.setLabel(Labels.getLabel("common_paste_text"));
        }
        item.setImage("~./themes/ap/common/img/icons/paste.svg");
        item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().paste(selectedFolder) :
            (selectedFolder != null ? event -> getBaseListboxController().paste(selectedFolder) :
                event -> getBaseListboxController().paste()));
        if (getBaseListboxController().getMainController().getCopyPasteController().getSelectedItemsSize() == 0) {
            item.setDisabled(true);
            item.setStyle("pointer-events:none");
        }
        popup.appendChild(item);
    }

    private void addShareMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("portal_share_hint"));
        item.setImage("~./themes/ap/common/img/icons/share.svg");
        item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().share(selectedFolder) :
            event -> getBaseListboxController().share());
        popup.appendChild(item);
    }

    private void addMenuSeparator(Menupopup popup) {
        if (popup.getFirstChild() != null && !(popup.getLastChild() instanceof Menuseparator)) {
            Menuseparator separator = new Menuseparator();
            popup.appendChild(separator);
        }
    }

    public void loadPopupMenu(Menupopup menupop, String menuId) {
        menuConfigLoader = (MenuConfigLoader) SpringUtil.getBean("menuConfigLoader");
        try {
            menuConfigLoader.load();
            MenuConfig menuConfig = menuConfigLoader.getMenuConfig(menuId);

            if (PortalPluginResolver.resolve().isEmpty()) {
                return;
            }
            List<MenuGroup> menuGroups = menuConfig.getGroups();

            List<MenuItem> menuitems = new ArrayList<MenuItem>();
            for (MenuGroup menuGroup : menuGroups) {
                List<MenuItem> menuitem = menuGroup.getItems();
                if (menuitem.isEmpty()) {
                    continue;
                }
                menuitems.addAll(menuitem);
            }

            for (MenuItem menuItem : menuitems) {
                addMenuitem(menupop, menuItem);
            }

        } catch (JsonParseException | JsonMappingException e) {
            LOGGER.error("Failed to parse menu configuration", e);
            loadError();
        } catch (IOException e) {
            LOGGER.error("Failed to read menu configuration", e);
            loadError();
        } catch (Exception e) {
            LOGGER.error("Failed to load menu", e);
            loadError();
        }
    }
    private void loadError() {
        Messagebox.show(Labels.getLabel("portal_failedLoadMenu_message"), "Error", Messagebox.OK, Messagebox.ERROR);
    }

    public static String getDisplayName(UserType userType) {
        String displayName;
        String firstName = userType.getFirstName();
        String lastName = userType.getLastName();

        if (Strings.isNullOrEmpty(firstName)) {
            displayName = (Strings.isNullOrEmpty(lastName)) ? userType.getUsername() : lastName;
        } else {
            displayName = (Strings.isNullOrEmpty(lastName)) ? firstName : lastName + ", " + firstName;
        }
        if (LabelConstants.TRUE.equals(Labels.getLabel(LabelConstants.IS_DISPLAYNAME_CAPITALIZED))) {
            displayName = displayName.toUpperCase();
        }
        return displayName;
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

    private BaseListboxController getBaseListboxController() {
        return mainController.getBaseListboxController();
    }

    private NavigationController getNavigationController() {
        return mainController.getNavigationController();
    }

    private PortalContext getPortalContext() {
        return PortalContexts.getActivePortalContext();
    }
}
