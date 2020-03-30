/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apromore.model.*;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.common.TabListitem;
import org.apromore.portal.common.TabQuery;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.dialogController.workspaceOptions.AddFolderController;
import org.apromore.portal.exception.DialogException;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.ExplicitComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

public class MenuController extends Menubar {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

    private final MainController mainC;
    private Menubar menuB;
    private PortalContext portalContext;

    public MenuController(final MainController mainController) throws ExceptionFormats {
        this.mainC = mainController;
        this.portalContext = new PluginPortalContext(mainC);
        this.menuB = (Menubar) this.mainC.getFellow("menucomp").getFellow("operationMenu");

        // If there are portal plugins, create the menus for launching them
        if (!PortalPluginResolver.resolve().isEmpty()) {
            
            // If present, this comparator expresses the preferred ordering for menus along the the menu bar
            Comparator<String> ordering = (ExplicitComparator) SpringUtil.getBean("portalMenuOrder");
            Comparator<String> fileMenuitemOrdering = (ExplicitComparator) SpringUtil.getBean("portalFileMenuitemOrder");

            SortedMap<String, Menu> menuMap = new TreeMap<>(ordering);
            for (final PortalPlugin plugin: PortalPluginResolver.resolve()) {
                if (plugin.getAvailability(portalContext) == PortalPlugin.Availability.UNAVAILABLE) {
                    continue;
                }

                String menuName = plugin.getGroupLabel(Locale.getDefault());

                // Create a new menu if this is the first menu item within it
                if (!menuMap.containsKey(menuName)) {
                    Menu menu = new Menu(menuName);
                    menu.appendChild(new Menupopup());
                    menuMap.put(menuName, menu);
                }
                assert menuMap.containsKey(menuName);

                // Create the menu item
                Menu menu = menuMap.get(menuName);
                Menuitem menuitem = new Menuitem();
                if (plugin.getResourceAsStream(plugin.getIconPath()) != null) {
                    try {
                        menuitem.setImage("portalPluginResource/"
                            + URLEncoder.encode(plugin.getGroupLabel(Locale.getDefault()), "utf-8") + "/"
                            + URLEncoder.encode(plugin.getLabel(Locale.getDefault()), "utf-8") + "/"
                            + plugin.getIconPath());

                    } catch (UnsupportedEncodingException e) {
                        throw new Error("Hardcoded UTF-8 encoding failed", e);
                    }
                } else {
                    menuitem.setImageContent(plugin.getIcon());
                }
                menuitem.setLabel(plugin.getLabel(Locale.getDefault()));
                menuitem.setDisabled(plugin.getAvailability(portalContext) == PortalPlugin.Availability.DISABLED);

                // Insert the menu item into the appropriate position within the menu
                // (As configured in site.properties in the case of the File menu, alphabetically otherwise)
                Menuitem precedingMenuitem = null;
                List<Menuitem> existingMenuitems = menu.getMenupopup().getChildren();
                for (Menuitem existingMenuitem: existingMenuitems) {
                    int comparison = "File".equals(menuName) && (fileMenuitemOrdering != null)
                        ? fileMenuitemOrdering.compare(menuitem.getLabel(), existingMenuitem.getLabel())
                        : menuitem.getLabel().compareTo(existingMenuitem.getLabel());

                    if (comparison <= 0) {
                        precedingMenuitem = existingMenuitem;
                        break;
                    }
                }
                menu.getMenupopup().insertBefore(menuitem, precedingMenuitem);

                menuitem.addEventListener("onClick", new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        plugin.execute(new PluginPortalContext(mainC));
                    }
                });
            }

            // Add the menus to the menu bar
            for (final Menu menu: menuMap.values()) {
                if (!"Account".equals(menu.getLabel())) {
                    menuB.appendChild(menu);
                }
            }

            Menuseparator separator = new Menuseparator();
            separator.setHflex("1");
            separator.setStyle("border-width: 0");
            menuB.appendChild(separator);

            for (final Menu menu: menuMap.values()) {
                if ("Account".equals(menu.getLabel())) {
                    try {
                        menu.setLabel(UserSessionManager.getCurrentUser().getUsername());
                        menu.setSclass("ap-user-menu");
                    } catch (Exception e) {
                        LOGGER.warn("Unable to set Account menu to current user name", e);
                    }

                    menuB.appendChild(menu);
                }
            }
        }
    }

    public Menubar getMenuB() {
        return menuB;
    }
}
