package org.apromore.calendar.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.DurationModel;
import org.apromore.calender.builder.CalendarModelBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DurationCalculationUnixTsUnitTest {

  CalendarModelBuilder calenderModelBuilder;
  long startDateTime;
  long endDateTime;
  long expected;


  @Before
  public void Setup() {
    calenderModelBuilder = new CalendarModelBuilder();
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

    CalendarModel calenderModel = calenderModelBuilder.with7DayWorking().withZoneId("UTC").build();

    // When
    DurationModel durationModel = calenderModel.getDuration(startDateTime, endDateTime);

    // Then
    assertThat(durationModel.getDuration().toMillis()).isEqualTo(expected);
  }



}
