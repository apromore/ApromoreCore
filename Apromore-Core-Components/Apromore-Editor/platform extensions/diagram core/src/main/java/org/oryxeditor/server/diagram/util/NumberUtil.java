/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.oryxeditor.server.diagram.util;


public class NumberUtil {

    /**
     * Default maximum variation allowed for numbers to be considered as the same.
     * Currently 1x10<sup>-6</sup>
     */
    public static final double DEFAULT_DELTA = 1E-6;

    /**
     * @param num1
     * @param num2
     * @return true, if both numbers are within a minimal delta
     * @see #DEFAULT_DELTA
     */
    public static boolean areNumbersSame(Number num1, Number num2) {
        return areNumbersWithinDelta(num1, num2, DEFAULT_DELTA);
    }


    /**
     * @param num1
     * @param num2
     * @param delta
     * @return true, if num2 is within [num1-delta, num1+delta] (inclusive), false if either number is null
     */
    public static boolean areNumbersWithinDelta(Number num1, Number num2, Double delta) {
        if (num1 == null || num2 == null)
            return false;

        return (num1.doubleValue() <= num2.doubleValue() + delta) &&
                (num1.doubleValue() >= num2.doubleValue() - delta);
    }


    /**
     * Parses a double from the given string, returns null if no double could be parsed.
     * <p/>
     * Handles "NaN" and Unicode infinity characters (returns negative or positive infinite depending on the leading sign)
     *
     * @param numberString
     * @return double value or null
     */
    public static Double createDouble(String numberString) {
        if (numberString == null)
            return null;
        else if (numberString.equals("NaN"))
            return Double.NaN;
        else if (numberString.equals("-\u221E"))
            return Double.NEGATIVE_INFINITY;
        else if (numberString.equals("\u221E"))
            return Double.POSITIVE_INFINITY;

        try {
            return Double.parseDouble(numberString);
        } catch (NumberFormatException e) {
            return null;
        }
    }


    /**
     * Parses a float from the given string, returns null if no float could be parsed.
     * <p/>
     * Handles "NaN" and Unicode infinity characters (returns negative or positive infinite depending on the leading sign)
     *
     * @param numberString
     * @return float value or null
     */
    public static Float createFloat(String numberString) {
        if (numberString == null)
            return null;
        else if (numberString.equals("NaN"))
            return Float.NaN;
        else if (numberString.equals("-\u221E"))
            return Float.NEGATIVE_INFINITY;
        else if (numberString.equals("\u221E"))
            return Float.POSITIVE_INFINITY;
        try {
            return Float.parseFloat(numberString);
        } catch (NumberFormatException e) {
            return null;
        }
    }


    /**
     * Parses a int from the given string, returns null if no int could be parsed.
     *
     * @param numberString
     * @return int value or null
     */
    public static Integer createInt(String numberString) {
        if (numberString == null)
            return null;

        try {
            return Integer.parseInt(numberString);
        } catch (NumberFormatException e) {
            return null;
        }
    }


    /**
     * Parses a long from the given string, returns null if no long could be parsed.
     *
     * @param numberString
     * @return long value or null
     */
    public static Long createLong(String numberString) {
        if (numberString == null)
            return null;

        try {
            return Long.parseLong(numberString);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
