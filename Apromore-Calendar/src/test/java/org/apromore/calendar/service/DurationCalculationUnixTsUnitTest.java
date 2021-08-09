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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;

import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.DurationModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DurationCalculationUnixTsUnitTest {

  CalendarModelBuilder calendarModelBuilder;
  long startDateTime;
  long endDateTime;
  long expected;


  @Before
  public void Setup() {
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
        {1549004400000l, 1549137600000l, 57600000l},
        {1549004400000l, 1549044000000l, 28800000L},
        {1549022400000l, 1549044000000l, 18000000L},
        {1549040400000l, 1549044000000l, 0l},
        {1549022400000l, 1549206000000l, 68400000L},
        {1549022400000l, 1549220400000l, 75600000L},
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
