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



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.commons.mapper.CustomMapper;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.model.CustomCalendar;
import org.apromore.dao.model.Holiday;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CalendarServiceUnitTest {

  @Mock
  CustomCalendarRepository calendarRepository;


  @Spy
  CustomMapper mapper;

  @InjectMocks
  CustomCalendarService calendarService;

  @Before
  public void Before() {
    List<String> mapperList = Arrays.asList("mappers/calendar.xml");
    mapper = new CustomMapper(mapperList);
    mapper.init();
    calendarService.setModelMapper(mapper);
  }

  @Test
  public void testCreateCalendar() throws CalendarAlreadyExistsException {
    // Given

    CustomCalendar calendar = new CustomCalendar("Test Desc", ZoneId.of("UTC"));
    calendar.setId(1l);
    when(calendarRepository.findByName(calendar.getName())).thenReturn(null);
    when(calendarRepository.saveAndFlush(any(CustomCalendar.class))).thenReturn(calendar);

    // When
    CalendarModel calendarSaved = calendarService.createGenericCalendar(calendar.getName(), true,
        ZoneId.systemDefault().toString());

    // Then
    assertThat(calendarSaved.getId()).isEqualTo(calendar.getId());
    verify(calendarRepository, times(1)).findByName(calendar.getName());
    verify(calendarRepository, times(1)).saveAndFlush(any(CustomCalendar.class));

  }


  @Test(expected = CalendarAlreadyExistsException.class)
  public void testCreateCalendarWithException() throws CalendarAlreadyExistsException {
    // Given
    CustomCalendar calendar = new CustomCalendar("Test Desc");
    calendar.setId(1l);
    when(calendarRepository.findByName(calendar.getName())).thenReturn(calendar);


    // When
    CalendarModel calendarSaved = calendarService.createGenericCalendar(calendar.getName(), true,
        ZoneId.systemDefault().toString());

    // Then
    // exception thrown

  }


  @Test
  public void testCreateCalendarWithHoliday() throws CalendarAlreadyExistsException {
    // Given
    CustomCalendar calendar = new CustomCalendar("Test Desc", ZoneId.of("UTC"));
    calendar.setId(1l);
    Holiday holiday = new Holiday("test", "test holiday", LocalDate.of(2020, 02, 02));
    calendar.setHolidays(Arrays.asList(holiday));

    when(calendarRepository.findByName(calendar.getName())).thenReturn(null);
    when(calendarRepository.saveAndFlush(any(CustomCalendar.class))).thenReturn(calendar);

    // When
    CalendarModel calendarSaved = calendarService.createGenericCalendar(calendar.getName(), true,
        ZoneId.systemDefault().toString());

    // Then
    assertThat(calendarSaved.getId()).isEqualTo(calendar.getId());
    assertThat(calendarSaved.getHolidays()).hasSize(1);
    assertThat(calendarSaved.getHolidays().get(0).getDescription())
        .isEqualTo(holiday.getDescription());
    verify(calendarRepository, times(1)).findByName(calendar.getName());
    verify(calendarRepository, times(1)).saveAndFlush(any(CustomCalendar.class));

  }

  @Test
  public void testCreateCalendarWithDuplicateHoliday() throws CalendarAlreadyExistsException {
    // Given
    CustomCalendar calendar = new CustomCalendar("Test Desc1", ZoneId.of("UTC"));
    calendar.setId(1l);
    Holiday holiday = new Holiday("test", "test holiday", LocalDate.of(2020, 02, 02));
    Holiday holiday1 = new Holiday("test1", "test holiday1", LocalDate.of(2020, 02, 02));
    calendar.setHolidays(Arrays.asList(holiday, holiday));

    when(calendarRepository.findByName(calendar.getName())).thenReturn(null);
    when(calendarRepository.saveAndFlush(any(CustomCalendar.class))).thenReturn(calendar);

    // When
    CalendarModel calendarSaved = calendarService.createGenericCalendar(calendar.getName(), true,
        ZoneId.systemDefault().toString());
    calendarSaved.populateHolidayMap();

    // Then
    assertThat(calendarSaved.getId()).isEqualTo(calendar.getId());
    assertThat(calendarSaved.getHolidays()).hasSize(2);
    assertThat(calendarSaved.getHolidayLocalDateMap()).hasSize(1);

    verify(calendarRepository, times(1)).findByName(calendar.getName());
    verify(calendarRepository, times(1)).saveAndFlush(any(CustomCalendar.class));

  }


}
