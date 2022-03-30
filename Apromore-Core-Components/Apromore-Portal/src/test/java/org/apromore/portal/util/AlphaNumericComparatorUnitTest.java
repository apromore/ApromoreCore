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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AlphaNumericComparatorUnitTest {

    @ParameterizedTest
    @CsvSource({
        "procure, procurePay",
        "procureMent, procurePay",
        "1procure, 2procure",
        "1.1procure, 2procure",
        "1.10procure, 2procure"
    })
    void similarResultWithRegularComparison(String name1, String name2) {
        assertTrue((name1.compareTo(name2)) < 0);
        assertTrue((AlphaNumericComparator.compareTo(name1, name2)) < 0);
    }

    @ParameterizedTest
    @CsvSource({
        "1.3procure, 1.10procure",
        "procure1.3, procure1.10",
        "procure1.3payment, procure1.10payment"
    })
    void differentResultWithRegularComparison(String name1, String name2) {
        assertFalse((name1.compareTo(name2)) < 0);

        assertTrue((AlphaNumericComparator.compareTo(name1, name2)) < 0);
    }


}
