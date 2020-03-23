package org.apromore.logfilter.criteria.impl.util;

public class Util {
    public static boolean isNumeric(String s) {
        return s.matches("-?\\d+(\\.\\d+)?") && !s.contains("_");
    }
}
