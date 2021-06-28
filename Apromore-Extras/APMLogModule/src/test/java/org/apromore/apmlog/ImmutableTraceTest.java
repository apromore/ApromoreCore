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

import org.apromore.apmlog.immutable.ImmutableLog;
import org.apromore.apmlog.immutable.ImmutableTrace;
import org.deckfour.xes.model.XLog;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class ImmutableTraceTest {

    private static long[] expectedStartTimes =
            new long[]{1305381600000L, 1305900000000L, 1305986400000L, 1306245600000L};
    private static long[] expectedEndTimes =
            new long[]{1305986400000L, 1305900000000L, 1306418400000L, 1306504800000L};

    public static void testStartEndTimestamps(XLog xLog) {
        ImmutableLog immutableLog = (ImmutableLog) LogFactory.convertXLog(xLog);

        List<ATrace> traceList = immutableLog.getTraceList();

        long[] startTimes = new long[4];
        long[] endTimes = new long[4];

        for (int i = 0; i < traceList.size(); i++) {
            startTimes[i] = traceList.get(i).getStartTimeMilli();
            endTimes[i] = traceList.get(i).getEndTimeMilli();
        }

        assertArrayEquals(expectedStartTimes, startTimes);
        assertArrayEquals(expectedEndTimes, endTimes);
    }
}
