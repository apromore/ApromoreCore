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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final String FILENAME_CONSTRAINT = "[a-zA-Z0-9 \\[\\]._+\\-()]+";

    private StringUtil() {
        throw new IllegalStateException("String Utility class");
    }

    public static String normalizeFilename(String name) {
        StringBuilder normalized = new StringBuilder();
        try {
            Pattern pattern = Pattern.compile(FILENAME_CONSTRAINT);
            Matcher matcher = pattern.matcher(name);

            while (matcher.find()) {
                normalized.append(matcher.group());
            }
        } catch (Exception e) {
            // ignore exception
        } finally {
            if (normalized.length() == 0) {
                normalized = new StringBuilder("Untitled");
            } else if (normalized.length() > 60) {
                normalized = new StringBuilder(normalized.substring(0, 59));
            }
        }
        return normalized.toString();
    }
}
