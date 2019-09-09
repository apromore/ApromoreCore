package org.apromore.logman.utils;

public class MathUtils {
	/**
	 * values are assumed to be pre-sorted 
	 */
    public static long quartile(long [] values, double lowerPercent) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("The data array either is null or does not contain any data.");
        }

        // Rank order the values
        int n = (int) Math.round(values.length * lowerPercent / 100);
        return values[n];

    }
}
