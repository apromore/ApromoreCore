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
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.calendar.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * This class represents the absolute 24/7 calendar with every moment is included as working time, no holidays.
 * All duration of [start, end] is simply the number of milliseconds between start and end.
 * It can be used as a default calendar.
 * It is not accessible outside this package, only via CalendarModel.ABSOLUTE_CALENDAR.
 *
 * @author Bruce Nguyen
 */
public final class AbsoluteCalendarModel extends CalendarModel {
    public AbsoluteCalendarModel() {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            WorkDayModel workDayModel = new WorkDayModel();
            workDayModel.setDayOfWeek(dayOfWeek);
            workDayModel.setStartTime(LocalTime.MIN);
            workDayModel.setEndTime(LocalTime.MAX);
            workDayModel.setWorkingDay(true);
            workDays.add(workDayModel);
        }
    }

    public DurationModel getDuration(ZonedDateTime starDateTime, ZonedDateTime endDateTime) {
        return getDuration(starDateTime.toInstant().toEpochMilli(), endDateTime.toInstant().toEpochMilli());
    }

    public DurationModel getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {
        return getDuration(starDateTime, endDateTime);
    }

    public DurationModel getDuration(Long starDateTimeUnixTs, Long endDateTimeunixTs) {
        DurationModel durationModel = new DurationModel();
        durationModel.setDuration(Duration.ofMillis(endDateTimeunixTs > starDateTimeUnixTs
            ? (endDateTimeunixTs - starDateTimeUnixTs) : 0));
        return durationModel;
    }
}
