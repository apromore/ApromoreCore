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

package org.apromore.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.DurationModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class DurationCalculationUnixTsUnitTest {

    CalendarModelBuilder calendarModelBuilder;
    long startDateTime;
    long endDateTime;
    long expected;


    @Before
    public void setup() {
        calendarModelBuilder = new CalendarModelBuilder();
    }

    public DurationCalculationUnixTsUnitTest(long startDateTime, long endDateTime, long expected) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.expected = expected;
    }


    @Parameterized.Parameters
    public static Collection params() {
        return Arrays.asList(new Object[][] {
            {1549004400000L, 1549137600000L, 57600000L},
            {1549004400000L, 1549044000000L, 28800000L},
            {1549022400000L, 1549044000000L, 18000000L},
            {1549040400000L, 1549044000000L, 0L},
            {1549022400000L, 1549206000000L, 68400000L},
            {1549022400000L, 1549220400000L, 75600000L},
        });
    }


    @Test
    public void testCalculateDuration8HoursDifferentDay() {

        CalendarModel calendarModel = calendarModelBuilder.with7DayWorking().withZoneId(ZoneOffset.UTC.getId()).build();

        // When
        DurationModel durationModel = calendarModel.getDuration(startDateTime, endDateTime);

        // Then
        assertThat(durationModel.getDuration().toMillis()).isEqualTo(expected);
    }


}
