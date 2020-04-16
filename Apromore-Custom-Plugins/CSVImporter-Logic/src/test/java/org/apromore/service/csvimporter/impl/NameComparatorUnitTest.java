/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
        assertOrder("-1", "-2");     // not what you'd expect for negative numbers
        assertOrder(" ", "1");       // untrimmed spaces matter
        assertOrder("(A)", "65");    // punctuation lexically precedes digits
        assertOrder("123456789012345678901", "1234567890123456789012");  // larger than 64-bit
        assertOrder("0", "1");       // zero ordering
        assertOrder("0", "00");      // different numbers of zeroes ordering
        assertOrder("", "0");        // zero vs empty string
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
