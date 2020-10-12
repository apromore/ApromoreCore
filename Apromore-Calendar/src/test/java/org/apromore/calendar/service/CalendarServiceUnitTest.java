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



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.service.CustomCalendarService;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.model.CustomCalendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CalendarServiceUnitTest {

  @Mock
  CustomCalendarRepository calendarRepository;


  @InjectMocks
  CustomCalendarService calendarService;

 
  @Test
  public void testCreateCalendar() throws CalendarAlreadyExistsException {
    // Given
    CustomCalendar calendar = new CustomCalendar("Test Desc");
    calendar.setId(1l);
    when(calendarRepository.findByName(calendar.getName())).thenReturn(null);
    when(calendarRepository.saveAndFlush(any(CustomCalendar.class))).thenReturn(calendar);

    // When
    Long id = calendarService.createGenericCalendar(calendar.getName(), true);

    // Then
    assertThat(id).isEqualTo(calendar.getId());
    verify(calendarRepository,times(1)).findByName(calendar.getName());
    verify(calendarRepository,times(1)).saveAndFlush(any(CustomCalendar.class));
    
  }


  @Test(expected = CalendarAlreadyExistsException.class)
  public void testCreateCalenderWithException() throws CalendarAlreadyExistsException {
    // Given
    CustomCalendar calendar = new CustomCalendar("Test Desc");
    calendar.setId(1l);
    when(calendarRepository.findByName(calendar.getName())).thenReturn(calendar);
  
    
    // When
    Long id = calendarService.createGenericCalendar(calendar.getName(), true);

    // Then
//    exception thrown
    
  }

}
