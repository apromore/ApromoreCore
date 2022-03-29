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
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.calendar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Duration calculation tests with different timezones.
 * @author Bruce Nguyen
 */

// This test is temporarily disabled to investigate the issue of timezone
@Disabled
class DurationCalculationTestWithSpecialTimezonesUnitTest {

    CalendarModelBuilder calendarModelBuilder;

    @BeforeEach
    void setup() {
        calendarModelBuilder = new CalendarModelBuilder();
    }

    private static Stream<Arguments> datesWithDaylightSavings() {
        return Stream.of(
            // Include the ending time of Daylight Saving in Victoria: on 5 April 2020 at 3am the clock was turned to
            // 2am The expected duration is actually 1 hour longer if Daylight Saving is taken into account
            Arguments.of(
                OffsetDateTime.of(2020, 04, 04, 9, 00, 00, 0, ZoneOffset.of("+11:00")),
                OffsetDateTime.of(2020, 04, 05, 9, 00, 00, 0, ZoneOffset.of("+11:00")),
                Duration.of(25, ChronoUnit.HOURS)
            ),
            // This interval doesn't include the ending time of Daylight Saving
            // So, expected duration is the same as the clock data
            Arguments.of(
                OffsetDateTime.of(2020, 04, 06, 9, 00, 00, 0, ZoneOffset.of("+11:00")),
                OffsetDateTime.of(2020, 04, 07, 9, 00, 00, 0, ZoneOffset.of("+11:00")),
                Duration.of(24, ChronoUnit.HOURS)
            ),
            // Include the start time of Daylight Saving in Victoria: on 4 Oct 2020 at 2am the clock was turned to 3am
            // The expected duration is actually 1 hour shorter if Daylight Saving is taken into account
            Arguments.of(
                OffsetDateTime.of(2020, 10, 03, 9, 00, 00, 0, ZoneOffset.of("+11:00")),
                OffsetDateTime.of(2020, 10, 04, 9, 00, 00, 0, ZoneOffset.of("+11:00")),
                Duration.of(23, ChronoUnit.HOURS)
            ),
            // This interval doesn't include the ending time of Daylight Saving
            // So, expected duration is the same as the clock data
            Arguments.of(
                OffsetDateTime.of(2020, 10, 05, 9, 00, 00, 0, ZoneOffset.of("+11:00")),
                OffsetDateTime.of(2020, 10, 06, 9, 00, 00, 0, ZoneOffset.of("+11:00")),
                Duration.of(24, ChronoUnit.HOURS)
            )
        );
    }


    @ParameterizedTest
    @MethodSource("datesWithDaylightSavings")
    void testCalculateDurationWithDaylightSaving(OffsetDateTime startDateTime, OffsetDateTime endDateTime,
                                                        Duration expected) {

        CalendarModel calendarModel = calendarModelBuilder.withAllDayAllTime()
            .withZoneId("+11:00")
            .build();

        // When
        long duration = calendarModel.getDurationMillis(startDateTime.toInstant(), endDateTime.toInstant());

        // Then
        assertEquals(expected.toMillis(), duration);
    }
}
