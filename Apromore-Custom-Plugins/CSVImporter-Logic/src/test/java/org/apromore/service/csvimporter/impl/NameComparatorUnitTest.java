package org.apromore.service.csvimporter.impl;

import java.util.Comparator;
import org.junit.Test;

/** Test suite for {@link NameComparator}. */
public class NameComparatorUnitTest {

    /** Test instance. */
    private Comparator<String> c = new NameComparator();


    // Test cases

    /** Unit test for the {@link NameComparator#compare} method. */
    @Test
    public void test() {
        assertEqual("a", "a");
        assertOrder("a", "b");
        assertOrder("a9", "a10");    // numerically rather than lexically ordered
        assertOrder("1a", "a1");
        assertOrder("01", "1");      // numerically equal but lexically distinct
        assertEqual("", "");         // empty string
        assertOrder("", "a");        // empty string
        assertOrder("1.2", "1.12");  // not what you'd expect for floating point numbers
        assertOrder("1.01", "1.1");
        assertOrder(" ", "1");       // untrimmed spaces matter
        assertOrder("(A)", "65");    // punctuation lexically precedes digits
    }

    @Test(expected = NullPointerException.class)
    public void testNullArgumentLHS() {
        c.compare(null, "a");
    }

    @Test(expected = NullPointerException.class)
    public void testNullArgumentRHS() {
        c.compare("b", null);
    }


    // Internal methods

    private void assertEqual(String a, String b) {
        assert c.compare(a, b) == 0;
        assert c.compare(b, a) == 0;
        assert a.equals(b);
    }

    private void assertOrder(String a, String b) {
        assert c.compare(a, b) < 0;
        assert c.compare(b, a) > 0;
        assert !a.equals(b);
    }
}
