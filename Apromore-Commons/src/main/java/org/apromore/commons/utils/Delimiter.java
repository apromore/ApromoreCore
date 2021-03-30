/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.commons.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Delimiter {
    public static final String PLACE_HOLDER = "s";

    /**
     * Find the delimiter for a given rows.
     *
     * @param rows 2D array of rows.
     * @return
     */
    public static String findDelimiter(final List<String> rows) {
        Map<String, Integer> delimiterMap = new ConcurrentHashMap<>();
        try {
            Arrays.stream(DelimiterType.values()).forEach(d -> {
                delimiterMap.put(d.getDelimiter(), findDelimiterCount(rows, d.getDelimiter()));
            });
            int maxValue = delimiterMap.values()
            .stream()
            .filter(val -> val > 0)
            .collect(Collectors.summarizingInt(Integer::intValue))
            .getMax();
            return delimiterMap.entrySet()
            .stream()
            .filter(entry -> maxValue == entry.getValue())
            .map(Map.Entry::getKey)
            .findFirst()
            .get();
        } catch (NullPointerException | NoSuchElementException e) {
            return DelimiterType.NONE.getDelimiter();
        }
    }

    /**
     * Find the delimiter count.
     *
     * @param rows
     * @param delimiter
     * @return
     * @throws NullPointerException
     */
    private static int findDelimiterCount(final List<String> rows, final String delimiter)
            throws NullPointerException  {
        int headRowCount = rows.isEmpty() ? -1 : rows.get(0).split(delimiter).length;
        // parse row per for DelimiterTypes and get integers List and compare if they all are bigger
        for (String row : rows) {
            if ((row + PLACE_HOLDER).split(delimiter).length != headRowCount) {
                return -1;
            }
        }
        return headRowCount;
    }
}
