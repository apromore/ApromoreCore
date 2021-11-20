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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.LabelConstants;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.menu.MenuConfig;
import org.apromore.portal.menu.MenuConfigLoader;
import org.apromore.portal.menu.MenuGroup;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.UserType;
import org.slf4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
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
                }

		addPluginMenuitem(popup, menuItem);  // handle null or unknown menuitem id
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
		if("FOLDER".equals(popupType) ||"FOLDER_TREE".equals(popupType)) {
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
		if (!(popup.getLastChild() instanceof Menuseparator)) {
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
