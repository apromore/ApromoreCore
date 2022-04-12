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

package org.apromore.calendar.service;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DurationCalculationUnixTsUnitTest {

    CalendarModelBuilder calendarModelBuilder;

    @BeforeEach
    void setup() {
        calendarModelBuilder = new CalendarModelBuilder();
    }

    private static Stream<Arguments> params() {
        return Stream.of(
            Arguments.of(1549004400000L, 1549137600000L, 57600000L),
            Arguments.of(1549004400000L, 1549044000000L, 28800000L),
            Arguments.of(1549022400000L, 1549044000000L, 18000000L),
            Arguments.of(1549040400000L, 1549044000000L, 0L),
            Arguments.of(1549022400000L, 1549206000000L, 68400000L),
            Arguments.of(1549022400000L, 1549220400000L, 75600000L)
        );
    }


    @ParameterizedTest
    @MethodSource("params")
    void testCalculateDuration8HoursDifferentDay(long startDateTime, long endDateTime, long expected) {

        CalendarModel calendarModel = calendarModelBuilder.with7DayWorking().withZoneId(ZoneOffset.UTC.getId()).build();

        // When
        Duration durationModel = calendarModel.getDuration(startDateTime, endDateTime);

        // Then
        Assertions.assertEquals(expected, durationModel.toMillis());
    }


}
