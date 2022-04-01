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
package org.apromore.apmlog;

import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.logobjects.ImmutableLog;
import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class LogsDurationsTest {

    private static final double[] expected = new double[]{
            0,
            1000 * 60 * 60 * 24 * 4d,
            1000 * 60 * 60 * 24 * 3.75d,
            1000 * 60 * 60 * 24 * 7d
    };

    public static void testAPMLogDurations(APMLog apmLog) {
        proceedAPMLogDurTest(apmLog);
    }

    public static void testPLogDurations(APMLog apmLog) throws EmptyInputException {
        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        PLog pLog = apmLogFilter.getPLog();
        proceedPLogDurTest(pLog);

        // test converted version
        APMLog converted = pLog.toImmutableLog();
        proceedAPMLogDurTest(converted);

        // test updated version
        proceedPLogDurTest(pLog);
    }

    public static void testImmutableLogDurations(APMLog apmLog) {
        ImmutableLog immutableLog = (ImmutableLog) apmLog;
        proceedImmutableLogDurTest(immutableLog);
    }

    public static void testClonedImmutableLogDurations(APMLog apmLog) throws EmptyInputException {
        ImmutableLog immutableLog = (ImmutableLog) apmLog;

        ImmutableLog clone = (ImmutableLog) immutableLog.deepClone();
        proceedImmutableLogDurTest(clone);
    }

    private static void proceedAPMLogDurTest(APMLog apmLog) {
        DoubleArrayList durs = TimeStatsProcessor.getCaseDurations(apmLog.getTraces());

        double minDur = durs.min();
        double medDur = durs.median();
        double avgDur = durs.average();
        double maxDur = durs.max();
        proceedResult(minDur, medDur, avgDur, maxDur);
    }

    private static void proceedImmutableLogDurTest(ImmutableLog immutableLog) {
        DoubleArrayList durs = TimeStatsProcessor.getCaseDurations(immutableLog.getTraces());

        double minDur = durs.min();
        double medDur = durs.median();
        double avgDur = durs.average();
        double maxDur = durs.max();
        proceedResult(minDur, medDur, avgDur, maxDur);
    }

    private static void proceedPLogDurTest(PLog pLog) {
        DoubleArrayList durs =
                TimeStatsProcessor.getCaseDurations(pLog.getPTraces().stream().collect(Collectors.toList()));

        double minDur = durs.min();
        double medDur = durs.median();
        double avgDur = durs.average();
        double maxDur = durs.max();
        proceedResult(minDur, medDur, avgDur, maxDur);
    }

    private static void proceedResult(double min, double med, double avg, double max) {
        double[] apmLogDurs = new double[]{min, med, avg, max};
        assertArrayEquals(expected, apmLogDurs, 0);
    }
}
