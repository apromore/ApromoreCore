package org.apromore.util;

/**
 * @author Chathura Ekanayake
 */
public class VersionNameUtil {

    /**
     * Constructs the next version name based on the given version name.
     * Version sequence: 1.0, 1.1, 1.2, ..., 1.9, 2.0, 2.1, ...
     *
     * @param versionNumber Preceding version number
     * @return Next version number
     */
    public static String getNextVersionName(final Double versionNumber) {
        double dvn = versionNumber;
        dvn += 0.1;
        return Double.toString(dvn);
    }
}
