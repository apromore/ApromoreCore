/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.portal.util;

import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final String[] DICTIONARY = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
    private static final StringBuilder STRING_BUILDER;
    private static final Formatter FORMATTER;
    /**
     * Regex used to parse content-disposition headers
     */
    private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern
            .compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"");

    static {
        STRING_BUILDER = new StringBuilder();
        FORMATTER = new Formatter(STRING_BUILDER, Locale.getDefault());
    }

    /*
     * Parse the Content-Disposition HTTP Header. The format of the header is
     * defined here: http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html This
     * header provides a filename for content that is going to be downloaded to
     * the file system. We only support the attachment type.
     */
    public static String contentDispositionFileName(String contentDisposition) {
        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            if (m.find()) {
                return m.group(1);
            }
        } catch (IllegalStateException ex) {
            // This function is defined as returning null when it can't parse
            // the header
        }
        return null;
    }

    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255)
            return false;
        else
            return fileName.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Method to format bytes in human readable format
     *
     * @param bytes  - the value in bytes
     * @param digits - number of decimals to be displayed
     * @return human readable format string
     */
    public static String formatBytes(double bytes, int digits) {
        int index;
        for (index = 0; index < DICTIONARY.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        return formatString("%." + digits + "f", bytes) + " " + DICTIONARY[index];
    }

    public static String formatString(String format, Object... args) {
        STRING_BUILDER.setLength(0);
        return FORMATTER.format(format, args).toString();
    }

    public static String formatPercent(double number) {
        return NumberFormat.getPercentInstance().format(number);
    }
}
