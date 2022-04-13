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
/*
 *
 * This is the calendar model, used for getting duration based on start and end time. The start time
 * can be a ZonedDateTime, or Unix timestamp. This model is thread safe. This is a model created
 * from Calendar which is in the db layer. The duration calculation is based on number of working
 * days and holidays associated to the calendar
 *
 * @see CalendarService.getCalendar(id) for details
 *
 */

package org.apromore.plugin.portal.calendar.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor (access = AccessLevel.PROTECTED)
@NoArgsConstructor (access = AccessLevel.PROTECTED)
public class Calendar {
    @Setter(AccessLevel.NONE)
    private @NonNull Long id = 0L; // only used for mapping from database object

    private @NonNull String name = "CalendarModel";
    private @NonNull String zoneId = ZoneOffset.UTC.getId();
    private @NonNull List<WorkDay> workDays = new ArrayList<>();
    private @NonNull List<Holiday> holidays = new ArrayList<>();
    private @NonNull OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);

    public List<WorkDay> getOrderedWorkDay() {
        List<WorkDay> sortedList = new ArrayList<>(workDays);
        sortedList.sort(Comparator.comparing(WorkDay::getDayOfWeek));
        return sortedList;
    }
}
