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
//            "time:duration",
            "time:timestamp",
			"duration:range",
			"time:startrange",
			"time:endrange",
			"case:id"
    };
    
    // Must be sorted for Arrays.binarySearch
//    private static String[] name = new String[] {
//            "Activity",
//			"Case variant",
//            "Direct Follow Relation",
//            "Duration",
//            "Eventually Follow Relation",
//            "Group",
//            "Lifecycle",
//            "Resource",
//            "Role",
//            "Time-frame"
//    };
    
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
//		codeNameMap.put("time:duration", "Duration");
		codeNameMap.put("time:timestamp", "Timeframe");
		codeNameMap.put("duration:range", "Duration range");
		codeNameMap.put("time:startrange", "Start time range");
		codeNameMap.put("time:endrange", "End time range");
		codeNameMap.put("case:id", "Case ID");
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
//    	typeMap.put("time:duration", Type.TIME_DURATION);
    	typeMap.put("time:timestamp", Type.TIME_TIMESTAMP);
		typeMap.put("duration:range", Type.DURATION_RANGE);
		typeMap.put("time:startrange", Type.TIME_STARTRANGE);
		typeMap.put("time:endrange", Type.TIME_ENDRANGE);
		typeMap.put("case:id", Type.CASE_ID);
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
//													            "time:duration",
													            "time:timestamp",
																"duration:range",
																"case:id"));

	public static List<String> getStandardCodes() {
		List<String> codeList = Arrays.asList(codes);
		return Collections.unmodifiableList(codeList);
	}
    
    
//    public static List<String> getStandardNames() {
//    	List<String> names = Arrays.asList(name);
//    	return Collections.unmodifiableList(names);
//    }
    

    public static Type getType(String typeName) {
    	if (!typeMap.containsKey(typeName)) {
    		return Type.UNKNOWN;
    	}
    	else {
    		return typeMap.get(typeName);
    	}
    }

    //index of the name in the list
//    public static int getName(String attribute) {
//        int t = Arrays.binarySearch(name, attribute);
//        if(t < 0) return -1;
//        return t;
//    }
    
//    public static boolean isStandardType(String type) {
//    	return typeNameMap.containsKey(type);
//    }
    
//    public static boolean isStandardName(String name) {
//    	return typeNameMap.inverse().containsKey(name);
//    }

    // search the corresponding name of a given type
    public static String getNameFromCode(String code) {
//    	return search1(attribute, type, name);
        return codeNameMap.get(code);
    }

    // search the corresponding type of a given name
//    public static String getTypeFromName(String name) {
////        return search2(attribute, name, type);
//    	return codeNameMap.inverse().get(name);
//    }
    
    public static boolean isValidCode(String type, Level level) {
    	if (level == Level.EVENT) {
    		return !codeNameMap.containsKey(type) || eventStandardCodes.contains(type);
    	}
    	else {
    		return !codeNameMap.containsKey(type) || traceStandardCodes.contains(type);
    	}
    }
    
//    // The index is not 1-1 between the two arrays as the array values must be ordered
//    private static String search1(String attribute, String[] origin, String[] translation) {
//        int t = Arrays.binarySearch(origin, attribute);
//        switch (t) {
//            case 0 : return translation[0];
//            case 1 : return translation[1];
//            case 2 : return translation[3];
//            case 3 : return translation[4];
//            case 4 : return translation[5];
//            case 5 : return translation[6];
//            case 6 : return translation[7];
//            case 7 : return translation[2];
//            case 8 : return translation[8];
//            default : return null;
//        }
//    }
//
//    // The index is not 1-1 between the two arrays as the array values must be ordered
//    private static String search2(String attribute, String[] origin, String[] translation) {
//        int t = Arrays.binarySearch(origin, attribute);
//        switch (t) {
//            case 0 : return translation[0];
//            case 1 : return translation[1];
//            case 3 : return translation[2];
//            case 4 : return translation[3];
//            case 5 : return translation[4];
//            case 6 : return translation[5];
//            case 7 : return translation[6];
//            case 2 : return translation[7];
//            case 8 : return translation[8];
//            default : return null;
//        }
//    }

}
