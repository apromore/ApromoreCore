/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.plugin.portal.processdiscoverer.data;

import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;

public class ConfigData {
    public final String DEFAULT_SELECTOR = "concept:name";
    private final int NUMBER_OF_UNIQUE_VALUES_ADJUST = 100;
    private final int NUMBER_OF_UNIQUE_VALUES_FORCE_ADJUST = 200;
    private final int NUMBER_OF_UNIQUE_VALUES_MAX_SELECT = 300;
    private final int MAX_NUMBER_OF_NODES = 80;
    private final int MAX_NUMBER_OF_ARCS = 150;
    
    public static MeasureType DEFAULT_MEASURE_TYPE = MeasureType.FREQUENCY;
    public static MeasureAggregation DEFAULT_MEASURE_AGGREGATE = MeasureAggregation.CASES;
    
    private HashBiMap<String,String> displayToOriginKeyMapping = new HashBiMap<>();
    
    public ConfigData() {
        displayToOriginKeyMapping.put("concept:name", "Activity");
        displayToOriginKeyMapping.put("lifecycle:transition", "State");
        displayToOriginKeyMapping.put("org:group", "Resource group");
        displayToOriginKeyMapping.put("org:resource", "Resource");
        displayToOriginKeyMapping.put("org:role", "Role");
    }
    
    public String getDefaultAttribute() {
        return DEFAULT_SELECTOR;
    }
    
    public int getAttributeUniqueValuesToAdjust() {
        return NUMBER_OF_UNIQUE_VALUES_ADJUST;
    }
    
    public int getAttributeUniqueValuesToForceAdjust() {
        return NUMBER_OF_UNIQUE_VALUES_FORCE_ADJUST;
    }
    
    public int getMaxNumberOfUniqueValues() {
        return NUMBER_OF_UNIQUE_VALUES_MAX_SELECT;
    }
    
    public int getMaxNumberOfNodes() {
        return MAX_NUMBER_OF_NODES;
    }
    
    public int getMaxNumberOfArcs() {
        return MAX_NUMBER_OF_ARCS;
    }
    
    public String getDisplayAttributeName(String attributeName) {
        String displayName = displayToOriginKeyMapping.get(attributeName);
        return (displayName == null ? attributeName : displayName);
    }
    
    public String getAttributeName(String displayName) {
        String attributeName = displayToOriginKeyMapping.inverse().get(displayName);
        return (attributeName == null ? displayName : attributeName);
    }    
}
