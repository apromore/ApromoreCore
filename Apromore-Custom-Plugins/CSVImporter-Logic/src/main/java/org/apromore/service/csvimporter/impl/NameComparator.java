package org.apromore.service.csvimporter.impl;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A comparator which sorts embedded runs of digits in numerical rather than alphabetical order.
 *
 * So "a9" &lt; "a10" &lt; "b9" &lt; "b10".
 *
 * If numerical values are the same, the ordering is alphabetical: "01" &lt; "1".
 *
 * Beware that this differs from floating point ordering: "1.2" &lt; "1.11" and "1.0" &lt; "1.00".
 */
public class NameComparator implements Comparator<String> {

    /** Test harness. */
    public static void main(String[] arg) {
        NameComparator comparator = new NameComparator();
        int result = comparator.compare(arg[0], arg[1]);
        System.out.print(arg[0]);
        System.out.print(result < 0 ? " < " :
                         result > 0 ? " > " : " = ");
        System.out.println(arg[1]);
    }

    private Matcher m1, m2;

    public NameComparator() {

        // Extracts the leading token, which is a run of either only digits or only non-digits, or empty
        Pattern p = Pattern.compile("(?<token>(?<digits>\\d+)|(\\D*))(?<remainder>.*)");

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

        // If the leading tokens are both digits, compare them numerically EXCEPT if they're equal.
        // This prevents "01" from being treated as identical to "1".
        int result;
        String digits1 = m1.group("digits");
        String digits2 = m2.group("digits");
        if (digits1 != null && digits2 != null) {
            result = Integer.compare(Integer.parseUnsignedInt(digits1), Integer.parseUnsignedInt(digits2));
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
