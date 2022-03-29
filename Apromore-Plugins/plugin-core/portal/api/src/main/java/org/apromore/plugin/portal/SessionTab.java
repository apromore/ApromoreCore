/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal;

import org.slf4j.Logger;
import org.zkoss.zul.Tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by corno on 17/07/2014.
 */
public class SessionTab {

    private static SessionTab sessionTab;  // singleton instance
    private static Logger LOGGER = PortalLoggerFactory.getLogger(SessionTab.class);
    private HashMap<String, LinkedList<Tab>> mapTabs;  // value is the set of tabs for a user's session, keyed on their user ID
    private PortalContext portalContext;

    private SessionTab(PortalContext portalContext) {
        assert portalContext != null;
        mapTabs = new HashMap<>();
        this.portalContext = portalContext;
    }

    /** @param portalContext must not be <code>null</code> */
    public static SessionTab getSessionTab(PortalContext portalContext) {
        if(sessionTab == null) {
            sessionTab = new SessionTab(portalContext);
        }
        return sessionTab;
    }

    private List<Tab> getTabs(String id) {
        LinkedList<Tab> tabs;
        if((tabs = mapTabs.get(id)) == null) {
            tabs = new LinkedList<>();
            mapTabs.put(id, tabs);
        }
        return tabs;
    }

    /** @param id  a user ID
     *  @return the tabs associated with that user's session */
    public List<Tab> getTabsSession(String id) {
        return new ArrayList<>(getTabs(id));
    }

    /** @param refresh  whether to refresh the tab bar in the UI */
    public void addTabToSession(String id, Tab tab, boolean refresh) {
        LOGGER.debug("Adding " + tab + " id=" + id + " to " + getTabs(id) + " refresh=" + refresh);
        getTabs(id).add(tab);
        if (refresh) {
            portalContext.refreshContent();
        }
        LOGGER.debug("Added " + tab + " id=" + id + " to " + getTabs(id));
    }

    /** @param refresh  whether to refresh the tab bar in the UI */
    public void removeTabFromSession(String id, Tab tab, boolean refresh) {
        LOGGER.debug("Removing " + tab + " id=" + id + " from " + getTabs(id) + " refresh=" + refresh);
        getTabs(id).remove(tab);
        if (refresh) {
            portalContext.refreshContent();
        }
        LOGGER.debug("Removed " + tab + " id=" + id + " from " + getTabs(id));
    }
}
