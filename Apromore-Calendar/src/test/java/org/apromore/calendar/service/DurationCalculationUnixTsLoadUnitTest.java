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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.DurationModel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import lombok.Data;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.ignoreStubs;
// This is to test compute load with 100k records, 
//With 2 months difference the response comes in 860 milliseconds.

@Ignore("Ignored as this is a intensive test")
public class DurationCalculationUnixTsLoadUnitTest {

  CalendarModelBuilder calendarModelBuilder;

  @Before
  public void Setup() {
    calendarModelBuilder = new CalendarModelBuilder();
  }


  private  Container params() {
    
    long[][] data=new long[6][3];
    data[0]=new long[] {1549004400000l, 1554195600000l, 1728000000L};
    data[1]=new long[]  {1549004400000l, 1554195600000l, 1728000000L};
    data[2]=new long[] {1549022400000l, 1554195600000l, 1717200000L};
    data[3]=new long[] {1549040400000l, 1554195600000l, 1699200000L};
    data[4]=new long[] {1549022400000l, 1554195600000l, 1717200000L};
    data[5]=new long[] {1549022400000l, 1554195600000l, 1717200000L};
 
  Container container = new Container();
  List<Long> start = new ArrayList<Long>();
  List<Long> end = new ArrayList<Long>();
  List<Long> diff = new ArrayList<Long>();;

  IntStream.range(0,17000).forEach(i->
  {

    for (long[] o : data) {
      start.add(o[0]);
      end.add(o[1]);
      diff.add(o[2]);
    }

  });
  container.setStart(start);
  container.setEnd(end);
  container.setDiff(diff);
  
  return container;
  }


  @Test
  public void testCalculateDuration8HoursDifferentDay() {

    CalendarModel calendarModel = calendarModelBuilder.with7DayWorking().withZoneId("UTC").build();

    Container container=params();
    // When
    LocalDateTime start = LocalDateTime.now();
    System.out.println("Start="+start);
    
    Long[] durationModel = calendarModel.getDuration(container.getStart().toArray(new Long[container.getStart().size()]),
        container.getEnd().toArray(new Long[container.getStart().size()]));

    LocalDateTime end = LocalDateTime.now();
    System.out.println("End="+end);
    System.out.println("Diff="+Duration.between(start, end));
    System.out.println("size="+durationModel.length);
    // Then
    assertThat(Arrays.asList(durationModel)).isEqualTo(container.getDiff());
  }


  @Data
  class Container {

    List<Long> start;
    List<Long> end;
    List<Long> diff;
  }


}
