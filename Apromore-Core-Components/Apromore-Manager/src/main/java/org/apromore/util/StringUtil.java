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
