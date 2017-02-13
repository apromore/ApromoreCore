/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
 */

package org.apromore.canoniser.pnml.internal.pnml2canonical;

// Java 2 Standard Edition
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import java.util.List;

// Third party libraries
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for {@link RemoveDuplicateListItems}.
 */
public class RemoveDuplicateListItemsUnitTest {

    private static final List<String> empty = emptyList();

    /**
     * Test {@link RemoveDuplicateListItems#transform}.
     */
    @Test
    public void testTransform() throws Exception {
        f(empty, empty);
        f(Arrays.asList("a"), empty);
        f(Arrays.asList("a", "b", "c"), empty);
        f(Arrays.asList("a", "a"), Arrays.asList("a"));
        f(Arrays.asList("a", "a", "a", "a", "a"), Arrays.asList("a", "a", "a", "a"));
        f(Arrays.asList("b", "a", "b", "a", "b"), Arrays.asList("a", "b", "b"));
        f(Arrays.asList("a", "b", "a"), Arrays.asList("a"));
        f(Arrays.asList("a", "a", "b", "c", "c"), Arrays.asList("a", "c"));
        f(Arrays.asList("a", "b", "r", "a", "c", "a", "d", "a", "b", "r", "a"), Arrays.asList("a", "a", "a", "a", "b", "r"));
    }

    // Internal methods

    private void f(List<String> input, List<String> output) {
        List<String> list = new ArrayList(input);
        List<String> expected = new ArrayList(output);
        List<String> result = RemoveDuplicateListItems.transform(list);
        assertEquals(expected, result);
        assertEquals(expected, list);
    }
}
