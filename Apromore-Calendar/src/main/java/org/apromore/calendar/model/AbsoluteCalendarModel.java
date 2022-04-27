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
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;

/**
 * This class represents the absolute 24/7 calendar with every moment is included as working time, no holidays.
 * All duration of [start, end] is simply the number of milliseconds between start and end.
 * It can be used as a default calendar.
 *
 * @author Bruce Nguyen
 */
public class AbsoluteCalendarModel extends CalendarModel {
    protected AbsoluteCalendarModel() {
        super();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            workDays.add(new WorkDayModel(dayOfWeek, LocalTime.MIN, LocalTime.MAX, true));
        }
    }

    @Override
    public Duration getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {
        return getDuration(starDateTime.toInstant(), endDateTime.toInstant());
    }

    @Override
    public Duration getDuration(Instant start, Instant end) {
        return getDuration(start.toEpochMilli(), end.toEpochMilli());
    }

    @Override
    public Duration getDuration(Long starDateTimeUnixTs, Long endDateTimeunixTs) {
        return Duration.ofMillis(endDateTimeunixTs > starDateTimeUnixTs
            ? (endDateTimeunixTs - starDateTimeUnixTs) : 0);
    }

    @Override
    public CalendarModel immutable() {
        return new ImmutableCalendarModel(this);
    }
}
