/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal;

import org.zkoss.zul.Tab;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by corno on 17/07/2014.
 */
public class SessionTab {

    private static SessionTab sessionTab;
    private HashMap<String, LinkedList<Tab>> mapTabs;
    private PortalContext portalContext;

    private SessionTab(PortalContext portalContext) {
        mapTabs = new HashMap<>();
        this.portalContext = portalContext;
    }

    public static SessionTab getSessionTab(PortalContext portalContext) {
        if(sessionTab == null) {
            sessionTab = new SessionTab(portalContext);
        }
        return sessionTab;
    }

    public void setTabsSession(String id, LinkedList<Tab> tabs) {
        mapTabs.put(id, tabs);
    }

    public List<Tab> getTabsSession(String id) {
        LinkedList<Tab> tabs;
        if((tabs = mapTabs.get(id)) == null) {
            tabs = new LinkedList<>();
            mapTabs.put(id, tabs);
        }
        return tabs;
    }

    public void addTabToSession(String id, Tab tab) {
//        LinkedList<Tab> tabs;
//        if((tabs = mapTabs.get(id)) == null) {
//            tabs = new LinkedList<>();
//            mapTabs.put(id, tabs);
//        }
        getTabsSession(id).add(tab);
        portalContext.refreshContent();
    }

    public void removeTabFromSession(String id, Tab tab) {
        getTabsSession(id).remove(tab);
        portalContext.refreshContent();
    }

}
