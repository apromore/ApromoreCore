/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import java.math.BigDecimal;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;

/*
 * @author Mohammad Ali
 */

public class AlphaNumericComparator {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(AlphaNumericComparator.class);

    private AlphaNumericComparator(){}

    // Taken from https://javaprogramming.language-tutorial.com/2012/10/alphanumeric-string-sorting-using-java.html
    public static int compareTo(String name1, String name2) {
        try {
            if (name1 == null || name2 == null) {
                return 0;
            }
            name1 = name1.toUpperCase();
            name2 = name2.toUpperCase();
            int lengthFirstStr = name1.length();
            int lengthSecondStr = name2.length();

            int index1 = 0;
            int index2 = 0;

            while (index1 < lengthFirstStr && index2 < lengthSecondStr) {
                char ch1 = name1.charAt(index1);
                char ch2 = name2.charAt(index2);

                char[] space1 = new char[lengthFirstStr];
                char[] space2 = new char[lengthSecondStr];

                index1 = getFirstNonMatchTypeIndex(name1, lengthFirstStr, index1, ch1, space1);
                index2 = getFirstNonMatchTypeIndex(name2, lengthSecondStr, index2, ch2, space2);

                String str1 = new String(space1);
                String str2 = new String(space2);

                int result;
                if (Character.isDigit(space1[0]) && Character.isDigit(space2[0])) {
                    BigDecimal firstNumberToCompare=new BigDecimal(str1.trim());
                    BigDecimal secondNumberToCompare=new BigDecimal(str2.trim());
                    result = firstNumberToCompare.compareTo(secondNumberToCompare);
                } else {
                    result = str1.trim().compareTo(str2.trim());
                }

                if (result != 0) {
                    return result;
                }
            }
            return lengthFirstStr - lengthSecondStr;
        } catch (Exception ex) {
            LOGGER.error("Error in compare string", ex);
            return 0;
        }
    }

    private static int getFirstNonMatchTypeIndex(String name, int lengthStr, int index, char ch, char[] space) {
        int loc = 0;
        do {
            space[loc++] = ch;
            index++;

            if (index < lengthStr) {
                ch = name.charAt(index);
            } else {
                break;
            }
        } while (Character.isDigit(ch) == Character.isDigit(space[0]));
        return index;
    }


}
