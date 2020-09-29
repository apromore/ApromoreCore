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

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.util.CalendarUtil;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.model.CustomCalendar;
import org.apromore.dao.model.WorkDay;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.Data;

@Data
public class CustomCalendarService {
//  spelling of calendar
    @Autowired
    public CustomCalendarRepository calendarRepo;

    public Long createGenericCalendar(String description, boolean weekendsOff)
            throws CalendarAlreadyExistsException {

        OffsetTime startTime = OffsetTime.of(LocalTime.MIN, ZoneOffset.UTC);
        OffsetTime endTime = OffsetTime.of(LocalTime.MAX, ZoneOffset.UTC);
        return createCalendar(description, weekendsOff, startTime, endTime);
    }

    public Long createBusinessCalendar(String description, boolean weekendsOff)
            throws CalendarAlreadyExistsException {
        OffsetTime startTime = OffsetTime.of(LocalTime.of(9, 0), ZoneOffset.UTC);
        OffsetTime endTime = OffsetTime.of(LocalTime.of(5, 0), ZoneOffset.UTC);
        return createCalendar(description, weekendsOff, startTime, endTime);

    }

    private Long createCalendar(String description, boolean weekendsOff, OffsetTime start,
            OffsetTime end)
            throws CalendarAlreadyExistsException {

        validateCalenderExists(calendarRepo.findByName(description));
        
        final CustomCalendar calendar = new CustomCalendar(description);
        for (WorkDay workDay : getWorkDays(start, end, weekendsOff)) {
            calendar.addWorkDay(workDay);
        }
        CustomCalendar newcalender = calendarRepo.saveAndFlush(calendar);
        return newcalender.getId();

    }

    private List<WorkDay> getWorkDays(OffsetTime start, OffsetTime end, boolean weekendOff) {

        Predicate<DayOfWeek> isWeekendOff = CalendarUtil.getWeekendOffPRedicate(weekendOff);
        List<WorkDay> workDaysList = new ArrayList<WorkDay>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            workDaysList.add(new WorkDay(dayOfWeek, start, end, isWeekendOff.test(dayOfWeek)));
        }
        return workDaysList;

    }

    public CustomCalendar getCalender(Long id) {

        return calendarRepo.findById(id);

    }

    private void validateCalenderExists(CustomCalendar calendar)
            throws CalendarAlreadyExistsException {

        if (calendar != null) {
            throw new CalendarAlreadyExistsException("Calendar already exists");
        }

    }

}
