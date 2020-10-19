package org.apromore.calendar.service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.apromore.calendar.model.CalenderModel;
import org.apromore.calendar.model.DurationModel;
import org.apromore.calender.builder.CalenderModelBuilder;
import org.junit.Before;
import org.junit.Test;
import liquibase.sdk.verifytest.TestPermutation.Setup;

public class DurationCalculationUnitTest {

  CalenderModelBuilder calenderModelBuilder;

  @Before
  public void Setup() {
    calenderModelBuilder = new CalenderModelBuilder();
  }

  @Test
  public void testCalculateDuration() {
    // Given
    OffsetDateTime startDateTime = OffsetDateTime.of(2019, 02, 01, 07, 00, 00, 0, ZoneOffset.UTC);
    OffsetDateTime endDateTime = OffsetDateTime.of(2019, 02, 02, 20, 00, 00, 0, ZoneOffset.UTC);

    CalenderModel calenderModel = calenderModelBuilder.with7DayWorking().build();

    // When
    DurationModel durationModel = calenderModel.getDuration(startDateTime, endDateTime);
    
    
    // Then
    System.out.println(durationModel);
  }

}
