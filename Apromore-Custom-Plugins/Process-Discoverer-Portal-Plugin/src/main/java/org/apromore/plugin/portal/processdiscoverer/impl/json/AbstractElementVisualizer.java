/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.processdiscoverer.impl.json;

import org.apromore.plugin.portal.processdiscoverer.vis.MissingLayoutException;
import org.apromore.plugin.portal.processdiscoverer.vis.UnsupportedElementException;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualContext;
import org.apromore.plugin.portal.processdiscoverer.vis.VisualSettings;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class AbstractElementVisualizer implements ElementVisualizer {
	protected VisualContext visContext;
	protected VisualSettings visSettings;
	
	public AbstractElementVisualizer(VisualContext visContext, VisualSettings visSettings) {
	    this.visContext = visContext;
	    this.visSettings = visSettings;
    }
	
	public void setVisualContext(VisualContext visContext) {
	    this.visContext = visContext;
	}
	
	public void setVisualSettings(VisualSettings visSettings) {
	    this.visSettings = visSettings;
	}
	
	@Override
    public abstract JSONObject generateJSON(ContainableDirectedGraphElement element) 
	            throws UnsupportedElementException, JSONException, MissingLayoutException;

}
