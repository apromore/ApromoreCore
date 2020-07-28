/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.ExplicitComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

public class MenuController extends Menubar {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuController.class);

    private final MainController mainC;
    private Menubar menuB;
    private Menubar userMenu;
    private Menuitem aboutMenuitem;
    private Menuitem targetMenuitem;
    private PortalContext portalContext;

    public MenuController(final MainController mainController) throws ExceptionFormats {
        MenuController me = this;
        this.mainC = mainController;
        this.portalContext = new PluginPortalContext(mainC);
        this.menuB = (Menubar) this.mainC.getFellow("menucomp").getFellow("operationMenu");
        this.userMenu = (Menubar) this.mainC.getFellow("userMenu");
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
                String label = plugin.getLabel(Locale.getDefault());
                menuitem.setLabel(label);
                menuitem.setDisabled(plugin.getAvailability(portalContext) == PortalPlugin.Availability.DISABLED);
                menuitem.addEventListener("onClick", new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        plugin.execute(new PluginPortalContext(mainC));
                    }
                });

                if ("About".equals(menu.getLabel())) {
                    aboutMenuitem = menuitem;
                    continue;
                }
                if ("Create folder".equals(menuitem.getLabel())) {
                    targetMenuitem = menuitem;
                }

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

            }

            // Add the menus to the menu bar
            for (final Menu menu: menuMap.values()) {
                if (!"Account".equals(menu.getLabel()) &&
                    !"About".equals(menu.getLabel())) {
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
                        // menu.setLabel(UserSessionManager.getCurrentUser().getUsername());
                        // menu.setSclass("ap-user-menu");
                        // menuB.appendChild(menu);
                        Menupopup userMenupopup = menu.getMenupopup();
                        userMenupopup.insertBefore(aboutMenuitem, userMenupopup.getFirstChild());
                        this.userMenu.appendChild(menu);
                    } catch (Exception e) {
                        LOGGER.warn("Unable to set Account menu to current user name", e);
                    }
                } else if ("File".equals(menu.getLabel())) {
                    try {
                        Menupopup fileMenupopup = menu.getMenupopup();

                        Menuseparator sep = new Menuseparator();
                        fileMenupopup.insertBefore(sep, targetMenuitem);

                        Menuitem item = new Menuitem();
                        item.setLabel("Cut");
                        item.setImage("/themes/ap/common/img/icons/cut.svg");
                        item.addEventListener("onClick", new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                me.mainC.geBaseListboxController().cut();
                            }
                        });
                        fileMenupopup.insertBefore(item, targetMenuitem);

                        item = new Menuitem();
                        item.setLabel("Copy");
                        item.setImage("/themes/ap/common/img/icons/copy.svg");
                        item.addEventListener("onClick", new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                me.mainC.geBaseListboxController().copy();
                            }
                        });
                        fileMenupopup.insertBefore(item, targetMenuitem);

                        item = new Menuitem();
                        item.setLabel("Paste");
                        item.setImage("/themes/ap/common/img/icons/paste.svg");
                        item.addEventListener("onClick", new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                me.mainC.geBaseListboxController().paste();
                            }
                        });
                        fileMenupopup.insertBefore(item, targetMenuitem);

                        sep = new Menuseparator();
                        fileMenupopup.insertBefore(sep, targetMenuitem);

                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public Menubar getMenuB() {
        return menuB;
    }
}
