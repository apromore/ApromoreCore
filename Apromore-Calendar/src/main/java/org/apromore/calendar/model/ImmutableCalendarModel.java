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

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImmutableCalendarModel extends CalendarModel {
    private CalendarModel mutableModel;

    protected ImmutableCalendarModel(CalendarModel model) {
        super();
        this.mutableModel = model;
        this.id = model.getId();
        this.name = model.getName();
        this.zoneId = model.getZoneId();
        this.created = OffsetDateTime.from(model.getCreated());
        this.updated = OffsetDateTime.from(model.getUpdated());
        this.createdBy = model.getCreatedBy();
        this.updatedBy = model.getUpdatedBy();
        this.workDays = model.getWorkDays().stream()
            .map(WorkDayModel::immutable)
            .collect(Collectors.toList());
        this.holidays = model.getHolidays().stream()
            .map(HolidayModel::immutable)
            .collect(Collectors.toList());
    }

    public List<WorkDayModel> getWorkDays() {
        return Collections.unmodifiableList(workDays);
    }

    public List<HolidayModel> getHolidays() {
        return Collections.unmodifiableList(holidays);
    }

    @Override
    public Duration getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {
        return mutableModel.getDuration(starDateTime, endDateTime);
    }

    @Override
    public Duration getDuration(Long starDateTimeUnixTs, Long endDateTimeunixTs) {
        return mutableModel.getDuration(starDateTimeUnixTs, endDateTimeunixTs);
    }

    @Override
    public Duration getDuration(Instant start, Instant end) {
        return mutableModel.getDuration(start, end);
    }

    @Override
    public long getDurationMillis(long start, long end) {
        return mutableModel.getDurationMillis(start, end);
    }

    @Override
    public long getDurationMillis(Instant start, Instant end) {
        return mutableModel.getDurationMillis(start, end);
    }

    @Override
    public void setId(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setZoneId(String zoneId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWorkDays(List<WorkDayModel> workDays) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHolidays(List<HolidayModel> holidays) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCreated(OffsetDateTime created) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUpdated(OffsetDateTime updated) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCreatedBy(String createdBy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        throw new UnsupportedOperationException();
    }
}
