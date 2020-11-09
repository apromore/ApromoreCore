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
package org.apromore.integration;



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CustomCalendarService;
import org.apromore.dao.model.CustomCalendar;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class CalendarServiceIntegrationTest extends BaseTestClass {
  
  @Autowired
  CustomCalendarService calenderService;

  @Test
  public void testCreateCalender() throws CalendarAlreadyExistsException {
    
//    when
    CalendarModel model=calenderService.createGenericCalendar("Generic", true);
    
 // Then
    assertThat(model.getId()).isNotNull();
    assertThat(model.getWorkDays()).hasSize(7);   
    assertThat(model.getWorkDays().get(5).getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);   
    assertThat(model.getWorkDays().get(6).getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY); 
    assertThat(model.getWorkDays().get(5).getStartTime()).isEqualTo(OffsetTime.of(LocalTime.MIN, ZoneOffset.UTC));   
    assertThat(model.getWorkDays().get(6).getEndTime()).isEqualTo(OffsetTime.of(LocalTime.MAX, ZoneOffset.UTC));

  }
  
  
  @Test
  public void testGetCalender() throws CalendarAlreadyExistsException {
//    Given
    CalendarModel model=calenderService.createGenericCalendar(UUID.randomUUID().toString(), true);
//    when
    CalendarModel modelExpected=calenderService.getCalenderById(model.getId());
    
 // Then
    assertThat(modelExpected.getId()).isNotNull();
    assertThat(modelExpected.getName()).isEqualTo(model.getName());
    assertThat(modelExpected.getWorkDays()).hasSize(7);   
    
  }

}
