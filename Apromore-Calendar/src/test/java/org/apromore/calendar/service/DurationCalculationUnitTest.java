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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DurationCalculationUnitTest {
    private CalendarModelBuilder calendarModelBuilder;

    @BeforeEach
    void setup() {
        calendarModelBuilder = new CalendarModelBuilder();
    }

    private static Stream<Arguments> params() {
        return Stream.of(
            // Span 2 work-day periods, fully contain them
            Arguments.of(
                OffsetDateTime.of(2019, 02, 01, 07, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2019, 02, 02, 20, 00, 00, 0, ZoneOffset.UTC), Duration.of(16, ChronoUnit.HOURS)
            ),

            // Span 3 work-day periods but not fully contain them
            Arguments.of(OffsetDateTime.of(2019, 02, 01, 12, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2019, 02, 03, 15, 00, 00, 0, ZoneOffset.UTC), Duration.of(19, ChronoUnit.HOURS)
            ),

            // Same day and completely contain a work-day period (9 to 5)
            Arguments.of(OffsetDateTime.of(2019, 02, 01, 07, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2019, 02, 01, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(8, ChronoUnit.HOURS)
            ),

            // Same day but overlap with a work-day period (9 to 5)
            Arguments.of(OffsetDateTime.of(2019, 02, 01, 12, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2019, 02, 01, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(5, ChronoUnit.HOURS)
            ),

            // Same day but outside a work-day period (9 to 5)
            Arguments.of(OffsetDateTime.of(2019, 02, 01, 18, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2019, 02, 01, 19, 00, 00, 0, ZoneOffset.UTC), Duration.of(0, ChronoUnit.HOURS)
            ),

            // Same day but outside a work-day period (9 to 5)
            Arguments.of(OffsetDateTime.of(2019, 02, 01, 3, 00, 00, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2019, 02, 01, 4, 00, 00, 0, ZoneOffset.UTC), Duration.of(0, ChronoUnit.HOURS)
            )
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void testCalculateDuration8HoursDifferentDay(
        OffsetDateTime startDateTime, OffsetDateTime endDateTime, Duration expected) {

        CalendarModel calendarModel = calendarModelBuilder.with7DayWorking().withZoneId(ZoneOffset.UTC.getId()).build();

        // When
        Duration durationModel = calendarModel.getDuration(startDateTime, endDateTime);

        // Then
        assertThat(durationModel).isEqualTo(expected);
    }


}
