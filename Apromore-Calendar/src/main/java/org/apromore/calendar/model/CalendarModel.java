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

package org.apromore.calendar.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Represents one custom calendar.
 * @author Nolan Tellis:
 *     - Created this module
 * @author Bruce Nguyen:
 *     - Add new duration calculation based on intervals
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarModel {

    protected @NonNull Long id = 0L; // only used for mapping from database object
    protected @NonNull String name = "CalendarModel";
    protected @NonNull OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);
    protected @NonNull OffsetDateTime updated = OffsetDateTime.now(ZoneOffset.UTC);
    protected String createdBy = "";
    protected String updatedBy = "";

    @Setter(AccessLevel.NONE)
    protected @NonNull String zoneId = ZoneOffset.UTC.getId();

    protected @NonNull List<WorkDayModel> workDays = new ArrayList<>();
    protected @NonNull List<HolidayModel> holidays = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<LocalDate> holidayDates = new HashSet<>();

    /**
     * Set zone id.
     * @param zoneId zone id name
     * @throws DateTimeException or ZoneRulesException if zoneId is not valid
     */
    public void setZoneId(String zoneId) {
        ZoneId.of(zoneId);
        this.zoneId = zoneId;
    }

    public Duration getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {
        return getDuration(starDateTime.toInstant(), endDateTime.toInstant());
    }

    public Duration getDuration(Long starDateTimeUnixTs, Long endDateTimeUnixTs) {
        return getDuration(Instant.ofEpochMilli(starDateTimeUnixTs), Instant.ofEpochMilli(endDateTimeUnixTs));
    }

    public Duration getDuration(Instant start, Instant end) {
        if (end.isBefore(start) || end.equals(start)) {
            return Duration.ZERO;
        }
        ZonedDateTime startDate = ZonedDateTime.ofInstant(start, ZoneId.of(zoneId));
        ZonedDateTime endDate = ZonedDateTime.ofInstant(end, ZoneId.of(zoneId));
        collectHolidayDates();
        Duration totalDuration = Duration.ZERO;
        for (WorkDayModel workDay : workDays) {
            if (workDay.isWorkingDay()) {
                totalDuration = totalDuration.plus(workDay.getWorkDuration(startDate, endDate, holidayDates));
            }
        }
        return totalDuration;
    }

    public Long[] getDuration(Long[] starDateTimeUnixTs, Long[] endDateTimeUnixTs) {
        Long[] resultList = new Long[starDateTimeUnixTs.length];
        IntStream.range(0, starDateTimeUnixTs.length).parallel().forEach(i ->
            resultList[i] = getDurationMillis(Instant.ofEpochMilli(starDateTimeUnixTs[i]),
                Instant.ofEpochMilli(endDateTimeUnixTs[i])));

        return resultList;
    }

    public long[] getDuration(long[] starDateTimeUnixTs, long[] endDateTimeUnixTs) {
        if (starDateTimeUnixTs == null || starDateTimeUnixTs.length == 0 || endDateTimeUnixTs == null
            || endDateTimeUnixTs.length == 0) {
            return new long[] {};
        }
        long[] resultList = new long[starDateTimeUnixTs.length];
        IntStream.range(0, starDateTimeUnixTs.length).parallel().forEach(i ->
            resultList[i] = getDurationMillis(Instant.ofEpochMilli(starDateTimeUnixTs[i]),
                Instant.ofEpochMilli(endDateTimeUnixTs[i])));

        return resultList;
    }

    public long getDurationMillis(long start, long end) {
        return getDurationMillis(Instant.ofEpochMilli(start), Instant.ofEpochMilli(end));
    }

    // This duration is rounded to the nearest milliseconds
    public long getDurationMillis(Instant start, Instant end) {
        Duration dur = getDuration(start, end);
        return dur.getNano() > 500_000
            ? dur.truncatedTo(ChronoUnit.MILLIS).plusMillis(1).toMillis()
            : dur.toMillis();
    }

    private void collectHolidayDates() {
        if (holidayDates.isEmpty()) {
            holidayDates = holidays.stream().map(HolidayModel::getHolidayDate).collect(Collectors.toSet());
        }
    }

    public List<WorkDayModel> getOrderedWorkDay() {
        List<WorkDayModel> sortedList = new ArrayList<>(workDays);
        sortedList.sort(Comparator.comparing(WorkDayModel::getDayOfWeek));
        return sortedList;
    }

    public boolean is247() {
        if (!holidays.isEmpty()) {
            return false;
        }
        if (workDays.stream().filter(WorkDayModel::isWorkingDay)
            .map(WorkDayModel::getDayOfWeek).distinct().count() < 7) {
            return false;
        }
        for (DayOfWeek dow : DayOfWeek.values()) {
            List<WorkDayModel> sortedDays = workDays.stream()
                .filter(d -> d.isWorkingDay() && d.getDayOfWeek().equals(dow))
                .sorted((d1, d2) -> d1.compareTo(d2))
                .collect(Collectors.toList());
            if (betweenGapExists(sortedDays)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if between gap exists between these days in one day, from LocalTime.MIN to LocalTime.MAX
     * @param sortedDays list of WorkDayModel of the same day of week
     * @return true if any gap exists between these days
     */
    private boolean betweenGapExists(List<WorkDayModel> sortedDays) {
        LocalTime largestEnd = LocalTime.MIN;
        for (WorkDayModel d : sortedDays) {
            if (d.getStartTime().isAfter(largestEnd)) {
                return true;
            } else if (d.getEndTime().isAfter(largestEnd)) {
                largestEnd = d.getEndTime();
                if (largestEnd.equals(LocalTime.MAX)) {
                    return false;
                }
            }
        }
        return true;
    }

    public CalendarModel immutable() {
        return new ImmutableCalendarModel(this);
    }
}
