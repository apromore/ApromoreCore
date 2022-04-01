/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
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

package org.apromore.service.logimporter.utilities;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A comparator which sorts embedded runs of digits in numerical rather than alphabetical order.
 *
 * <p>So "a9" &lt; "a10" &lt; "b9" &lt; "b10".
 *
 * <p>If numerical values are the same, the ordering is alphabetical: "01" &lt; "1".
 *
 * <p>Beware that negative signs and decimal point are not treated as part of the numbers.
 * This can lead to unexpected behaviors: "-1" &lt; "-2", "1.2" &lt; "1.11", and "1.0" &lt; "1.00".
 */
public class NameComparator implements Comparator<String> {

    private Matcher m1;
    private Matcher m2;

    public NameComparator() {
        // Extracts the leading token, which is a run of either only digits or only non-digits, or empty
        // Leading zeroes are ignored by the "digits" capturing group
        Pattern p = Pattern.compile("(?<token>(0*(?<digits>\\d+))|(\\D*))(?<remainder>.*)");
        m1 = p.matcher("");
        m2 = p.matcher("");
    }

    @Override
    public int compare(String o1, String o2) {

        // Extract the leading token (either all digits or all non-digits) from each of the two parameters.
        m1.reset(o1);
        m2.reset(o2);
        boolean b1 = m1.matches();  // ignore the result; we just want the side effect of setting the matcher groups
        assert b1;
        boolean b2 = m2.matches();
        assert b2;

        // If the leading tokens are both numerical, compare them numerically EXCEPT if they're equal.
        // This prevents "01" from being treated as identical to "1".
        int result;
        String digits1 = m1.group("digits");
        String digits2 = m2.group("digits");
        if (digits1 != null && digits2 != null) {
            // Numbers with more digits are larger
            result = digits1.length() - digits2.length();
            if (result != 0) {
                return result;
            }

            // Alphabetic and numerical ordering is the same if they have the same number of digits
            result = digits1.compareTo(digits2);
            if (result != 0) {
                return result;
            }
        }

        // Compare the leading tokens lexicographically, recursing to the next token if they're equal and non-empty.
        String token1 = m1.group("token");
        String token2 = m2.group("token");
        result = token1.compareTo(token2);
        return result != 0 || token1.isEmpty() ? result : compare(m1.group("remainder"), m2.group("remainder"));
    }
}
