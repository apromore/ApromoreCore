/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.plugin.portal.processdiscoverer.plugins;

import java.util.Locale;

import javax.inject.Inject;

import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.logfilter.generic.LogFilterPlugin;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.loganimation.LogAnimationService2;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

@Component("frequencyPlugin")
public class PDFrequencyPlugin extends PDAbstractPlugin {

    private String label = "Discover model";
    private String groupLabel = "Discover";

    @Inject DomainService domainService;
    @Inject EventLogService eventLogService;
    @Inject ProcessService processService;
    @Inject LogFilterPlugin logFilterPlugin;
    @Inject LogAnimationService2 logAnimationService;

    @Override
    public String getItemCode(Locale locale) { return "Discover model"; }

    @Override
    public String getLabel(Locale locale) {
        return Labels.getLabel("plugin_discover_discoverModel_text",label);
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return Labels.getLabel("plugin_discover_title_text", groupLabel);
    }

    @Override
	public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroup(Locale locale) {
        return "Discover";
    }

    @Override
	public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    @Override
    public void execute(PortalContext context) {
        try {
        	boolean prepare = this.prepare(context, MeasureType.FREQUENCY); //prepare session
        	Desktop d = Executions.getCurrent().getDesktop();
        	Session s = Executions.getCurrent().getSession();

                Sessions.getCurrent().setAttribute("domainService", domainService);
                Sessions.getCurrent().setAttribute("eventLogService", eventLogService);
                Sessions.getCurrent().setAttribute("processService", processService);
                Sessions.getCurrent().setAttribute("logFilterPlugin", logFilterPlugin);
                Sessions.getCurrent().setAttribute("logAnimationService", logAnimationService);

        	if (!prepare) return;
        	Clients.evalJavaScript("window.open('../processdiscoverer/zul/processDiscoverer.zul?id=" + this.getSessionId() + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
