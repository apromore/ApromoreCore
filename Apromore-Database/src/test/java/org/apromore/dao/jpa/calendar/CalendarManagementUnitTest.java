/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.dao.jpa.calendar;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import org.apromore.config.BaseTestClass;
import org.apromore.dao.CustomCalendarInfoRepository;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.model.CustomCalendar;
import org.apromore.dao.model.WorkDay;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CalendarManagementUnitTest extends BaseTestClass {

	@Autowired
	CustomCalendarRepository customCal;
	
	@Autowired
	CustomCalendarInfoRepository customCalInfo;


	@Test
	void createCustomCalendar() {
//		Given
		CustomCalendar calendar = new CustomCalendar("Test Calendar",ZoneId.of("UTC"));

//		When
		calendar = customCal.saveAndFlush(calendar);

//		Then
		assertThat(calendar.getId()).isNotNull();
	}

	@Test
	void testGetCusomCalendarByDesc() {

//		Given
		CustomCalendar calendarToSave = new CustomCalendar("Test Calendar Desc");
		customCal.saveAndFlush(calendarToSave);

//		When
		CustomCalendar calendarExpected = customCal.findByName("Test Calendar Desc");

//		Then
		assertThat(calendarExpected.getId()).isNotNull();
		assertThat(calendarExpected.getCreated()).startsWith(calendarToSave.getCreated().subSequence(0, 15));
		assertThat(calendarExpected.getUpdated()).startsWith(calendarToSave.getUpdated().subSequence(0, 15));
		assertThat(calendarExpected.getCreateOffsetDateTime()).isNotNull();
		assertThat(calendarExpected.getUpdateOffsetDateTime()).isNotNull();

	}

	@Test
	void testAddCustomCalendarWithWorkDayMonday() {
//		Given
		CustomCalendar calendarToSave = new CustomCalendar("Test Calendar Work Day");
//		customCal.saveAndFlush(calenderToSave);

		LocalTime startTime = LocalTime.of(9, 0, 0, 0);
		LocalTime endTime = LocalTime.of(5, 0, 0, 0);
		WorkDay workDayMonDay = new WorkDay(DayOfWeek.MONDAY, startTime, endTime, true);
		calendarToSave.addWorkDay(workDayMonDay);

//		When
		CustomCalendar calendarExpected = customCal.saveAndFlush(calendarToSave);

//		Then
		assertThat(calendarExpected.getId()).isNotNull();
		assertThat(calendarExpected.getWorkDays().get(0).getId()).isNotNull();
		assertThat(calendarExpected.getWorkDays().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);

	}

	@Test
	void testAddCustomCalendarWithNonWorkDaySaturday() {
//		Given
		CustomCalendar calendarToSave = new CustomCalendar("Test Calender Non Work Day");
//		customCal.saveAndFlush(calenderToSave);

		LocalTime startTime = LocalTime.of(9, 0, 0, 0);
		LocalTime endTime = LocalTime.of(5, 0, 0, 0);
		WorkDay workDayMonDay = new WorkDay(DayOfWeek.SATURDAY, startTime, endTime, false);
		calendarToSave.addWorkDay(workDayMonDay);

//		When
		CustomCalendar calendarExpected = customCal.saveAndFlush(calendarToSave);

//		Then
		assertThat(calendarExpected.getId()).isNotNull();
		assertThat(calendarExpected.getWorkDays().get(0).getId()).isNotNull();
		assertThat(calendarExpected.getWorkDays().get(0).getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);
		assertThat(calendarExpected.getWorkDays().get(0).isWorkingDay()).isFalse();

	}

	@Test
	void testAddCustomCalendarWithBusinessDays() {
//		Given
		CustomCalendar calendarToSave = new CustomCalendar("Test Calendar Business Calendar");
//		customCal.saveAndFlush(calenderToSave);

		LocalTime startTime = LocalTime.of(9, 0, 0, 0);
		LocalTime endTime = LocalTime.of(5, 0, 0, 0);
		for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
			boolean isWorkDay = true;
			if (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY)) {
				isWorkDay = false;
			}
			WorkDay workDayMonDay = new WorkDay(dayOfWeek, startTime, endTime, isWorkDay);

			calendarToSave.addWorkDay(workDayMonDay);
		}

//		When
		CustomCalendar calendarExpected = customCal.saveAndFlush(calendarToSave);

//		Then
		assertThat(calendarExpected.getId()).isNotNull();
		
		  
		assertThat(calendarExpected.getWorkDays()).hasSize(7);

		Condition<WorkDay> workDaysFilter = new Condition<WorkDay>() {
			@Override
			public boolean matches(WorkDay workDay) {
				return workDay.getDayOfWeek().equals(DayOfWeek.SATURDAY)
						|| workDay.getDayOfWeek().equals(DayOfWeek.SUNDAY);
			}
		};

//		Need assert for holiday check, after lambda function work.
	}
	
	
	

}
