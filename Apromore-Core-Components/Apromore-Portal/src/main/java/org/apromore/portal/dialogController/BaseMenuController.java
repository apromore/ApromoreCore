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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Strings;
import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.portal.PortalContexts;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.LabelConstants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.security.DefaultRoles;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.menu.MenuConfig;
import org.apromore.portal.menu.MenuConfigLoader;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.menu.MenuGroup;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.model.RoleType;
import org.apromore.portal.model.UserType;
import org.apromore.service.EventLogService;
import org.slf4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuseparator;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class BaseMenuController extends SelectorComposer<Menubar> {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(BaseMenuController.class);
    private static final String DISPLAY_NAME_EXP = "displayName";
    private static final String GROUP = "group";
    private static final String ON_CLICK = "onClick";

    protected transient MenuConfigLoader menuConfigLoader;
    protected transient Map<String, PortalPlugin> portalPluginMap;

    @WireVariable("managerClient")
    private ManagerService managerService;

    @WireVariable
    private EventLogService eventLogService;

    @Override
    public void doAfterCompose(Menubar menubar) {
        UserSessionManager.initializeUser(managerService, eventLogService.getConfigBean(), null, null);
    }

    public static String getDisplayName(UserType userType) {
        String displayName;
        String firstName = userType.getFirstName();
        String lastName = userType.getLastName();

        if (!Strings.isNullOrEmpty(firstName)) {
            displayName = (Strings.isNullOrEmpty(lastName)) ? firstName : firstName + " " + lastName;
        } else {
            displayName = (Strings.isNullOrEmpty(lastName)) ? userType.getUsername() : lastName;
        }
        if (LabelConstants.TRUE.equals(Labels.getLabel(LabelConstants.IS_DISPLAYNAME_CAPITALIZED))) {
            displayName = displayName.toUpperCase();
        }
        displayName = getDisplayNameWithRole(displayName, userType);
        return displayName;
    }

    private static String getDisplayNameWithRole(String displayName, UserType userType) {
        StringBuilder roleName = new StringBuilder();
        if (userType.getRoles() != null) {
            for (RoleType role : userType.getRoles()) {
                roleName.append(DefaultRoles.getInstance().getRoleDisplayByName(role.getName()) + ", ");
            }
            if (roleName.length() > 0) {
                displayName = displayName + " - " + (roleName.substring(0, roleName.length() - 2)).toUpperCase();
            }
        }
        return displayName;
    }

    private void loadError() {
        Messagebox.show(Labels.getLabel("portal_failedLoadMenu_message"), "Error", Messagebox.OK, Messagebox.ERROR);
    }
    public void loadMenu(Menubar menubar, String menuId) {
        menuConfigLoader = (MenuConfigLoader) SpringUtil.getBean("menuConfigLoader");
        try {
            menuConfigLoader.load();
            populateMenubar(menubar, menuConfigLoader.getMenuConfig(menuId)); // Create the menubar initially
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
        item.addEventListener(ON_CLICK, event -> plugin.execute(PortalContexts.getActivePortalContext()));
        popup.appendChild(item);
    }

    private void addCutMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("common_cut_text"));
        item.setImage("~./themes/ap/common/img/icons/cut.svg");
        item.addEventListener(ON_CLICK, event -> getBaseListboxController().cut());
        popup.appendChild(item);
    }

    private void addCopyMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("common_copy_text"));
        item.setImage("~./themes/ap/common/img/icons/copy.svg");
        item.addEventListener(ON_CLICK, event -> getBaseListboxController().copy());
        popup.appendChild(item);
    }

    private void addPasteMenuItem(Menupopup popup) {
        Menuitem item = new Menuitem();
        item.setLabel(Labels.getLabel("common_paste_text"));
        item.setImage("~./themes/ap/common/img/icons/paste.svg");
        item.addEventListener(ON_CLICK, event -> getBaseListboxController().paste());
        popup.appendChild(item);
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

    private void addMenuitem(Menupopup popup, MenuItem menuItem) {
        String itemId = menuItem.getId();
        if (PluginCatalog.PLUGIN_CUT.equals(itemId)) {
            addCutMenuItem(popup);
        } else if (PluginCatalog.PLUGIN_COPY.equals(itemId)) {
            addCopyMenuItem(popup);
        } else if (PluginCatalog.PLUGIN_PASTE.equals(itemId)) {
            addPasteMenuItem(popup);
        } else if (PluginCatalog.ITEM_SEPARATOR.equals(itemId)) {
            if (!(popup.getLastChild() instanceof Menuseparator)) {
                Menuseparator separator = new Menuseparator();
                popup.appendChild(separator);
            }
        } else {
            addPluginMenuitem(popup, menuItem);
        }
    }

    private void populateMenubar(Menubar menubar, MenuConfig menuConfig) {

        if (PortalPluginResolver.resolve().isEmpty()) {
            return;
        }
        // If there are portal plugins, create the menus for launching them
        portalPluginMap = PortalPluginResolver.getPortalPluginMap();
        menubar.getChildren().clear();
        List<MenuGroup> menuGroups = menuConfig.getGroups();

        for (MenuGroup menuGroup : menuGroups) {
            List<MenuItem> items = menuGroup.getItems();
            if (items.isEmpty()) {
                continue;
            }
            String groupLabel = getGroupLabel(menuGroup);
            Menu menu = new Menu(groupLabel);
            menu.setClientDataAttribute(GROUP, menuGroup.getId());
            if (menuGroup.getId().equals("ACCOUNT")) {
                menu.setTooltiptext(groupLabel);
            }
            Menupopup popup = new Menupopup();
            for (MenuItem menuItem : items) {
                addMenuitem(popup, menuItem);
            }
            if (!popup.getChildren().isEmpty()) {
                menu.appendChild(popup);
                menubar.appendChild(menu);
            }
        }
    }

    private BaseListboxController getBaseListboxController() {
        MainController mainController = (MainController) PortalContexts.getActivePortalContext().getMainController();
        return mainController.getBaseListboxController();
    }

}
