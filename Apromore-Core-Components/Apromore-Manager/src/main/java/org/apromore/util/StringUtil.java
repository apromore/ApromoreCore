/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.util;

public class StringUtil {

    private StringUtil() {
        throw new IllegalStateException("String Utility class");
    }

    /**
     * Removes all illegal filename characters from a given String
     *
     * @param name user input filename
     * @return normalized file name
     * @see "http://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words"
     */
    public static String normalizeFilename(String name) {

        String normalized = name.trim();

        // remove illegal characters
        normalized = normalized.replaceAll(
                "[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]",
                "");

        // replace . dots with _ and remove the _ if at the end
        normalized = normalized.replaceAll("\\.", "_");
        if (normalized.endsWith("_")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (normalized.length() == 0) {
            normalized = "Untitled";
        } else if (normalized.length() > 60) {
            normalized = normalized.substring(0, 59);
        }
        return normalized;
    }
}
