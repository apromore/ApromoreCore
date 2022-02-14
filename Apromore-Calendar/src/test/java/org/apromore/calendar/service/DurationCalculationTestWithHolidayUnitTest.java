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

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.HolidayType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class DurationCalculationTestWithHolidayUnitTest {

    CalendarModelBuilder calendarModelBuilder;
    OffsetDateTime startDateTime;
    OffsetDateTime endDateTime;
    Duration expected;


    @Before
    public void setup() {
        calendarModelBuilder = new CalendarModelBuilder();
    }

    public DurationCalculationTestWithHolidayUnitTest(OffsetDateTime startDateTime, OffsetDateTime endDateTime,
                                                      Duration expected) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.expected = expected;
    }


    @Parameterized.Parameters
    public static Collection params() {
        return Arrays.asList(new Object[][] {
            // Within holiday 26/1
            {OffsetDateTime.of(2021, 01, 26, 9, 00, 59, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2021, 01, 26, 10, 00, 00, 0, ZoneOffset.UTC),
                Duration.of(0, ChronoUnit.HOURS)
            },
            // Overlapping holiday 26/1
            {
                OffsetDateTime.of(2021, 01, 25, 23, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2021, 01, 26, 9, 00, 00, 0, ZoneOffset.UTC),
                Duration.of(1, ChronoUnit.HOURS)
            },
            // Containing holiday 26/1
            {
                OffsetDateTime.of(2021, 01, 25, 23, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2021, 01, 27, 01, 00, 00, 0, ZoneOffset.UTC),
                Duration.of(2, ChronoUnit.HOURS)
            },
            // Outside holiday 26/1
            {
                OffsetDateTime.of(2021, 01, 25, 9, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2021, 01, 25, 10, 00, 00, 0, ZoneOffset.UTC),
                Duration.of(1, ChronoUnit.HOURS)},
        });
    }


    @Test
    public void testCalculateDurationWithHoliday() {

        CalendarModel calendarModel = calendarModelBuilder.withAllDayAllTime()
            .withZoneId(ZoneOffset.UTC.getId())
            .withHoliday(HolidayType.PUBLIC, "Anzac", "Anzac",
                LocalDate.of(2021, 01, 26))
            .build();

        // When
        long duration = calendarModel.getDurationMillis(startDateTime.toInstant(), endDateTime.toInstant());

        // Then
        Assert.assertEquals(expected.toMillis(), duration);
    }


}
