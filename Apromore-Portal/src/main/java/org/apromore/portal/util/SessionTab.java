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

package org.apromore.portal.util;

import org.apromore.portal.common.TabQuery;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

import java.util.*;

/**
 * Created by corno on 17/07/2014.
 */
public class SessionTab {

    private static SessionTab sessionTab = new SessionTab();
    private HashMap<String, LinkedList<Tab>> mapTabs = new HashMap<>();

    private SessionTab() {
    }

    public static SessionTab getSessionTab() {
        if(sessionTab == null) {
            sessionTab = new SessionTab();
        }
        return sessionTab;
    }

    public static void setTabsSession(String id, LinkedList<Tab> tabs) {
            SessionTab.getSessionTab().mapTabs.put(id, tabs);
    }

    public static LinkedList<Tab> getTabsSession(String id) {
//        if(SessionTab.getSessionTab().mapTabs.get(id)!=null){
//            LinkedList<Tab> tabs=new LinkedList<>(SessionTab.getSessionTab().mapTabs.get(id));
//
//        }
        return SessionTab.getSessionTab().mapTabs.get(id);
    }

}
