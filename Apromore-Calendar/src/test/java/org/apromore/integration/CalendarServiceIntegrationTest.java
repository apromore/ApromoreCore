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
package org.apromore.integration;



import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.exception.CalendarNotExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.HolidayModel;
import org.apromore.calendar.service.CustomCalendarService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class CalendarServiceIntegrationTest extends BaseTestClass {
  
  @Autowired
  CustomCalendarService calendarService;

  @Test
  public void testCreateCalendar() throws CalendarAlreadyExistsException {
    
//    when
    CalendarModel model=calendarService.createGenericCalendar("Generic", true,ZoneId.systemDefault().toString());
    
 // Then
    assertThat(model.getId()).isNotNull();
    assertThat(model.getWorkDays()).hasSize(7);   
    assertThat(model.getWorkDays().get(5).getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);   
    assertThat(model.getWorkDays().get(6).getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY); 
    assertThat(model.getWorkDays().get(5).getStartTime()).isEqualTo(OffsetTime.of(LocalTime.MIN, ZoneId.systemDefault().getRules().getOffset(Instant.now())));   
    assertThat(model.getWorkDays().get(6).getEndTime()).isEqualTo(OffsetTime.of(LocalTime.MAX, ZoneId.systemDefault().getRules().getOffset(Instant.now())));

  }
  
  
  @Test
  public void testGetCalendar() throws CalendarAlreadyExistsException {
//    Given
    CalendarModel model=calendarService.createGenericCalendar(UUID.randomUUID().toString(), true,ZoneId.systemDefault().toString());
//    when
    CalendarModel modelExpected=calendarService.getCalendar(model.getId());
    
 // Then
    assertThat(modelExpected.getId()).isNotNull();
    assertThat(modelExpected.getName()).isEqualTo(model.getName());
    assertThat(modelExpected.getWorkDays()).hasSize(7);   
    
  }
  
  
  @Test
  public void testGetCalendarWithCustomHoliday() throws CalendarAlreadyExistsException, CalendarNotExistsException {
//    Given
    CalendarModel model=calendarService.createGenericCalendar(UUID.randomUUID().toString(), true,ZoneId.systemDefault().toString());
 
    HolidayModel holiday = new HolidayModel("CUSTOM","Test Holiday", "Test Holiday Desc", LocalDate.of(2020, 01, 01));
    calendarService.updateHoliday(model.getId(),Arrays.asList(holiday));
   
//    when
    CalendarModel modelExpected=calendarService.getCalendar(model.getId());
    
 // Then
    assertThat(modelExpected.getId()).isNotNull();
    assertThat(modelExpected.getName()).isEqualTo(model.getName());
    assertThat(modelExpected.getWorkDays()).hasSize(7);   
    assertThat(modelExpected.getHolidays()).hasSize(1);   
    assertThat(modelExpected.getHolidays().get(0).getDescription()).isEqualTo(holiday.getDescription());   
    
  }
  
  @Test
  public void testGetCalendarWithCustomHolidayRemoved() throws CalendarAlreadyExistsException, CalendarNotExistsException {
//    Given
    CalendarModel model=calendarService.createGenericCalendar(UUID.randomUUID().toString(), true,ZoneId.systemDefault().toString());
    HolidayModel holiday1 = new HolidayModel("CUSTOM","Test Holiday1", "Test Holiday Desc1", LocalDate.of(2020, 01, 01));
    HolidayModel holiday2 = new HolidayModel("CUSTOM","Test Holiday2", "Test Holiday Desc2", LocalDate.of(2020, 01, 02));
    HolidayModel holiday3 = new HolidayModel("CUSTOM","Test Holiday3", "Test Holiday Desc3", LocalDate.of(2020, 01, 03));
    calendarService.updateHoliday(model.getId(),Arrays.asList(holiday1,holiday2,holiday3));
   
//    when
    CalendarModel modelExpected=calendarService.getCalendar(model.getId());
    
 // Then
    assertThat(modelExpected.getId()).isNotNull();
    assertThat(modelExpected.getName()).isEqualTo(model.getName());
    assertThat(modelExpected.getWorkDays()).hasSize(7);   
    assertThat(modelExpected.getHolidays()).hasSize(3);   
  
//    When
    List<Long> holidayIdList = Arrays.asList(modelExpected.getHolidays().get(0).getId(),
        modelExpected.getHolidays().get(1).getId());
    String leftHolidayDesc=modelExpected.getHolidays().get(2).getDescription();
    calendarService.removeHoliday(model.getId(), holidayIdList);
    modelExpected=calendarService.getCalendar(model.getId());
    
//    Then
    
    assertThat(modelExpected.getId()).isNotNull();
    assertThat(modelExpected.getName()).isEqualTo(model.getName());
    assertThat(modelExpected.getWorkDays()).hasSize(7);   
    assertThat(modelExpected.getHolidays()).hasSize(1);
    assertThat(modelExpected.getHolidays().get(0).getDescription()).isEqualTo(leftHolidayDesc);  
  }
  

  @Test
  public void testGetCalendarWithCustomHolidays() throws CalendarAlreadyExistsException, CalendarNotExistsException {
//    Given
      CalendarModel model = calendarService.createGenericCalendar(UUID.randomUUID().toString(), true,
              ZoneId.systemDefault().toString());
      HolidayModel holiday1 = new HolidayModel("CUSTOM", "Test Holiday1", "Test Holiday Desc1",
              LocalDate.of(2020, 01, 01));
      HolidayModel holiday2 = new HolidayModel("CUSTOM", "Test Holiday2", "Test Holiday Desc2",
              LocalDate.of(2020, 01, 02));
      HolidayModel holiday3 = new HolidayModel("CUSTOM", "Test Holiday3", "Test Holiday Desc3",
              LocalDate.of(2020, 01, 03));
      calendarService.updateHoliday(model.getId(), Arrays.asList(holiday1, holiday2, holiday3));

//    when
      List<CalendarModel> modelExpected = calendarService.getCalendars();

      System.out.println(modelExpected);

  }

}
