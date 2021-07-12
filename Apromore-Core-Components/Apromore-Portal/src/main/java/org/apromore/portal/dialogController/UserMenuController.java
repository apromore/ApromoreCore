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

import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.LabelConstants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.model.UserType;
import org.apromore.portal.util.ExplicitComparator;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import com.google.common.base.Strings;

public class UserMenuController extends SelectorComposer<Menubar> {

  private static final Logger LOGGER = PortalLoggerFactory.getLogger(UserMenuController.class);

  private static final String JWTS_MAP_BY_USER_KEY = TokenHandoffController.JWTS_MAP_BY_USER_KEY;

  private Menuitem aboutMenuitem;

  private ManagerService managerService;

  protected AutowireCapableBeanFactory beanFactory;
  protected ConfigBean config;

  public UserMenuController() {
    beanFactory = WebApplicationContextUtils
      .getWebApplicationContext(Sessions.getCurrent().getWebApp().getServletContext())
      .getAutowireCapableBeanFactory();
    config = (ConfigBean) beanFactory.getBean("portalConfig");
  }

  public ManagerService getManagerService() {
    if (this.managerService == null) {
      this.managerService = (ManagerService) SpringUtil.getBean(Constants.MANAGER_SERVICE);
    }

    return managerService;
  }

  public static final String getDisplayName(UserType userType) {
    String displayName = "";
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

  @Override
  public void doAfterCompose(Menubar menubar) {
    // If there are portal plugins, create the menus for launching them
    if (!PortalPluginResolver.resolve().isEmpty()) {

      // If present, this comparator expresses the preferred ordering for menus along the the menu bar
      Comparator<String> ordering = (ExplicitComparator) SpringUtil.getBean("portalMenuOrder");

      SortedMap<String, Menu> menuMap = new TreeMap<>(ordering);
      for (final PortalPlugin plugin: PortalPluginResolver.resolve()) {
        PortalPlugin.Availability availability = plugin.getAvailability();
        if (availability == PortalPlugin.Availability.UNAVAILABLE || availability == PortalPlugin.Availability.HIDDEN) {
          continue;
        }

        String group = plugin.getGroup(Locale.getDefault());
        String menuName = plugin.getGroupLabel(Locale.getDefault());
        String label = plugin.getLabel(Locale.getDefault());

        // Create a new menu if this is the first menu item within it
        if (!menuMap.containsKey(group)) {
          Menu menu = new Menu(menuName);
          menu.appendChild(new Menupopup());
          menuMap.put(group, menu);
        }
        assert menuMap.containsKey(group);

        // Create the menu item
        Menu menu = menuMap.get(group);
        Menuitem menuitem = new Menuitem();
        if (plugin.getIconPath().startsWith("/")) {
          menuitem.setImage(plugin.getIconPath());

        } else if (plugin.getResourceAsStream(plugin.getIconPath()) != null) {
          try {
            menuitem.setImage("portalPluginResource/"
              + URLEncoder.encode(plugin.getGroup(Locale.getDefault()), "utf-8") + "/"
              + URLEncoder.encode(plugin.getItemCode(Locale.getDefault()), "utf-8") + "/"
              + plugin.getIconPath());

          } catch (UnsupportedEncodingException e) {
            throw new Error("Hardcoded UTF-8 encoding failed", e);
          }
        } else {
          menuitem.setImageContent(plugin.getIcon());
        }
        menuitem.setLabel(label);
        menuitem.setDisabled(plugin.getAvailability() == PortalPlugin.Availability.DISABLED);
        menuitem.addEventListener("onClick", new EventListener<Event>() {
          @Override
          public void onEvent(Event event) throws Exception {
            PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
            plugin.execute(portalContext);
          }
        });

        if ("About".equals(menu.getLabel())) {
          aboutMenuitem = menuitem;
          continue;
        }

        // Insert the menu item into alphabetical position within the menu
        Menuitem precedingMenuitem = null;
        List<Menuitem> existingMenuitems = menu.getMenupopup().getChildren();
        for (Menuitem existingMenuitem: existingMenuitems) {
          int comparison = menuitem.getLabel().compareTo(existingMenuitem.getLabel());

          if (comparison <= 0) {
            precedingMenuitem = existingMenuitem;
            break;
          }
        }
        menu.getMenupopup().insertBefore(menuitem, precedingMenuitem);

      }

      for (final Menu menu: menuMap.values()) {
        if ("Account".equals(menu.getLabel())) {
          try {
            Menupopup userMenupopup = menu.getMenupopup();
            userMenupopup.insertBefore(aboutMenuitem, userMenupopup.getFirstChild());
            UserType userType = UserSessionManager.getCurrentUser();
            if (userType != null) {
              menu.setLabel(getDisplayName(userType));
            }
            menubar.appendChild(menu);
          } catch (Exception e) {
            LOGGER.warn("Unable to set Account menu to current user name", e);
          }
        }
      }

      // The signOutQueue receives events whose data is a ZK session which has signed out
      // If this desktop is part of a signed-out session, close the browser tab or switch to login
      EventQueues.lookup("signOutQueue", EventQueues.DESKTOP, true).subscribe(new EventListener() {
        public void onEvent(Event event) {
          Session session = Sessions.getCurrent();

          if (session != null) {
            if (!config.isUseKeycloakSso()) {
              session.invalidate();
            }
            Executions.sendRedirect("/j_spring_security_logout");
          }
        }
      });

      // Force logout a user that has been deleted by admin
      EventQueues.lookup("forceSignOutQueue", EventQueues.SESSION, true)
        .subscribe(new EventListener() {
          public void onEvent(Event event) {
            Session session = Sessions.getCurrent();

            UserType userType = (UserType) Sessions.getCurrent().getAttribute("USER");
            if (session == null || event.getData().equals(userType.getUsername())) {
              Executions.sendRedirect("/j_spring_security_logout");
            }
          }
        });
    }
  }
}