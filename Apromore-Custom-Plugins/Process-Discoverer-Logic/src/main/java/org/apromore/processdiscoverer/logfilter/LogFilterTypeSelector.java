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

package org.apromore.processdiscoverer.logfilter;

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
 * Modified: Bruce Nguyen: add checkLevelValidity()
 * Note that this class only contains standard filter types
 * There are non-standard types which are other attributes available in logs
 */
public class LogFilterTypeSelector {

	// Must be sorted for Arrays.binarySearch
    private static String[] type = new String[] {
            "concept:name",
            "direct:follow",
            "eventually:follow",
            "lifecycle:transition",
            "org:group",
            "org:resource",
            "org:role",
            "time:duration",
            "time:timestamp"
    };
    
    // Must be sorted for Arrays.binarySearch
    private static String[] name = new String[] {
            "Activity",
            "Direct Follow Relation",
            "Duration",
            "Eventually Follow Relation",
            "Group",
            "Lifecycle",
            "Resource",
            "Role",
            "Time-frame"
    };
    
    private static HashBiMap<String,String> typeNameMap = new HashBiMap<>();
    static {
    	typeNameMap.put("concept:name", "Activity");
    	typeNameMap.put("direct:follow", "Direct Follow Relation");
    	typeNameMap.put("eventually:follow", "Eventually Follow Relation");
    	typeNameMap.put("lifecycle:transition", "Lifecycle");
    	typeNameMap.put("org:group", "Group");
    	typeNameMap.put("org:resource", "Resource");
    	typeNameMap.put("org:role", "Role");
    	typeNameMap.put("time:duration", "Duration");
    	typeNameMap.put("time:timestamp", "Time-frame");
    }
       
    private static Map<String,Type> typeMap = new HashMap<>();
    static {
    	typeMap.put("concept:name", Type.CONCEPT_NAME);
    	typeMap.put("direct:follow", Type.DIRECT_FOLLOW);
    	typeMap.put("eventually:follow", Type.EVENTUAL_FOLLOW);
    	typeMap.put("lifecycle:transition", Type.LIFECYCLE_TRANSITION);
    	typeMap.put("org:group", Type.ORG_GROUP);
    	typeMap.put("org:resource", Type.ORG_RESOURCE);
    	typeMap.put("org:role", Type.ORG_ROLE);
    	typeMap.put("time:duration", Type.TIME_DURATION);
    	typeMap.put("time:timestamp", Type.TIME_TIMESTAMP);
    }
    
    private static Set<String> eventStandardTypes = new HashSet<>(Arrays.asList(
    															"concept:name", 
    															"lifecycle:transition",
    															"org:group",
    															"org:resource",
    															"org:role",
    															"time:timestamp"));
    
    private static Set<String> traceStandardTypes = new HashSet<>(Arrays.asList(
													    		"concept:name",
													            "direct:follow",
													            "eventually:follow",
													            "lifecycle:transition",
													            "org:group",
													            "org:resource",
													            "org:role",
													            "time:duration",
													            "time:timestamp"));
    
    public static List<String> getStandardTypes() {
    	List<String> types = Arrays.asList(type);
    	return Collections.unmodifiableList(types);
    }
    
    
    public static List<String> getStandardNames() {
    	List<String> names = Arrays.asList(name);
    	return Collections.unmodifiableList(names);
    }
    

    public static Type getType(String typeName) {
//        int t = Arrays.binarySearch(type, attribute);
//        if(t < 0) {
//        	return Type.UNKNOWN;
//        }
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
    
    public static boolean isStandardType(String type) {
    	return typeNameMap.containsKey(type);
    }
    
    public static boolean isStandardName(String name) {
    	return typeNameMap.inverse().containsKey(name);
    }

    // search the corresponding name of a given type
    public static String getNameFromType(String type) {
//    	return search1(attribute, type, name);
        return typeNameMap.get(type);
    }

    // search the corresponding type of a given name
    public static String getTypeFromName(String name) {
//        return search2(attribute, name, type);
    	return typeNameMap.inverse().get(name);
    }
    
    public static boolean isValidType(String type, Level level) {
    	if (level == Level.EVENT) {
    		return !typeNameMap.containsKey(type) || eventStandardTypes.contains(type);
    	}
    	else {
    		return !typeNameMap.containsKey(type) || traceStandardTypes.contains(type);
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
