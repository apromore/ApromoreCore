/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.apmlog.customcalendartests;

import org.apache.commons.lang3.tuple.Triple;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.histogram.GeneralHistogram;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

public class CusCalTest {

    public static void run(APMLog apmLog) throws Exception {

        List<Triple> triList = GeneralHistogram.getCaseUtilization(apmLog.getTraces(), 100, 20);
        Triple tri100 = triList.stream()
                .filter(x->Double.parseDouble(x.getRight().toString())==100d).findFirst().orElse(null);
        Triple tri25 = triList.stream()
                .filter(x->Double.parseDouble(x.getRight().toString())==25d).findFirst().orElse(null);

        assertEquals("1", tri100.getMiddle().toString());
        assertEquals("0", tri25.getMiddle().toString());

        ATrace trace100Util = apmLog.getTraces().stream()
                .filter(x -> LogStatsAnalyzer.getCaseUtilizationOf(x) >= 0.999).findFirst().orElse(null);

        assertNotNull(trace100Util);

        // Original durations
        assertEquals( 1.209599999E9, apmLog.get(0).getDuration(), 100);
        assertEquals(9.50399999E8, apmLog.get(1).getDuration(), 100);
        assertEquals(6.04799999E8, apmLog.get(0).getActivityInstances().get(0).getDuration(), 100);
        assertEquals(3.45599999E8, apmLog.get(1).getActivityInstances().get(0).getDuration(), 100);

        CalendarModelBuilder caleBuilder = new CalendarModelBuilder();
        CalendarModel calendarModel = caleBuilder.with5DayWorking().build();
        apmLog.setCalendarModel(calendarModel);

        trace100Util = apmLog.getTraces().stream()
                .filter(x -> LogStatsAnalyzer.getCaseUtilizationOf(x) >= 0.999).findFirst().orElse(null);

        assertNotNull(trace100Util);

        assertNotNull(apmLog.getCalendarModel());
        assertNotNull(apmLog.get(0).getCalendarModel());
        assertNotNull(apmLog.get(0).getActivityInstances().get(0).getCalendarModel());

        // Durations changed by CustomCalendar
        assertEquals( 2.88E8, apmLog.get(0).getDuration(), 100);
        assertEquals(2.01599999E8, apmLog.get(1).getDuration(), 100);
        assertEquals(1.44E8, apmLog.get(0).getActivityInstances().get(0).getDuration(), 100);
        assertEquals(5.7599999E7, apmLog.get(1).getActivityInstances().get(0).getDuration(), 100);

        CusCalPathFilterTest.testDirectFollowLessInterval01(apmLog);
        CusCalCaseDurationFilterTest.testCaseDurationFilter01(apmLog);

    }
}
