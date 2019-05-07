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

package org.apromore.plugin.processdiscoverer.impl.filter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.plugin.processdiscoverer.impl.util.StringValues;
import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified: Bruce: add checkLevelValidity()
 * Note that this class only contains standard filter types
 * There are non-standard types which are other attributes available in logs
 */
public class LogFilterTypeSelector {

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
    
    private static String[] name = new String[] {
            "Activity",
            "Direct Follow Relation",
            "Duration",
            "Eventually Follow Relation",
            "Lifecycle",
            "Group",
            "Resource",
            "Role",
            "Time-frame"
    };
    
    public static List<String> getTypes() {
    	List<String> types = Arrays.asList(type);
    	return Collections.unmodifiableList(types);
    }
    
    
    public static List<String> getNames() {
    	List<String> names = Arrays.asList(name);
    	return Collections.unmodifiableList(names);
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
    

    //index of the attribute in the list
    public static Type getType(String attribute) {
        int t = Arrays.binarySearch(type, attribute);
        if(t < 0) {
        	return Type.UNKNOWN;
        }
        return typeMap.get(attribute);
    }

    //index of the name in the list
    public static int getName(String attribute) {
        int t = Arrays.binarySearch(name, attribute);
        if(t < 0) return -1;
        return t;
    }

    // search attribute in type and return the corresponding name
    public static String getMatch(String attribute) {
        return search1(attribute, type, name);
    }

 // search attribute in name and return the corresponding type
    public static String getReverseMatch(String attribute) {
        return search2(attribute, name, type);
    }

    // The index is not 1-1 between the two arrays as the array values must be ordered
    private static String search1(String attribute, String[] origin, String[] translation) {
        int t = Arrays.binarySearch(origin, attribute);
        switch (t) {
            case 0 : return translation[0];
            case 1 : return translation[1];
            case 2 : return translation[3];
            case 3 : return translation[4];
            case 4 : return translation[5];
            case 5 : return translation[6];
            case 6 : return translation[7];
            case 7 : return translation[2];
            case 8 : return translation[8];
            default : return null;
        }
    }

    // The index is not 1-1 between the two arrays as the array values must be ordered
    private static String search2(String attribute, String[] origin, String[] translation) {
        int t = Arrays.binarySearch(origin, attribute);
        switch (t) {
            case 0 : return translation[0];
            case 1 : return translation[1];
            case 3 : return translation[2];
            case 4 : return translation[3];
            case 5 : return translation[4];
            case 6 : return translation[5];
            case 7 : return translation[6];
            case 2 : return translation[7];
            case 8 : return translation[8];
            default : return null;
        }
    }
    
    public static boolean checkLevelValidity(String attribute, Level level) {
    	int t = Arrays.binarySearch(type, attribute);
    	if (level == Level.EVENT) {
	        switch (t) {
	            case 0 : return true;
	            case 1 : return false;
	            case 2 : return false;
	            case 3 : return true;
	            case 4 : return true;
	            case 5 : return true;
	            case 6 : return true;
	            case 7 : return true;
	            case 8 : return true;
	            default : return true;
	        }
    	}
    	else {
	        switch (t) {
	            case 0 : return true;
	            case 1 : return true;
	            case 2 : return true;
	            case 3 : return true;
	            case 4 : return true;
	            case 5 : return true;
	            case 6 : return true;
	            case 7 : return true;
	            case 8 : return true;
	            default : return true;
	        }    		
    	}
    }

}
