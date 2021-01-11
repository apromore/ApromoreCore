/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.apmlog;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

public class CaseStatsTest {
    public static void testCaseVariantFrequency(APMLog apmLog, Map<String, String> expectedMap, APMLogUnitTest parent)
            throws Exception
    {
        UnifiedMap<Integer, Integer> result = apmLog.getCaseVariantIdFrequencyMap();
        UnifiedMap<Integer, Integer> expected = UnifiedMap.newMap(
            expectedMap.entrySet().stream().collect(
                Collectors.toMap(e -> Integer.valueOf(e.getKey()), e -> Integer.valueOf(e.getValue()))
            )
        );

        if (!result.equals((expected))) {
            throw new AssertionError("TEST FAILED. CASE VARIANT FREQUENCY MISMATCH.\n");
        } else {
            parent.printString("'Case Variant Frequency' test PASS.\n");
        }
    }
}
