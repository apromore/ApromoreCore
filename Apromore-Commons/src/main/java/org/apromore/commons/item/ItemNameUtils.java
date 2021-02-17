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

package org.apromore.commons.item;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Various filename utility functions
 */
public final class ItemNameUtils {

    private static final String DELIMITERS = "\\._\\+\\- ";
    private static final String MERGED = "_merged";
    private static final int MAX_LENGTH = Constants.VALID_NAME_MAX_LENGTH - MERGED.length();
    private final static Pattern FILE_EXTENSION_PATTERN = Pattern.compile("(?<basename>.*)\\.(?<extension>[^/\\.]*)");

    /**
     * Check if the name an item (filename, folder) is valid based on default regex
     */
    public static final boolean hasValidName(String filename) {
        return Pattern.matches(Constants.VALID_NAME_REGEX, filename);
    }

    public static final boolean hasValidName(String filename, String pattern) {
        return Pattern.matches(pattern, filename);
    }

    /**
     * Merge two filenames
     *
     * @param filename1
     * @param filename2
     * @return Merged filename in the format {common_prefix}_merged
     */
    public static final String mergeNames(String filename1, String filename2) {
        String mergedName = null;
        int minLength = Math.min(filename1.length(), filename2.length());
        for (int i = 0; i < minLength; i++) {
            if (filename1.charAt(i) != filename2.charAt(i)) {
                if (i == 0) {
                    break;
                }
                if (ItemNameUtils.DELIMITERS.indexOf(filename1.charAt(i - 1)) >= 0) {
                    i = i - 1;
                }
                mergedName = filename1.substring(0, i);
                break;
            }
        }
        if (mergedName == null) {
            mergedName = filename1 + "_" + filename2;
        }
        if (mergedName.length() > ItemNameUtils.MAX_LENGTH) {
            mergedName = mergedName.substring(0, ItemNameUtils.MAX_LENGTH);
        }
        return mergedName + ItemNameUtils.MERGED;
    }

    /**
     * Merge filenames
     *
     * @param filenames
     * @return Merged filename in the format {common_prefix}_merged
     */
    public static final String mergeNames(List<String> filenames) {
        String mergedName = null;
        boolean found = false;
        int minLength = filenames.stream()
                .mapToInt(String::length)
                .min()
                .orElse(0);

        for (int i = 0; i < minLength; i++) {
            for (int j = 1; j < filenames.size(); j++) {
                if (filenames.get(j - 1).charAt(i) != filenames.get(j).charAt(i)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (i == 0) {
                    break;
                }
                if (ItemNameUtils.DELIMITERS.indexOf(filenames.get(0).charAt(i - 1)) >= 0) {
                    i = i - 1;
                }
                mergedName = filenames.get(0).substring(0, i);
                break;
            }
        }
        if (mergedName == null) {
            mergedName = String.join("_", filenames);
        }
        if (mergedName.length() > ItemNameUtils.MAX_LENGTH) {
            mergedName = mergedName.substring(0, ItemNameUtils.MAX_LENGTH);
        }
        return mergedName + ItemNameUtils.MERGED;
    }

    public static final String deriveName(List<String> existingNames, String commonName, String suffix) {
        int nth = existingNames.size() + 1;
        return commonName + suffix + " " + Integer.toString(nth);
    }

    public static String findBasename(String name) {
	Matcher matcher = FILE_EXTENSION_PATTERN.matcher(name);
	return matcher.matches() ? matcher.group("basename") : null;
    }

    public static String findExtension(String name) {
	Matcher matcher = FILE_EXTENSION_PATTERN.matcher(name);
	return matcher.matches() ? matcher.group("extension") : null;
    }

}

