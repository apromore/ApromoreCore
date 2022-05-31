/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.logimporter.utilities;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apromore.service.logimporter.constants.ColumnType;
import org.apromore.service.logimporter.constants.Constants;
import org.apromore.service.logimporter.dateparser.DateUtil;

public class ImporterStringUtils {

    private ImporterStringUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static char getMaxOccurringChar(String str) {
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = str.length() - 1; i >= 0; i--) {
            if (!Character.isLetter(str.charAt(i))) {
                for (char supportedSeparator : Constants.supportedSeparators) {
                    if (str.charAt(i) == supportedSeparator) {
                        char ch = str.charAt(i);
                        if (++charcnt[ch] >= maxcnt) {
                            maxcnt = charcnt[ch];
                            maxchar = ch;
                        }
                    }
                }
            }
        }
        return maxchar;
    }

    /**
     * Get the type of a string.
     * 1. Integer
     * 2. Real (this data-type covers float/double, i.e. numbers with decimals)
     * 3. String (this data-type covers strings and enumerated types)
     * 4. Timestamp  (this covers date-times and dates, we will not have a special data type for “time of the day”, this
     * feature could be added later).
     *
     * @param string string to check
     * @return type of the string
     */
    public static ColumnType getColumnType(final String string) {
        if (isInt(string)) {
            return ColumnType.INT;
        } else if (isDouble(string)) {
            return ColumnType.DOUBLE;
        } else if (isBool(string)) {
            return ColumnType.BOOLEAN;
        } else if (isTimestamp(string)) {
            return ColumnType.TIMESTAMP;
        } else {
            return ColumnType.STRING;
        }
    }

    /**
     * Get the type of column from a list of strings.
     *
     * @param stringList string to check
     * @return type of the string
     */
    public static ColumnType getColumnType(final List<String> stringList) {
        EnumMap<ColumnType, Integer> temp = new EnumMap<>(ColumnType.class);
        temp.put(ColumnType.BOOLEAN, 0);
        temp.put(ColumnType.INT, 0);
        temp.put(ColumnType.STRING, 0);
        temp.put(ColumnType.DOUBLE, 0);
        temp.put(ColumnType.TIMESTAMP, 0);

        List<String> filteredList = stringList.stream()
            .filter(Objects::nonNull)
            .filter(Predicate.not(String::isEmpty))
            .collect(Collectors.toList());

        filteredList.forEach(st -> {
            ColumnType columnType = getColumnType(st);
            temp.put(columnType, temp.get(columnType) + 1);
        });

        int max = 0;
        ColumnType maxType = ColumnType.STRING;
        for (Map.Entry<ColumnType, Integer> entry : temp.entrySet()) {
            if (max < entry.getValue()) {
                max = entry.getValue();
                maxType = entry.getKey();
            }
        }

        return maxType;
    }

    /**
     * Determine whether a string is an integer.
     *
     * @param string string to check
     * @return true if the string is an integer, false otherwise
     */
    private static boolean isInt(final String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Determine whether a string is a double.
     *
     * @param string string to check
     * @return true if the string is a double, false otherwise
     */
    private static boolean isDouble(final String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Determine whether a string is a boolean.
     *
     * @param string string to check
     * @return true if the string is a boolean, false otherwise
     */
    private static boolean isBool(final String string) {
        return ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string));
    }

    /**
     * Determine whether a string is a timestamp.
     *
     * @param string string to check
     * @return true if the string is a timestamp, false otherwise
     */
    private static boolean isTimestamp(final String string) {
        return DateUtil.isValidDate(string);
    }

}
