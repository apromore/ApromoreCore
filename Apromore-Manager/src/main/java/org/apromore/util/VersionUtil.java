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
