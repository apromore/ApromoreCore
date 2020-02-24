/*
 * Copyright © 2019-2020 The University of Melbourne.
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

package org.apromore.logfilter.criteria.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.impl.bimap.mutable.HashBiMap;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified: Bruce Nguyen
 * Modified: Chii Chang (10/10/2019)
 * A filter has codes, names (display names) and types.
 * These are standard filters (i.e. standard codes)
 * The codes are the original attribute name in event logs
 * The names are displayed on UI instead of the codes which are used internally
 * Non-standard filters will have names displayed in quotes on the UI, e.g. "A", "B"
 * Multiple filters with different codes can belong to the same type
 * Non-standard filters have UNKNOWN type.
 */
public class LogFilterTypeSelector {
	// Must be sorted for Arrays.binarySearch
    private static String[] codes = new String[] {
            "concept:name",
			"case:variant",
            "direct:follow",
            "eventually:follow",
            "lifecycle:transition",
            "org:group",
            "org:resource",
            "org:role",
            "time:timestamp",
			"duration:range",
			"time:startrange",
			"time:endrange",
			"case:id",
			"duration:total_processing",
			"duration:average_processing",
			"duration:max_processing",
			"duration:total_waiting",
			"duration:average_waiting",
			"duration:max_waiting",
			"case:utilization"
    };

    
    private static HashBiMap<String,String> codeNameMap = new HashBiMap<>();
    static {
		codeNameMap.put("concept:name", "Activity");
		codeNameMap.put("case:variant", "Case variant");
		codeNameMap.put("direct:follow", "Directly-follows relation");
		codeNameMap.put("eventually:follow", "Eventually-follows relation");
		codeNameMap.put("lifecycle:transition", "State");
		codeNameMap.put("org:group", "Resource group");
		codeNameMap.put("org:resource", "Resource");
		codeNameMap.put("org:role", "Role");
		codeNameMap.put("time:timestamp", "Timeframe");
		codeNameMap.put("duration:range", "Duration range");
		codeNameMap.put("time:startrange", "Start time range");
		codeNameMap.put("time:endrange", "End time range");
		codeNameMap.put("case:id", "Case ID");
		codeNameMap.put("duration:total_processing", "Total processing time");
		codeNameMap.put("duration:average_processing", "Average processing time");
		codeNameMap.put("duration:max_processing", "Max processing time");
		codeNameMap.put("duration:total_waiting", "Total waiting time");
		codeNameMap.put("duration:average_waiting", "Average waiting time");
		codeNameMap.put("duration:max_waiting", "Max waiting time");
		codeNameMap.put("case:Utilization", "Case utilization");
    }
       
    private static Map<String,Type> typeMap = new HashMap<>();
    static {
    	typeMap.put("concept:name", Type.CONCEPT_NAME);
		typeMap.put("case:variant", Type.CASE_VARIANT);
    	typeMap.put("direct:follow", Type.DIRECT_FOLLOW);
    	typeMap.put("eventually:follow", Type.EVENTUAL_FOLLOW);
    	typeMap.put("lifecycle:transition", Type.LIFECYCLE_TRANSITION);
    	typeMap.put("org:group", Type.ORG_GROUP);
    	typeMap.put("org:resource", Type.ORG_RESOURCE);
    	typeMap.put("org:role", Type.ORG_ROLE);
    	typeMap.put("time:timestamp", Type.TIME_TIMESTAMP);
		typeMap.put("duration:range", Type.DURATION_RANGE);
		typeMap.put("time:startrange", Type.TIME_STARTRANGE);
		typeMap.put("time:endrange", Type.TIME_ENDRANGE);
		typeMap.put("case:id", Type.CASE_ID);
		typeMap.put("duration:total_processing", Type.DURATION_TOTAL_PROCESSING);
		typeMap.put("duration:average_processing", Type.DURATION_AVERAGE_PROCESSING);
		typeMap.put("duration:max_processing", Type.DURATION_MAX_PROCESSING);
		typeMap.put("duration:total_waiting", Type.DURATION_TOTAL_WAITING);
		typeMap.put("duration:average_waiting", Type.DURATION_AVERAGE_WAITING);
		typeMap.put("duration:max_waiting", Type.DURATION_MAX_WAITING);
		typeMap.put("case:utilization", Type.CASE_UTILIZATION);
    }
    
    private static Set<String> eventStandardCodes = new HashSet<>(Arrays.asList(
    															"concept:name", 
    															"lifecycle:transition",
    															"org:group",
    															"org:resource",
    															"org:role",
    															"time:timestamp"));
    
    private static Set<String> traceStandardCodes = new HashSet<>(Arrays.asList(
			"concept:name",
			"case:variant",
			"direct:follow",
			"eventually:follow",
			"lifecycle:transition",
			"org:group",
			"org:resource",
			"org:role",
			"time:timestamp",
			"duration:range",
			"case:id",
			"duration:total_processing",
			"duration:average_processing",
			"duration:max_processing",
			"duration:total_waiting",
			"duration:average_waiting",
			"duration:max_waiting",
			"case:utilization"));

	public static List<String> getStandardCodes() {
		List<String> codeList = Arrays.asList(codes);
		return Collections.unmodifiableList(codeList);
	}

    public static Type getType(String typeName) {
    	if (!typeMap.containsKey(typeName)) {
    		return Type.UNKNOWN;
    	}
    	else {
    		return typeMap.get(typeName);
    	}
    }


    // search the corresponding name of a given type
    public static String getNameFromCode(String code) {
        return codeNameMap.get(code);
    }
    
    public static boolean isValidCode(String type, Level level) {
    	if (level == Level.EVENT) {
    		return !codeNameMap.containsKey(type) || eventStandardCodes.contains(type);
    	}
    	else {
    		return !codeNameMap.containsKey(type) || traceStandardCodes.contains(type);
    	}
    }


}
