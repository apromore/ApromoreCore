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

import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.immutable.ImmutableLog;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import static org.junit.Assert.assertArrayEquals;

public class LogsMethodsTests {

    // org:resource - R1 occur in 4 activities
    // org:resource - R1 occur in 3 cases
    private static final int[] expectedR1 = new int[]{4, 3};

    private static final int[][] expectedActIndexes = new int[][]{
            {0, 1},
            {0},
            {0, 2},
            {0, 1}
    };


    public static void testActivityNameIndexes(XLog xLog) {
        APMLog apmLog = LogFactory.convertXLog(xLog);
        ImmutableLog immutableLog = (ImmutableLog) apmLog;

        // Test ActivityNameIndexes
        for (int i = 0; i < immutableLog.size(); i++) {
            IntArrayList actNameIndexes = immutableLog.getActivityNameIndexes(immutableLog.get(i));
            assertArrayEquals(expectedActIndexes[i], actNameIndexes.toArray());
        }

        // Cloned version
        ImmutableLog clone = (ImmutableLog) immutableLog.clone();

        // Test ActivityNameIndexes of the cloned version
        for (int i = 0; i < immutableLog.size(); i++) {
            IntArrayList actNameIndexes = clone.getActivityNameIndexes(immutableLog.get(i));
            assertArrayEquals(expectedActIndexes[i], actNameIndexes.toArray());
        }

        // PLog
        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        PLog pLog = apmLogFilter.getPLog();

        // Test ActivityNameIndexes of PLog
        for (int i = 0; i < immutableLog.size(); i++) {
            PTrace pTrace = new PTrace(i, immutableLog.get(i), pLog);
            IntArrayList actNameIndexes = pLog.getActivityNameIndexes(pTrace);
            assertArrayEquals(expectedActIndexes[i], actNameIndexes.toArray());
        }

        // Updated PLog
        pLog.updateStats();

        // Test ActivityNameIndexes of PLog after update
        for (int i = 0; i < immutableLog.size(); i++) {
            PTrace pTrace = new PTrace(i, immutableLog.get(i), pLog);
            IntArrayList actNameIndexes = pLog.getActivityNameIndexes(pTrace);
            assertArrayEquals(expectedActIndexes[i], actNameIndexes.toArray());
        }
    }





}
