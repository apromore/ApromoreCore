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

package org.apromore.portal.dialogController;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.LabelConstants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.menu.MenuConfig;
import org.apromore.portal.menu.MenuConfigLoader;
import org.apromore.portal.menu.MenuGroup;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.PermissionType;
import org.apromore.portal.model.UserType;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.Menu;
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
	private static final String DISPLAY_NAME_EXP = "displayName";
	private static final String GROUP = "group";
	private static final String ON_CLICK = "onClick";
	private boolean popUpOnTree=false;
	private FolderType selectedFolder=null;
	private String popupType;

	@Override
	public void doAfterCompose(Menupopup menuPopup) {
		try {
			popupType = (String) Executions.getCurrent().getArg().get("POPUP_TYPE");
			if (popupType != null && !PortalPluginResolver.resolve().isEmpty()) {
				switch (popupType) {
					case "PROCESS": loadPopupMenu(menuPopup, "process-popup-menu"); break;
					case "LOG":     loadPopupMenu(menuPopup, "log-popup-menu"); break;
					case "CANVAS":  loadPopupMenu(menuPopup, "canvas-popup-menu"); break;
					case "FOLDER":
						selectedFolder=(FolderType)Executions.getCurrent().getArg().get("SELECTED_FOLDER");
						loadPopupMenu(menuPopup, "folder-popup-menu");
						break;
					case "FOLDER_TREE":
						popUpOnTree=true;
						selectedFolder=(FolderType)Executions.getCurrent().getArg().get("SELECTED_FOLDER");
						loadPopupMenu(menuPopup, "folder-popup-menu");
						break;
					case "ROOT_FOLDER_TREE" :
						popUpOnTree=true;
						selectedFolder=(FolderType)Executions.getCurrent().getArg().get("SELECTED_FOLDER");
						loadPopupMenu(menuPopup, "root-folder-popup-menu");
						break;
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error in Rendering popup menu", ex);
		}
	}

	private void addMenuitem(Menupopup popup, MenuItem menuItem) {
		switch (menuItem.getId()) {
			case PluginCatalog.PLUGIN_CUT:     addCutMenuItem(popup); return;
			case PluginCatalog.PLUGIN_COPY:    addCopyMenuItem(popup); return;
			case PluginCatalog.PLUGIN_PASTE:   addPasteMenuItem(popup); return;
			case PluginCatalog.PLUGIN_SHARE:   addShareMenuItem(popup); return;
			case PluginCatalog.ITEM_SEPARATOR: addMenuSeparator(popup); return;
			case PluginCatalog.PLUGIN_DELETE_MENU: addDeleteMenuItem(popup); return;
			case PluginCatalog.PLUGIN_RENAME_MENU: addRenameMenuItem(popup); return;
			case PluginCatalog.PLUGIN_CREATE_NEW_CALENDAR:   addNewCalendarMenuItem(popup); return;
			case PluginCatalog.PLUGIN_EXISTING_CALENDAR:     addExistingCalendarMenuItem(popup); return;
			case PluginCatalog.PLUGIN_CREATE_NEW_DASHBOARD:   addNewDashboardMenuItem(popup); return;
			case PluginCatalog.PLUGIN_VIEW_EXISTING_DASHBOARD:   addExistingDashboardMenuItem(popup); return;
			case PluginCatalog.PLUGIN_VIEW_FULL_LOG_DISCOVER_MODEL:   addFullLogDiscoverModelMenuItem(popup); return;
			case PluginCatalog.PLUGIN_VIEW_FILTER_LOG_DISCOVER_MODEL:   addViewLogFilterMenuItem(popup); return;
			case PluginCatalog.PLUGIN_CREATE_NEW_LOG_FILTER:   addNewLogFilterMenuItem(popup); return;
			case PluginCatalog.PLUGIN_VIEW_EXISTING_LOG_FILTER:   addExistingLogFilterViewMenuItem(popup); return;
			case PluginCatalog.PLUGIN_DISCOVER_MODEL_SUB_MENU:
			case PluginCatalog.PLUGIN_DASHBOARD_SUB_MENU:
			case PluginCatalog.PLUGIN_LOG_FILTER_SUB_MENU:
			case PluginCatalog.PLUGIN_APPLY_CALENDAR_SUB_MENU: addSubMenuItem(popup,menuItem.getId()); return;
		}
		addPluginMenuitem(popup, menuItem);  // handle null or unknown menuitem id
	}

	private void addExistingLogFilterViewMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("plugin_existing_log_filter_text"));
		item.setImage("~./themes/ap/common/img/icons/list.svg");
		item.addEventListener(ON_CLICK, event -> {
			try {
				PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
				Map<String, Object> attrMap = new HashMap<>();
				attrMap.put("FORWARD_FROM_CONTEXT_MENU",true);
				attrMap.put("EDIT_FILTER",true);
				plugin.setSimpleParams(attrMap);
				plugin.execute((PortalContext) Sessions.getCurrent().getAttribute("portalContext"));
			} catch (Exception e) {
				LOGGER.error("Error in showing the log filter", e);
			}
		});
		popup.appendChild(item);
	}

	private void addViewLogFilterMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("portal_filter_log_discover_model"));
		item.setImage("~./themes/ap/common/img/icons/list.svg");
		item.addEventListener(ON_CLICK, event -> {
			try {
				PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
				Map<String, Object> attrMap = new HashMap<>();
				attrMap.put("FORWARD_FROM_CONTEXT_MENU",true);
				attrMap.put("EDIT_FILTER",false);
				plugin.setSimpleParams(attrMap);
				plugin.execute((PortalContext) Sessions.getCurrent().getAttribute("portalContext"));
			} catch (Exception e) {
				LOGGER.error("Error in showing the filter log discover model", e);
			}
		});
		popup.appendChild(item);
	}

	private void addNewLogFilterMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("plugin_new_log_filter_text"));
		item.setIconSclass("z-icon-plus-circle");
		item.addEventListener(ON_CLICK, event -> {
			try {
				PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_FILTER_LOG);
				plugin.setSimpleParams(null);
				plugin.execute((PortalContext) Sessions.getCurrent().getAttribute("portalContext"));
			} catch (Exception e) {
				LOGGER.error("Error in showing log filter", e);
			}
		});
		popup.appendChild(item);
	}

	private void addFullLogDiscoverModelMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("portal_full_log_discover_model"));
		item.setImage("~./themes/ap/common/img/icons/filter.svg");
		item.addEventListener(ON_CLICK, event -> {
			try {
				PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_DISCOVER_MODEL);
				plugin.execute((PortalContext) Sessions.getCurrent().getAttribute("portalContext"));
			} catch (Exception e) {
				LOGGER.error("Error in showing the full log discover model", e);
			}
		});
		popup.appendChild(item);
	}

	private void addExistingCalendarMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("portal_existing_calendars"));
		item.setImage("~./themes/ap/common/img/icons/list.svg");
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
		item.setIconSclass("z-icon-plus-circle");
		item.addEventListener(ON_CLICK,  event ->  createNewCalendar());
		popup.appendChild(item);
	}

	private void addNewDashboardMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("portal_create_new_dashboard"));
		item.setIconSclass("z-icon-plus-circle");
		item.addEventListener(ON_CLICK,  event ->  createNewDashboard());
		popup.appendChild(item);
	}

	private void addExistingDashboardMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("portal_existing_dashboards"));
		item.setImage("~./themes/ap/common/img/icons/list.svg");
		item.addEventListener(ON_CLICK,  event ->  viewExistingDashboard());
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
			PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
			Map<String, Object> attrMap = new HashMap<>();
			attrMap.put("portalContext", portalContext);
			attrMap.put("artifactName", selectedItem.getName());
			attrMap.put("logId", selectedItem.getId());
			attrMap.put("createNewCalendar", true);
			PortalPlugin calendarPlugin = portalPluginMap.get(PluginCatalog.PLUGIN_CALENDAR);
			calendarPlugin.setSimpleParams(attrMap);
			calendarPlugin.execute(portalContext);
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
			plugin.execute((PortalContext) Sessions.getCurrent().getAttribute("portalContext"));
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
			plugin.execute((PortalContext) Sessions.getCurrent().getAttribute("portalContext"));
		} catch (Exception e) {
			LOGGER.error("Error in showing the Dashboard", e);
		}

	}

	private void addSubMenuItem(Menupopup popup, String subMenuId) {
		try {
			PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
			String subMenuImagePath ;
			if (PluginCatalog.PLUGIN_DISCOVER_MODEL_SUB_MENU.equals(subMenuId)) {
				if (!portalContext.getCurrentUser().hasAnyPermission(PermissionType.MODEL_DISCOVER_EDIT,
					PermissionType.MODEL_DISCOVER_VIEW)) {
					LOGGER.info("User '{}' does not have permission to access process discoverer",
						portalContext.getCurrentUser().getUsername());
					return;
				}
				subMenuImagePath = "~./themes/ap/common/img/icons/model-discover.svg";
			} else if (PluginCatalog.PLUGIN_DASHBOARD_SUB_MENU.equals(subMenuId)) {
				if (!portalContext.getCurrentUser().hasAnyPermission(PermissionType.DASH_EDIT,
					PermissionType.DASH_VIEW)) {
					LOGGER.info("User '{}' does not have permission to access dashboard",
						portalContext.getCurrentUser().getUsername());
					return;
				}
				subMenuImagePath = "~./themes/ap/common/img/icons/dashboard.svg";
			} else if (PluginCatalog.PLUGIN_LOG_FILTER_SUB_MENU.equals(subMenuId)) {
				if (!portalContext.getCurrentUser().hasAnyPermission(PermissionType.FILTER_VIEW,
					PermissionType.FILTER_EDIT)) {
					LOGGER.info("User '{}' does not have permission to access log filter",
						portalContext.getCurrentUser().getUsername());
					return;
				}
				subMenuImagePath = "~./themes/ap/common/img/icons/filter.svg";
			} else if (PluginCatalog.PLUGIN_APPLY_CALENDAR_SUB_MENU.equals(subMenuId)) {
				subMenuImagePath = "~./themes/ap/common/img/icons/calendar.svg";
			} else {
				return;
			}

			MenuConfig menuConfig = menuConfigLoader.getMenuConfig(subMenuId);
			if (menuConfig.getGroups().isEmpty())
				return;

			MenuGroup menuGroup = menuConfig.getGroups().get(0);
			if (!menuGroup.getItems().isEmpty()) {
				Menu subMenu = new Menu();
				subMenu.setLabel(getGroupLabel(menuGroup));
				subMenu.setImage(subMenuImagePath);
				Menupopup menuPopup = new Menupopup();
				for (MenuItem menuItem : menuGroup.getItems()) {
					addMenuitem(menuPopup, menuItem);
				}
				subMenu.appendChild(menuPopup);
				popup.appendChild(subMenu);
			}

		} catch (Exception e) {
			LOGGER.error("Failed to load menu", e);
		}
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
		item.setDisabled(plugin.getAvailability() == PortalPlugin.Availability.DISABLED);
		item.addEventListener(ON_CLICK, event -> {
			PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
			plugin.execute(portalContext);
		});
		popup.appendChild(item);
	}

	private void addRenameMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("portal_rename_hint"));
		item.setImage("~./themes/ap/common/img/icons/rename.svg");
		item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().rename(selectedFolder)
			: event -> getBaseListboxController().rename());
		popup.appendChild(item);
	}

	private void addDeleteMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("portal_delete_hint"));
		item.setImage("~./themes/ap/common/img/icons/trash.svg");
		item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().removeFolder(selectedFolder)
			: event -> getBaseListboxController().removeFolder());
		popup.appendChild(item);
	}

	private void addCutMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("common_cut_text"));
		item.setImage("~./themes/ap/common/img/icons/cut.svg");
		item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().cut(selectedFolder)
			: event -> getBaseListboxController().cut());
		popup.appendChild(item);
	}

	private void addCopyMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("common_copy_text"));
		item.setImage("~./themes/ap/common/img/icons/copy.svg");
		item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().copy(selectedFolder)
			: event -> getBaseListboxController().copy());
		popup.appendChild(item);
	}

	private void addPasteMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		if("FOLDER".equals(popupType) ||"FOLDER_TREE".equals(popupType)||"ROOT_FOLDER_TREE".equals(popupType)) {
			item.setLabel(Labels.getLabel("common_paste_within_text"));
		}else {
			item.setLabel(Labels.getLabel("common_paste_text"));
		}
		item.setImage("~./themes/ap/common/img/icons/paste.svg");
		item.addEventListener(ON_CLICK,	popUpOnTree ? event -> getNavigationController().paste(selectedFolder)
			:(selectedFolder!=null?event -> getBaseListboxController().paste(selectedFolder): event -> getBaseListboxController().paste()));
		if(getBaseListboxController().getMainController().getCopyPasteController().getSelectedItemsSize()==0) {
			item.setDisabled(true);
			item.setStyle("pointer-events:none");
		}
		popup.appendChild(item);
	}

	private void addShareMenuItem(Menupopup popup) {
		Menuitem item = new Menuitem();
		item.setLabel(Labels.getLabel("portal_share_hint"));
		item.setImage("~./themes/ap/common/img/icons/share.svg");
		item.addEventListener(ON_CLICK, popUpOnTree ? event -> getNavigationController().share(selectedFolder)
			: event -> getBaseListboxController().share());
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

			portalPluginMap = PortalPluginResolver.getPortalPluginMap();
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

	private String getGroupLabel(MenuGroup group) {
		String groupId = group.getId();
		String groupLabelExp = group.getLabelExp();
		String groupLabelKey = group.getLabelKey();
		String groupLabel = null;
		if (groupLabelExp != null) {
			if (DISPLAY_NAME_EXP.equals(groupLabelExp)) {
				UserType userType = UserSessionManager.getCurrentUser();
				if (userType != null) {
					groupLabel = getDisplayName(userType);
				}
			}
		} else if (groupLabelKey != null){
			groupLabel = Labels.getLabel(groupLabelKey, groupId);
		}
		if (groupLabel == null) {
			groupLabel = Labels.getLabel(
				MessageFormat.format("plugin_{0}_title_text", groupId.toLowerCase()),
				groupId
			);
		}
		return groupLabel;
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

	private BaseListboxController getBaseListboxController() {
		PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
		MainController mainController = (MainController) portalContext.getMainController();

		return mainController.getBaseListboxController();
	}
	private NavigationController getNavigationController() {
		PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
		MainController mainController = (MainController) portalContext.getMainController();

		return mainController.getNavigationController();
	}
}
