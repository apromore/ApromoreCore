/*
 * Copyright © 2019 The University of Melbourne.
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

package org.apromore.plugin.portal.processdiscoverer;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.processdiscoverer.VisualizationType;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.util.Clients;
import java.util.Locale;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
@Component("performancePlugin")
public class ProcessDiscovererPerformanceVisualizer extends ProcessDiscovererAbstractPlugin {

    private String label = "Mine process performance on process map / BPMN model";
    private String groupLabel = "Analyze";
    
     @Override
    public String getLabel(Locale locale) {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    @Override
    public void execute(PortalContext context) {
        try {
        	this.prepare(context, VisualizationType.DURATION); //prepare session
        	Clients.evalJavaScript("window.open('../processdiscoverer/zul/processDiscoverer.zul?id=" + this.getSessionId() + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
