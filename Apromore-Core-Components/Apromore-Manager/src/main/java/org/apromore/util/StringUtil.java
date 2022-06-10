/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
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
     * Illegal Characters on Various Operating Systems
     * / ? < > \ : * | "
     * https://kb.acronis.com/content/39790
     *
     * Unicode Control codes
     * C0 0x00-0x1f & C1 (0x80-0x9f)
     * http://en.wikipedia.org/wiki/C0_and_C1_control_codes
     *
     * @param name user input filename
     * @return normalized file name
     * @see "http://en.wikipedia.org/wiki/Filename#Reserved_characters_and_words"
     */
    public static String normalizeFilename(String name) {

        String illegalRe = "[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|\\$|\\&|\\@|\\#|\\^|,]";
        String controlRe = "[\\x00-\\x1f\\x80-\\x9f]";

        String normalized = name.trim().replaceAll(illegalRe, "")
            .replaceAll(controlRe, "")
            .replace(".", "_");

        if (normalized.endsWith("_")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (normalized.length() == 0) {
            normalized = "Untitled";
        // Capped at 60 characters in length
        } else if (normalized.length() > 60) {
            normalized = normalized.substring(0, 59);
        }
        return normalized;
    }
}
