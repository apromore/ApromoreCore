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

package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apromore.portal.common.Constants;
import org.apromore.portal.model.SearchHistoriesType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/** Test suite for {@link SimpleSearchController}. */
class SimpleSearchControllerUnitTest {

    // Test cases.

    /** Test {@link SimpleSearchController#addSearchHistory} method. */
    @Test
    void testAddSearchHistory_duplicates_and_overflows() throws Exception {
        assertEquals(Constants.maxSearches, 10);

        // When there are duplicates, only the most recent is retained
        assertSearchHistory(new String[] {"two", "one"},
            "one", "two", "one", "two", "one");

        // When more than ten elements are added, the earliest is discarded
        assertSearchHistory(new String[] {"two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven"},
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven");

        // Adding a duplicate to a full history may reorder it but can't change the population
        assertSearchHistory(new String[] {"two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "one"},
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "one");

        assertSearchHistory(new String[] {"one", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "two"},
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "two");

        assertSearchHistory(new String[] {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"},
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "ten");
    }

    /** Test whether the {@link SimpleSearchController#addSearchHistory} method corrects invalid states. */
    @Test
    void testAddSearchHistory_error_correction() throws Exception {
        SearchHistoriesType sh = new SearchHistoriesType();
        sh.setSearch("one");
        sh.setNum(1);

        // Create an invalid history with duplicate "one" entries
        List<SearchHistoriesType> result = new ArrayList<>();
        result.add(sh);
        result.add(sh);

        // Perform the method under test
        result = SimpleSearchController.addSearchHistory(result, "two");

        // Confirm that the invalid duplicate "one" entries have been corrected
        assertArrayEquals(new String[] {"one", "two"}, result.stream().map(SearchHistoriesType::getSearch).toArray(String[]::new));
    }

    // Internal methods

    /**
     * @param expected  the expected search history after <var>searches</var> have been added
     * @param searches  a series of search terms to add to the search history
     */
    private void assertSearchHistory(String[] expected, String... searches) throws Exception {
        List<SearchHistoriesType> result = new ArrayList<>();
        for (String search: searches) {
            result = SimpleSearchController.addSearchHistory(result, search);
        }

        assertArrayEquals(expected, result.stream().map(SearchHistoriesType::getSearch).toArray(String[]::new));
    }
}
