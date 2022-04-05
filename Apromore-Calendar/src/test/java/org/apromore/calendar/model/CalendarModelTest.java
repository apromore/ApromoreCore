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

package org.apromore.calendar.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.builder.Calendars;
import org.junit.jupiter.api.Test;

public class CalendarModelTest {
    @Test
    void is247Test() {
        assertFalse(Calendars.INSTANCE.empty().is247());
        assertTrue(Calendars.INSTANCE.absolute().is247());
        assertFalse(new CalendarModelBuilder()
            .with7DayWorking()
            .build().is247());
        assertTrue(new CalendarModelBuilder()
            .withAllDayAllTime()
            .build().is247());
        assertFalse(new CalendarModelBuilder()
            .withAllDayAllTime()
            .withHoliday(HolidayType.PUBLIC, "test", "test",
                LocalDate.of(2022, 4, 1))
            .build().is247());

        // Overlapping exists on Monday
        assertTrue(new CalendarModelBuilder()
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.MIN, LocalTime.of(5, 30), true)
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.of(5, 0), LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.TUESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.WEDNESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.THURSDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.FRIDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SATURDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SUNDAY, LocalTime.MIN, LocalTime.MAX, true)
            .build().is247());

        // Gap exists on Monday
        assertFalse(new CalendarModelBuilder()
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.MIN, LocalTime.of(5, 0, 0, 0), true)
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.of(5, 0, 0, 1), LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.TUESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.WEDNESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.THURSDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.FRIDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SATURDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SUNDAY, LocalTime.MIN, LocalTime.MAX, true)
            .build().is247());

        // Both gap and overlapping exist on Monday but no gaps if taking them altogether
        assertTrue(new CalendarModelBuilder()
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.MIN, LocalTime.of(5, 0, 0, 0), true)
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.of(5, 0, 0, 1), LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.of(5, 0, 0, 0), LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.TUESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.WEDNESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.THURSDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.FRIDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SATURDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SUNDAY, LocalTime.MIN, LocalTime.MAX, true)
            .build().is247());

        // Both gap and overlapping exist on Monday but gaps exist if taking them altogether
        assertTrue(new CalendarModelBuilder()
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.MIN, LocalTime.of(5, 1, 0, 0), true)
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.of(5, 0, 1, 0), LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.of(5, 30, 0, 0), LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.TUESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.WEDNESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.THURSDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.FRIDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SATURDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SUNDAY, LocalTime.MIN, LocalTime.MAX, true)
            .build().is247());

        // No gap exists on Monday, but they are unordered
        assertFalse(new CalendarModelBuilder()
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.of(5, 0, 0, 1),
                LocalTime.of(10, 0, 0), true)
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.MIN, LocalTime.of(5, 0, 0, 0), true)
            .withWorkDay(DayOfWeek.MONDAY, LocalTime.of(10, 0, 0, 0), LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.TUESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.WEDNESDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.THURSDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.FRIDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SATURDAY, LocalTime.MIN, LocalTime.MAX, true)
            .withWorkDay(DayOfWeek.SUNDAY, LocalTime.MIN, LocalTime.MAX, true)
            .build().is247());
    }

    @Test
    void immutableTest() {
        CalendarModel model = new CalendarModelBuilder()
            .with7DayWorking()
            .withHoliday(HolidayType.PUBLIC, "test", "test",
                LocalDate.of(2022, 4, 1))
            .build()
            .immutable();

        assertThrows(UnsupportedOperationException.class, () -> model.setName("new name"));
        assertThrows(UnsupportedOperationException.class, () -> model.setZoneId("zoneId"));
        assertThrows(UnsupportedOperationException.class, () -> model.setWorkDays(List.of()));
        assertThrows(UnsupportedOperationException.class, () -> model.setHolidays(List.of()));

        assertThrows(UnsupportedOperationException.class,
            () -> model.getWorkDays().get(0).setWorkingDay(false));
        assertThrows(UnsupportedOperationException.class,
            () -> model.getWorkDays().get(0).setDayOfWeek(DayOfWeek.MONDAY));
        assertThrows(UnsupportedOperationException.class,
            () -> model.getWorkDays().get(0).setStartTime(LocalTime.MIN));
        assertThrows(UnsupportedOperationException.class,
            () -> model.getWorkDays().get(0).setStartTime(LocalTime.MAX));

        assertThrows(UnsupportedOperationException.class,
            () -> model.getHolidays().get(0).setName("newName"));
        assertThrows(UnsupportedOperationException.class,
            () -> model.getHolidays().get(0).setHolidayType(HolidayType.CUSTOM));
        assertThrows(UnsupportedOperationException.class,
            () -> model.getHolidays().get(0).setId(100L));
        assertThrows(UnsupportedOperationException.class,
            () -> model.getHolidays().get(0).setDescription("new Description"));
    }
}
