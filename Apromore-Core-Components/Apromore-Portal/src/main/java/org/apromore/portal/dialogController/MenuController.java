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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PluginPortalContext;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.ExplicitComparator;
import org.slf4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;

public class MenuController extends SelectorComposer<Menubar> {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(MenuController.class);
    public static final String GROUP = "group";
    public static final String ON_CLICK = "onClick";

    private Menuitem aboutMenuitem;
    private Menuitem targetMenuitem;

    @Override
    public void doAfterCompose(Menubar menubar) {

        // Recreate the menubar when the authenticated user changes
        EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, EventQueues.SESSION, true).subscribe(new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (Constants.EVENT_QUEUE_SESSION_ATTRIBUTES.equals(event.getName())) {
                    if (UserSessionManager.USER.equals(event.getData())) {
                        getSelf().getChildren().clear();
                        populateMenubar(getSelf());
                    }
                }
            }
        });

        // Create the menubar initially
        populateMenubar(menubar);
    }

    private void populateMenubar(Menubar menubar) {

        // If there are portal plugins, create the menus for launching them
        if (!PortalPluginResolver.resolve().isEmpty()) {
            
            // If present, this comparator expresses the preferred ordering for menus along the the menu bar
            Comparator<String> ordering = (ExplicitComparator) SpringUtil.getBean("portalMenuOrder");
            Comparator<String> fileMenuitemOrdering = (ExplicitComparator) SpringUtil.getBean("portalFileMenuitemOrder");

            SortedMap<String, Menu> menuMap = new TreeMap<>(ordering);
            for (final PortalPlugin plugin: PortalPluginResolver.resolve()) {
                PortalPlugin.Availability availability = plugin.getAvailability();
                if (availability == PortalPlugin.Availability.UNAVAILABLE || availability == PortalPlugin.Availability.HIDDEN) {
                    continue;
                }

                String group = plugin.getGroup(Locale.getDefault());
                String menuName = plugin.getGroupLabel(Locale.getDefault());
                String itemCode = plugin.getItemCode(Locale.getDefault());
                if (group == "Settings") {
                    continue;
                }
                // Create a new menu if this is the first menu item within it
                if (!menuMap.containsKey(group)) {
                    Menu menu = new Menu(menuName);
                    menu.setClientDataAttribute(GROUP, group);
                    menu.appendChild(new Menupopup());
                    menuMap.put(group, menu);
                }
                assert menuMap.containsKey(group);

                // Create the menu item
                Menu menu = menuMap.get(group);
                Menuitem menuitem = new Menuitem();
                if (plugin.getResourceAsStream(plugin.getIconPath()) != null) {
                    try {
                        menuitem.setClientDataAttribute("itemCode", itemCode);
                        menuitem.setImage("portalPluginResource/"
                            + URLEncoder.encode(group, "utf-8") + "/"
                            + URLEncoder.encode(itemCode, "utf-8") + "/"
                            + plugin.getIconPath());

                    } catch (UnsupportedEncodingException e) {
                        throw new Error("Hardcoded UTF-8 encoding failed", e);
                    }
                } else {
                    menuitem.setImageContent(plugin.getIcon());
                }
                String label = plugin.getLabel(Locale.getDefault());
                menuitem.setLabel(label);
                menuitem.setDisabled(plugin.getAvailability() == PortalPlugin.Availability.DISABLED);
                menuitem.addEventListener(ON_CLICK, new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
                        plugin.execute(portalContext);
                    }
                });

                if ("About".equals(menu.getClientDataAttribute(GROUP))) {
                    aboutMenuitem = menuitem;
                    continue;
                }
                if ("Create folder".equals(menuitem.getClientDataAttribute("itemCode"))) {
                    targetMenuitem = menuitem;
                }

                // Insert the menu item into the appropriate position within the menu
                // (As configured in site.properties in the case of the File menu, alphabetically otherwise)
                Menuitem precedingMenuitem = null;
                List<Menuitem> existingMenuitems = menu.getMenupopup().getChildren();
                for (Menuitem existingMenuitem: existingMenuitems) {
                    int comparison = "File".equals(group) && (fileMenuitemOrdering != null)
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
                if (!"Account".equals(menu.getClientDataAttribute(GROUP)) &&
                    !"About".equals(menu.getClientDataAttribute(GROUP))) {
                    menubar.appendChild(menu);
                }
            }

            for (final Menu menu: menuMap.values()) {
                if ("Account".equals(menu.getClientDataAttribute(GROUP))) {
                    // ignore; belongs to the user menu

                } else if ("File".equals(menu.getClientDataAttribute(GROUP))) {
                    try {
                        Menupopup fileMenupopup = menu.getMenupopup();

                        Menuseparator sep = new Menuseparator();
                        fileMenupopup.insertBefore(sep, targetMenuitem);

                        Menuitem item = new Menuitem();
                        item.setLabel(Labels.getLabel("common_cut_text"));
                        item.setImage("/themes/ap/common/img/icons/cut.svg");
                        item.addEventListener(ON_CLICK, new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                getBaseListboxController().cut();
                            }
                        });
                        fileMenupopup.insertBefore(item, targetMenuitem);

                        item = new Menuitem();
                        item.setLabel(Labels.getLabel("common_copy_text"));
                        item.setImage("/themes/ap/common/img/icons/copy.svg");
                        item.addEventListener(ON_CLICK, new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                getBaseListboxController().copy();
                            }
                        });
                        fileMenupopup.insertBefore(item, targetMenuitem);

                        item = new Menuitem();
                        item.setLabel(Labels.getLabel("common_paste_text"));
                        item.setImage("/themes/ap/common/img/icons/paste.svg");
                        item.addEventListener(ON_CLICK, new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                getBaseListboxController().paste();
                            }
                        });
                        fileMenupopup.insertBefore(item, targetMenuitem);

                        sep = new Menuseparator();
                        fileMenupopup.insertBefore(sep, targetMenuitem);

                    } catch (Exception e) {
                        LOGGER.error("Ignored exception during main menu construction", e);
                    }
                }
            }
        }
    }

    private BaseListboxController getBaseListboxController() {
        PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
        MainController mainController = (MainController) portalContext.getMainController();

        return mainController.getBaseListboxController();
    }
}
