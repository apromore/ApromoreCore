/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

import org.apromore.portal.model.SearchHistoriesType;
import org.junit.Ignore;
import org.junit.Test;

/** Test suite for {@link ImportController}. */
public class SimpleSearchControllerUnitTest {

    // Test cases.

    /** Test the {@link ImportController#importFile} method with no file importers and a CSV log. */
    @Ignore
    @Test
    public void testAddSearchHistory() throws Exception {
	
        List<SearchHistoriesType> result = SimpleSearchController.addSearchHistory(new ArrayList<>(), "one");
        result = SimpleSearchController.addSearchHistory(result, "two");
        result = SimpleSearchController.addSearchHistory(result, "three");
        result = SimpleSearchController.addSearchHistory(result, "four");
        result = SimpleSearchController.addSearchHistory(result, "five");
        result = SimpleSearchController.addSearchHistory(result, "six");
        result = SimpleSearchController.addSearchHistory(result, "seven");
        result = SimpleSearchController.addSearchHistory(result, "eight");
        result = SimpleSearchController.addSearchHistory(result, "nine");
        result = SimpleSearchController.addSearchHistory(result, "ten");
        result = SimpleSearchController.addSearchHistory(result, "eleven");

        assertEquals(10, result.size());
        assertEquals("two", result.get(0).getSearch());
        assertEquals(new Integer(1), result.get(0).getNum());
        assertEquals("eleven", result.get(9).getSearch());
        assertEquals(new Integer(10), result.get(9).getNum());
    }
}
