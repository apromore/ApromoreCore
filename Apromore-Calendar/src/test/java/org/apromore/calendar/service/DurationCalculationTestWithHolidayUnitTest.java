/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
import java.time.ZoneId;
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
public class DurationCalculationTestWithHolidayUnitTest {

  CalendarModelBuilder calenderModelBuilder;
  OffsetDateTime startDateTime;
  OffsetDateTime endDateTime;
  Duration expected;
  

  @Before
  public void Setup() {
    calenderModelBuilder = new CalendarModelBuilder();
  }

 public DurationCalculationTestWithHolidayUnitTest(OffsetDateTime startDateTime,OffsetDateTime endDateTime,Duration expected)
 {
   this.startDateTime=startDateTime;
   this.endDateTime=endDateTime;
   this.expected=expected;
 }
  
 
 @Parameterized.Parameters
 public static Collection params() {
    return Arrays.asList(new Object[][] {
       { OffsetDateTime.of(2020, 10, 03, 07, 00, 00, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 10, 03, 15, 00, 00, 0, ZoneOffset.UTC), Duration.of(0, ChronoUnit.HOURS)},
       { OffsetDateTime.of(2020, 10, 02, 07, 00, 00, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 10, 06, 15, 00, 00, 0, ZoneOffset.UTC), Duration.of(24, ChronoUnit.HOURS)},
       { OffsetDateTime.of(2020, 10, 02, 07, 00, 00, 0, ZoneOffset.UTC), OffsetDateTime.of(2020, 10, 06, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(24, ChronoUnit.HOURS)},
       {  OffsetDateTime.of(2020, 10, 02, 12, 00, 00, 0, ZoneOffset.UTC),OffsetDateTime.of(2020, 10, 06, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(21, ChronoUnit.HOURS)},
       {  OffsetDateTime.of(2020, 10, 02, 17, 00, 00, 0, ZoneOffset.UTC),OffsetDateTime.of(2020, 10, 06, 18, 00, 00, 0, ZoneOffset.UTC), Duration.of(16, ChronoUnit.HOURS)},
    });
 }
 
 
  @Test
  public void testCalculateDuration8HoursDifferentDay() {
   
    CalendarModel calenderModel = calenderModelBuilder.with5DayWorking().withZoneId(ZoneId.systemDefault().toString()).build();

    // When
    DurationModel durationModel = calenderModel.getDuration(startDateTime, endDateTime);  
    
    // Then
    assertThat(durationModel.getDuration()).isEqualTo(expected);
  }
  
//    add test for midnight shift workers.
  


}
