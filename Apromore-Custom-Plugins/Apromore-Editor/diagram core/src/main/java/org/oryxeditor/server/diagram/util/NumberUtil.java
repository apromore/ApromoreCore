
package org.oryxeditor.server.diagram.util;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */


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
