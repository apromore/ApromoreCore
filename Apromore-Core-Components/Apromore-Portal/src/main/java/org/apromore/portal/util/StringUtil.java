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

package org.apromore.portal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

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

    public static String getFileNameByContentDisposition(String contentDisposition) {
        String fileNameNoExt = "";
        if (isBlank(contentDisposition)) {
            return fileNameNoExt;
        }
        String contentDispositionFileName = contentDisposition;
        int index = contentDispositionFileName.indexOf("filename");
        if (index < 0) {
            return fileNameNoExt;
        }
        index = contentDispositionFileName.indexOf("=", index);
        if (index < 0) {
            return fileNameNoExt;
        }
        contentDispositionFileName = contentDispositionFileName.substring(index + 1).trim();

        // remove double-quotes
        contentDispositionFileName = contentDispositionFileName.replaceAll("\"", "");
        index = contentDispositionFileName.lastIndexOf("'");
        if (index > -1) {
            contentDispositionFileName = contentDispositionFileName.substring(index + 1);
        }
        // Check encoding
        if (contentDispositionFileName.indexOf("%") > -1) {
            fileNameNoExt = urlDecode(contentDispositionFileName, "UTF-8");
        }
        if (fileNameNoExt.indexOf("?") > -1) {
            fileNameNoExt = urlDecode(contentDispositionFileName, "GBK");
        }
        if (fileNameNoExt.indexOf("?") > -1) {// Use original one
            fileNameNoExt = contentDispositionFileName;
        }
        return fileNameNoExt.trim();
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

    /**
     * Remove whitespaces
     *
     * @param s
     * @return
     */
    public static boolean isBlank(String s) {
        if (s == null || s.trim().length() == 0) {
            return true;
        }
        return false;
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

    public static String urlEncode(String s, String charset) {
        if (isBlank(s)) {
            return "";
        }
        if (isBlank(charset)) {
            charset = "UTF-8";
        }
        try {
            return URLEncoder.encode(s.trim(), charset);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return s;
    }

    public static String urlEncode(String s) {
        return urlEncode(s, "");
    }

    public static String urlDecode(String s, String charset) {
        if (isBlank(s)) {
            return "";
        }
        if (isBlank(charset)) {
            charset = "UTF-8";
        }
        try {
            return URLDecoder.decode(s, charset);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return s;
    }

}
