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

import org.apromore.plugin.processdiscoverer.impl.util.StringValues;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class LogFilterTypeSelector {

    public static String[] type = new String[] {
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

    public static int getType(String attribute) {
        int t = Arrays.binarySearch(type, attribute);
        if(t < 0) return -1;
        return t;
    }

    public static int getName(String attribute) {
        int t = Arrays.binarySearch(name, attribute);
        if(t < 0) return -1;
        return t;
    }

    public static String getMatch(String attribute) {
        return search1(attribute, type, name);
    }

    public static String getReverseMatch(String attribute) {
        return search2(attribute, name, type);
    }

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

}
