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

package org.apromore.plugin.portal.calendar.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.HolidayModel;
import org.apromore.calendar.model.HolidayType;
import org.apromore.calendar.model.WorkDayModel;
import org.junit.jupiter.api.Test;

class ConversionTest {

    @Test
    void toHolidayModelTest() {
        Holiday holiday = CalendarFactory.INSTANCE.createHoliday(
            HolidayType.PUBLIC,
            "HolidayName",
            "Holiday Desc",
            LocalDate.of(2022, 4, 1));
        HolidayModel model = CalendarFactory.INSTANCE.toHolidayModel(holiday);

        assertEquals(holiday.getHolidayDate(), model.getHolidayDate());
        assertEquals(holiday.getHolidayType(), model.getHolidayType());
        assertEquals(holiday.getName(), model.getName());
        assertEquals(holiday.getDescription(), model.getDescription());
    }

    @Test
    void toWorkDayModelTest() {
        WorkDay workDay = CalendarFactory.INSTANCE.createDefaultWorkDay(DayOfWeek.MONDAY);
        WorkDayModel model = CalendarFactory.INSTANCE.toWorkDayModel(workDay);

        assertEquals(workDay.getDayOfWeek(), model.getDayOfWeek());
        assertEquals(workDay.getStartTime(), model.getStartTime());
        assertEquals(workDay.getEndTime(), model.getEndTime());
    }

    @Test
    void fromCalendarModelTest() {
        CalendarModel model = (new CalendarModelBuilder())
            .with5DayWorking()
            .withHoliday(HolidayType.PUBLIC,
                "HolidayName",
                "HolidayDescription",
                LocalDate.of(2022, 4, 1))
            .build();
        Calendar cal = CalendarFactory.INSTANCE.fromCalendarModel(model);

        assertEquals(model.getId(), cal.getId());
        assertEquals(model.getName(), cal.getName());
        assertEquals(model.getZoneId(), cal.getZoneId());
        assertEquals(model.getWorkDays().size(), cal.getWorkDays().size());
        assertEquals(model.getHolidays().size(), cal.getHolidays().size());

        assertEquals(model.getWorkDays().get(0).getDayOfWeek(), cal.getWorkDays().get(0).getDayOfWeek());
        assertEquals(model.getWorkDays().get(0).getStartTime(), cal.getWorkDays().get(0).getStartTime());
        assertEquals(model.getWorkDays().get(0).getEndTime(), cal.getWorkDays().get(0).getEndTime());

        assertEquals(model.getWorkDays().get(4).getDayOfWeek(), cal.getWorkDays().get(4).getDayOfWeek());
        assertEquals(model.getWorkDays().get(4).getStartTime(), cal.getWorkDays().get(4).getStartTime());
        assertEquals(model.getWorkDays().get(4).getEndTime(), cal.getWorkDays().get(4).getEndTime());

        assertEquals(model.getHolidays().get(0).getHolidayDate(), cal.getHolidays().get(0).getHolidayDate());

    }
}
