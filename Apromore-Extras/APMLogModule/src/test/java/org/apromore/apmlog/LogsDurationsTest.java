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
import org.apromore.apmlog.immutable.ImmutableLog;
import org.deckfour.xes.model.XLog;

import static org.junit.Assert.assertArrayEquals;

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

    public static void testPLogDurations(APMLog apmLog) {
        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        PLog pLog = apmLogFilter.getPLog();
        proceedPLogDurTest(pLog);

        // test converted version
        APMLog converted = pLog.toAPMLog();
        proceedAPMLogDurTest(converted);

        // test updated version
        pLog.updateStats();
        proceedPLogDurTest(pLog);
    }

    public static void testImmutableLogDurations(XLog xLog) {
        ImmutableLog immutableLog = (ImmutableLog) LogFactory.convertXLog(xLog);
        proceedImmutableLogDurTest(immutableLog);

        immutableLog.updateStats(); // test after-updated values
        proceedImmutableLogDurTest(immutableLog);
    }

    public static void testClonedImmutableLogDurations(XLog xLog) {
        ImmutableLog immutableLog = (ImmutableLog) LogFactory.convertXLog(xLog);

        ImmutableLog clone = (ImmutableLog) immutableLog.clone();
        proceedImmutableLogDurTest(clone);

        clone.updateStats(); // test after-updated values
        proceedImmutableLogDurTest(clone);
    }

    private static void proceedAPMLogDurTest(APMLog apmLog) {
        double minDur = apmLog.getMinDuration();
        double medDur = apmLog.getMedianDuration();
        double avgDur = apmLog.getAverageDuration();
        double maxDur = apmLog.getMaxDuration();
        proceedResult(minDur, medDur, avgDur, maxDur);
    }

    private static void proceedImmutableLogDurTest(ImmutableLog immutableLog) {
        double minDur = immutableLog.getMinDuration();
        double medDur = immutableLog.getMedianDuration();
        double avgDur = immutableLog.getAverageDuration();
        double maxDur = immutableLog.getMaxDuration();
        proceedResult(minDur, medDur, avgDur, maxDur);
    }

    private static void proceedPLogDurTest(PLog pLog) {
        double minDur = pLog.getMinDuration();
        double medDur = pLog.getMedianDuration();
        double avgDur = pLog.getAverageDuration();
        double maxDur = pLog.getMaxDuration();
        proceedResult(minDur, medDur, avgDur, maxDur);
    }

    private static void proceedResult(double min, double med, double avg, double max) {
        double[] apmLogDurs = new double[]{min, med, avg, max};
        assertArrayEquals(expected, apmLogDurs, 0);
    }
}
