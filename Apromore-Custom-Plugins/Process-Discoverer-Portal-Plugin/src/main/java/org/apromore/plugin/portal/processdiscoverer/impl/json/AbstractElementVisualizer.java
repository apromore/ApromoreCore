/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.plugin.portal.processdiscoverer.impl.json;

import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.plugin.portal.processdiscoverer.vis.InvalidOutputException;
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
	
    protected String getWeightString(double weightValue, String separator, MeasureType measureType, 
                                        MeasureRelation measureRelation) {
        if (measureRelation == MeasureRelation.ABSOLUTE) {
            if (measureType == MeasureType.FREQUENCY) {
                return separator + visSettings.getDecimalFormatter().format(weightValue);
            }
            else if (measureType == MeasureType.DURATION) {
                return separator + visSettings.getTimeConverter().convertMilliseconds("" + weightValue);
            }
            else if (measureType == MeasureType.COST) {
                return separator + visSettings.getCurrency() + " " + visSettings.getDecimalFormatter().format(weightValue);
            }
            else {
                return "";
            }
        }
        else {
            return separator + visSettings.getDecimalFormatter().format(weightValue*100) + "%";
        }
    }
	
	
	@Override
    public abstract JSONObject generateJSON(ContainableDirectedGraphElement element) 
	            throws UnsupportedElementException, JSONException, InvalidOutputException;

}
