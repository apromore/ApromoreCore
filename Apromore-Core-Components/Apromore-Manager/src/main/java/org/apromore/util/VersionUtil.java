/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.CharMatcher;

/**
 * @author Chathura Ekanayake
 */
public class VersionUtil {


    /**
     * Extract the version number from a string.
     * @param versionNumber the version number (eg. 1.2, 1.3-beta)
     * @return an Array of strings that contain the version.
     */
    public static String[] extractVersionNumber(final String versionNumber) {
        List<String> version = new ArrayList<>();
        char previousChar = '\0';

        for (char c : versionNumber.toCharArray()) {
            if (CharMatcher.JAVA_DIGIT.matches(c)) {
                if (CharMatcher.JAVA_DIGIT.matches(previousChar)) {
                    // We have a number greater than 9.
                    String tmp = (version.get(version.size()-1)) + c;
                    version.remove(version.size()-1);
                    version.add(tmp);
                } else if (CharMatcher.JAVA_LETTER.matches(previousChar)) {
                    // We have a number besides a letter? what to do here.
                    version.add(String.valueOf(c));
                } else {
                    version.add(String.valueOf(c));
                }
            }
            previousChar = c;
        }

        return version.toArray(new String[version.size()]);
    }


    public static String incrementVersionNumber(final String[] versionNumber) {
        StringBuilder version = new StringBuilder();
        int index = 1;

        for (String num : versionNumber) {
            if (index++ == versionNumber.length) {
                num = incrementNumber(num);
            }
            if (version.length() > 0) {
                version.append(".");
            }
            version.append(num);
        }

        return version.toString();
    }


    private static String incrementNumber(final String number) {
        return String.valueOf(Integer.valueOf(number) + 1);
    }
}
