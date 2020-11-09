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
public class DurationCalculationUnitTestWithHoliday {

  CalendarModelBuilder calenderModelBuilder;
  OffsetDateTime startDateTime;
  OffsetDateTime endDateTime;
  Duration expected;
  

  @Before
  public void Setup() {
    calenderModelBuilder = new CalendarModelBuilder();
  }

 public DurationCalculationUnitTestWithHoliday(OffsetDateTime startDateTime,OffsetDateTime endDateTime,Duration expected)
 {
   this.startDateTime=startDateTime;
   this.endDateTime=endDateTime;
   this.expected=expected;
 }
  
 
 @Parameterized.Parameters
 public static Collection params() {
    return Arrays.asList(new Object[][] {
       { OffsetDateTime.of(2020, 10, 03, 07, 00, 00, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 10, 03, 15, 00, 00, 0, ZoneOffset.UTC), Duration.of(0, ChronoUnit.HOURS)},
       { OffsetDateTime.of(2020, 10, 02, 07, 00, 00, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 10, 06, 15, 00, 00, 0, ZoneOffset.UTC), Duration.of(22, ChronoUnit.HOURS)},
       { OffsetDateTime.of(2020, 10, 02, 07, 00, 00, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 10, 06, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(24, ChronoUnit.HOURS)},
       {  OffsetDateTime.of(2020, 10, 02, 12, 00, 00, 0, ZoneOffset.UTC),OffsetDateTime.of(2020, 10, 06, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(21, ChronoUnit.HOURS)},
       {  OffsetDateTime.of(2020, 10, 02, 17, 00, 00, 0, ZoneOffset.UTC),OffsetDateTime.of(2020, 10, 06, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(16, ChronoUnit.HOURS)},
    });
 }
 
 
  @Test
  public void testCalculateDuration8HoursDifferentDay() {
   
    CalendarModel calenderModel = calenderModelBuilder.with5DayWorking().build();

    // When
    DurationModel durationModel = calenderModel.getDuration(startDateTime, endDateTime);  
    
    // Then
    assertThat(durationModel.getDuration()).isEqualTo(expected);
  }
  
//    add test for midnight shift workers.
  


}
