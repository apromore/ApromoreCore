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
public class DurationCalculationUnitTest {

  CalendarModelBuilder calenderModelBuilder;
  OffsetDateTime startDateTime;
  OffsetDateTime endDateTime;
  Duration expected;
  

  @Before
  public void Setup() {
    calenderModelBuilder = new CalendarModelBuilder();
  }

 public DurationCalculationUnitTest(OffsetDateTime startDateTime,OffsetDateTime endDateTime,Duration expected)
 {
   this.startDateTime=startDateTime;
   this.endDateTime=endDateTime;
   this.expected=expected;
 }
  
 
 @Parameterized.Parameters
 public static Collection params() {
    return Arrays.asList(new Object[][] {
       { OffsetDateTime.of(2019, 02, 01, 07, 00, 00, 0, ZoneOffset.UTC), OffsetDateTime.of(2019, 02, 02, 20, 00, 00, 0, ZoneOffset.UTC), Duration.of(16, ChronoUnit.HOURS)},
       { OffsetDateTime.of(2019, 02, 01, 07, 00, 00, 0, ZoneOffset.UTC), OffsetDateTime.of(2019, 02, 01, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(8, ChronoUnit.HOURS)},
       {  OffsetDateTime.of(2019, 02, 01, 12, 00, 00, 0, ZoneOffset.UTC),OffsetDateTime.of(2019, 02, 01, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(5, ChronoUnit.HOURS)},
       {  OffsetDateTime.of(2019, 02, 01, 17, 00, 00, 0, ZoneOffset.UTC),OffsetDateTime.of(2019, 02, 01, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(0, ChronoUnit.HOURS)},
       {  OffsetDateTime.of(2019, 02, 01, 12, 00, 00, 0, ZoneOffset.UTC),OffsetDateTime.of(2019, 02, 03, 15, 00, 00, 0, ZoneOffset.UTC), Duration.of(19, ChronoUnit.HOURS)},
       {  OffsetDateTime.of(2019, 02, 01, 12, 00, 00, 0, ZoneOffset.UTC),OffsetDateTime.of(2019, 02, 03, 19, 00, 00, 0, ZoneOffset.UTC), Duration.of(21, ChronoUnit.HOURS)}
    });
 }
 
 
  @Test
  public void testCalculateDuration8HoursDifferentDay() {
   
    CalendarModel calenderModel = calenderModelBuilder.with7DayWorking().build();

    // When
    DurationModel durationModel = calenderModel.getDuration(startDateTime, endDateTime);  
    
    // Then
    assertThat(durationModel.getDuration()).isEqualTo(expected);
  }
  


}
