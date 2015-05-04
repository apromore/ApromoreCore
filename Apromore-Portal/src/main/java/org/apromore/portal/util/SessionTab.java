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
