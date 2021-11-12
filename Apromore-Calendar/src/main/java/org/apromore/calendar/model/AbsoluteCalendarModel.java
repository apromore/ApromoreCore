/**
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
package org.apromore.calendar.model;

import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class represents the absolute 24/7 calendar with every moment is included as working time, no holidays.
 * All duration of [start, end] is simply the number of milliseconds between start and end.
 * It can be used as a default calendar.
 * It is not accessible outside this package, only via CalendarModel.ABSOLUTE_CALENDAR.
 *
 * @author Bruce Nguyen
 */
public class AbsoluteCalendarModel extends CalendarModel {
    protected AbsoluteCalendarModel() {};

    public DurationModel getDuration(ZonedDateTime starDateTime, ZonedDateTime endDateTime) {
        return getDuration(starDateTime.toInstant().toEpochMilli(), endDateTime.toInstant().toEpochMilli());
    }

    public DurationModel getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {
        return getDuration(starDateTime, endDateTime);
    }

    public DurationModel getDuration(Long starDateTimeUnixTs, Long endDateTimeunixTs) {
        DurationModel durationModel = new DurationModel();
        durationModel.setDuration(Duration.ofMillis(endDateTimeunixTs > starDateTimeUnixTs ?
                                        (endDateTimeunixTs - starDateTimeUnixTs) : 0 ));
        return durationModel;
    }

    public void populateHolidayMap() {
        //Do nothing
    }

    @Override
    public List<HolidayModel> getHolidays() {
        return Collections.unmodifiableList(Collections.EMPTY_LIST);
    }

    @Override
    public List<WorkDayModel> getWorkDays() {
        return Collections.unmodifiableList(Collections.EMPTY_LIST);
    }

    @Override
    public Map<LocalDate, HolidayModel> getHolidayLocalDateMap() {
        return Collections.unmodifiableMap(Collections.EMPTY_MAP);
    }

    @Override
    public List<WorkDayModel> getOrderedWorkDay() {
        return Collections.unmodifiableList(Collections.EMPTY_LIST);
    }
}
