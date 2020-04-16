/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.PortalContext;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.util.Clients;

@Component("performancePlugin")
public class PDDurationPlugin extends PDAbstractPlugin {

    private String label = "Mine performance on map/model";
    private String groupLabel = "Analyze";
    
     @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
	public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    @Override
	public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    @Override
    public void execute(PortalContext context) {
        try {
        	boolean prepare = this.prepare(context, MeasureType.DURATION); //prepare session
            if (!prepare) return;
        	Clients.evalJavaScript("window.open('../processdiscoverer/zul/processDiscoverer.zul?id=" + this.getSessionId() + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
