/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.plugin.portal.apql;

// Java 2 Standard Edition packages
import java.util.Locale;

// Third party packages
import org.apromore.model.UserType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SizeEvent;
import org.zkoss.zul.Applet;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.model.UserType;

/**
 * A user interface to launch the APQL applet.
 */
public class APQLPlugin extends DefaultPortalPlugin {

    private Applet applet;
    private String managerEndpoint, portalEndpoint, filestoreURL, pqlLogicEndpoint;

    public APQLPlugin(String siteExternalHost, int siteExternalPort, String siteFilestore, String siteManager, String sitePortal, String sitePQL) {

        String siteBase = "http://" + siteExternalHost + ":" + siteExternalPort + "/";

        managerEndpoint  = siteBase + siteManager + "/services";
        portalEndpoint   = siteBase + sitePortal + "/services";
        filestoreURL     = siteBase + siteFilestore;
        pqlLogicEndpoint = siteBase + sitePQL + "/services";

        LoggerFactory.getLogger(getClass()).info("Created APQL portal plugin: manager at " + managerEndpoint + ", portal at " + portalEndpoint + ", filestore at " + filestoreURL + ", logic at " + pqlLogicEndpoint);
    }

    @Override
    public String getLabel(Locale locale) {
        return "Query with PQL";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Analyze";
    }

    @Override
    public void execute(final PortalContext portalContext) {
        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/pqlFilter.zul", null, null);

            UserType user = portalContext.getCurrentUser();

            applet = (Applet) window.getFirstChild().getFirstChild();
            applet.setParam("manager_endpoint", managerEndpoint);
            applet.setParam("portal_endpoint", portalEndpoint);
            applet.setParam("filestore_url", filestoreURL);
            applet.setParam("pql_logic_endpoint", pqlLogicEndpoint);
            applet.setParam("user",user.getUsername());
            applet.setParam("idSession",user.getId());

            window.addEventListener(Events.ON_CLOSE,new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    portalContext.refreshContent();
                    applet=null;
                }
            });

            window.addEventListener(Events.ON_SIZE, new EventListener<SizeEvent>() {
                @Override
                public void onEvent(SizeEvent sizeEvent) throws Exception {
                    applet.setWidth(sizeEvent.getWidth());
                    applet.setHeight(sizeEvent.getHeight());
                }
            }); 

            window.doModal();

        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Failed to launch APQL applet", e);
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
