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

package org.apromore.plugin.portal.processdiscoverer.data;

import org.apromore.logman.Constants;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:pd.application.properties")
public class ConfigData {
    private final String DEFAULT_SELECTOR;

    private final int MAX_NUMBER_OF_NODES;

    private final int MAX_NUMBER_OF_ARCS;

    private final HashBiMap<String,String> displayToOriginKeyMapping = new HashBiMap<>();
    {
        displayToOriginKeyMapping.put("concept:name", "Activity");
        displayToOriginKeyMapping.put("lifecycle:transition", "State");
        displayToOriginKeyMapping.put("org:group", "Resource group");
        displayToOriginKeyMapping.put("org:resource", "Resource");
        displayToOriginKeyMapping.put("org:role", "Role");
    }
    
    public static MeasureType DEFAULT_MEASURE_TYPE = MeasureType.FREQUENCY;
    public static MeasureAggregation DEFAULT_MEASURE_AGGREGATE = MeasureAggregation.CASES;

    public static final ConfigData DEFAULT = new ConfigData("concept:name", 500, 500);

    public ConfigData(@Value(Constants.ATT_KEY_CONCEPT_NAME) String attributeName,
                      @Value("${pd.maxNodes}") int maxNumberOfNodes,
                      @Value("${pd.maxArcs}") int maxNumberOfArcs) {
        DEFAULT_SELECTOR = attributeName;
        MAX_NUMBER_OF_NODES = maxNumberOfNodes;
        MAX_NUMBER_OF_ARCS = maxNumberOfArcs;
    }
    
    public String getDefaultAttribute() {
        return DEFAULT_SELECTOR;
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
