/**
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
package org.apromore.calendar.model;

import org.apromore.commons.datetime.DateTimeUtils;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Bruce Nguyen
 */
public class AbsoluteCalendarModelTest {
    @Test
    public void test_Duration_Zero() {
        long duration = CalendarModel.ABSOLUTE_CALENDAR.getDuration(
                DateTime.parse("2021-01-01T22:31:19.495+10:00").getMillis(),
                DateTime.parse("2021-01-01T22:31:19.495+10:00").getMillis()
        ).getDuration().toMillis();
        assertEquals(0, duration);
    }

    @Test
    public void test_Duration_Days() {
        long duration = CalendarModel.ABSOLUTE_CALENDAR.getDuration(
                DateTime.parse("2021-01-01T09:00:00.000+10:00").getMillis(),
                DateTime.parse("2021-01-02T09:00:00.000+10:00").getMillis()
        ).getDuration().toMillis();
        assertEquals(24*3600*1000, duration);
    }

    @Test
    public void test_Duration_Hours() {
        long duration = CalendarModel.ABSOLUTE_CALENDAR.getDuration(
                DateTime.parse("2021-01-01T09:00:00.000+10:00").getMillis(),
                DateTime.parse("2021-01-01T10:00:00.000+10:00").getMillis()
                ).getDuration().toMillis();
        assertEquals(1*3600*1000, duration);
    }

    @Test
    public void test_Duration_Minutes() {
        long duration = CalendarModel.ABSOLUTE_CALENDAR.getDuration(
                DateTime.parse("2021-01-01T09:00:00.000+10:00").getMillis(),
                DateTime.parse("2021-01-01T09:01:00.000+10:00").getMillis()
        ).getDuration().toMillis();
        assertEquals(60*1000, duration);
    }

    @Test
    public void test_Duration_Seconds() {
        long duration = CalendarModel.ABSOLUTE_CALENDAR.getDuration(
                DateTime.parse("2021-01-01T09:00:00.000+10:00").getMillis(),
                DateTime.parse("2021-01-01T09:00:01.000+10:00").getMillis()
                ).getDuration().toMillis();
        assertEquals(1000, duration);
    }

    @Test
    public void test_Duration_Milliseconds() {
        long duration = CalendarModel.ABSOLUTE_CALENDAR.getDuration(
                DateTime.parse("2021-01-01T09:00:00.000+10:00").getMillis(),
                DateTime.parse("2021-01-01T09:00:00.001+10:00").getMillis()
        ).getDuration().toMillis();
        assertEquals(1, duration);

    }

    @Test
    public void test_Duration_Years() {
        long duration = CalendarModel.ABSOLUTE_CALENDAR.getDuration(
                DateTime.parse("2020-01-01T09:00:00.000+10:00").getMillis(),
                DateTime.parse("2021-01-01T09:00:00.000+10:00").getMillis()
        ).getDuration().toMillis();
        assertEquals(366*24*3600*1000L, duration);
    }
}
