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

package org.apromore.calendar.builder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.Calendars;
import org.apromore.calendar.model.HolidayModel;
import org.apromore.calendar.model.HolidayType;
import org.apromore.calendar.model.WorkDayModel;

/**
 * This class is a Builder for CalendarModel with a fluent-style API.
 * @author Nolan Tellis
 * @author Bruce Nguyen
 */
public class CalendarModelBuilder {

    CalendarModel model = Calendars.INSTANCE.emptyCalendar();

    public CalendarModel build() {

        return model;
    }

    public CalendarModelBuilder withWorkDay(DayOfWeek dayOfWeek, LocalTime startTime,
                                            LocalTime endTime, boolean isWorkingDay) {
        model.getWorkDays().add(new WorkDayModel(dayOfWeek, startTime, endTime, isWorkingDay));
        return this;
    }

    /**
     * Because of the ending time setting to 23:59:59:999999999, duration calculation based on this calendar will have a
     * nanosecond imprecision. When comparing a Duration value with a constant, it must be rounded up to a precision
     * level to be compared, e.g. rounded up to SECONDS or MILLISECONDS.
     *
     * @return A CalendarModelBuilder with all days and times selected.
     */
    public CalendarModelBuilder withAllDayAllTime() {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            withWorkDay(dayOfWeek, LocalTime.MIN, LocalTime.MAX, true);
        }
        return this;
    }

    public CalendarModelBuilder withWork9to5Day(DayOfWeek dayOfWeek) {

        return withWorkDay(dayOfWeek, LocalTime.of(9, 0, 0, 0),
            LocalTime.of(5, 0, 0, 0), true);

    }

    public CalendarModelBuilder withNotWorkDay(DayOfWeek dayOfWeek) {

        return withWorkDay(dayOfWeek, LocalTime.of(9, 0, 0, 0),
            LocalTime.of(5, 0, 0, 0), false);

    }

    public CalendarModelBuilder withZoneId(String zoneId) {
        model.setZoneId(zoneId);
        return this;
    }

    public CalendarModelBuilder with7DayWorking() {

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            withWorkDay(dayOfWeek, LocalTime.of(9, 0, 0, 0),
                LocalTime.of(17, 0, 0, 0), true);

        }
        return this;
    }

    public CalendarModelBuilder with5DayWorking() {

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            boolean isWorking = !dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY);
            withWorkDay(dayOfWeek, LocalTime.of(9, 0, 0, 0),
                LocalTime.of(17, 0, 0, 0), isWorking);

        }
        return this;
    }

    public CalendarModelBuilder withHoliday(HolidayType holidayType, String name, String description,
                                            LocalDate holidayDate) {
        model.getHolidays().add(new HolidayModel(holidayType, name, description, holidayDate));
        return this;
    }

}
