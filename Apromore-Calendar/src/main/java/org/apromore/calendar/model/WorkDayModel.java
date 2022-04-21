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
 * This is a Pojo which is a DTO to WorkDay model in JPA.
 * This contains all WorkDay information which is associated with a calendar.
 * This is used in calculation of duration, where the number of hours is the difference between start and end
 * time for a holiday period.
 * The start and end time is adjusted for the start and end day, based on the time provided in the argument.
 */

package org.apromore.calendar.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * WorkDayModel is a template for actual working days.
 * @author Nolan Tellis - created
 * @author Bruce Nguyen: add documentation, todo, revised
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkDayModel implements Comparable<WorkDayModel> {

    @EqualsAndHashCode.Exclude
    protected @NonNull Long id = 0L; // only used for mapping from database object

    protected @NonNull DayOfWeek dayOfWeek = DayOfWeek.MONDAY;

    protected @NonNull LocalTime startTime = LocalTime.MIN;

    protected @NonNull LocalTime endTime = LocalTime.MAX;

    protected boolean workingDay = true;

    @EqualsAndHashCode.Exclude
    protected String createdBy = "";

    @EqualsAndHashCode.Exclude
    protected String updatedBy = "";

    public WorkDayModel(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, boolean workingDay) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workingDay = workingDay;
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    public Duration getWorkDuration(@NonNull ZonedDateTime start,
                                    @NonNull ZonedDateTime end,
                                    @NonNull Set<LocalDate> holidays) {
        Map<LocalDateTime, Duration> instances = getWorkdayInstances(start.toLocalDateTime(), end.toLocalDateTime());
        Duration totalDuration = Duration.ZERO;
        for (LocalDateTime instance : instances.keySet()) {
            if (!holidays.contains(instance.toLocalDate())) {
                totalDuration = totalDuration.plus(instances.get(instance));
            }
        }
        return totalDuration;
    }

    public long getStartTimeInMillis() {
        return startTime.getHour() * 3600000L + startTime.getMinute() * 60000L + startTime.getSecond() * 1000L
            + startTime.getNano() / 1000000L;
    }

    public long getEndTimeInMillis() {
        return endTime.getHour() * 3600000L + endTime.getMinute() * 60000L + endTime.getSecond() * 1000L
            + endTime.getNano() / 1000000L;
    }

    public WorkDayModel immutable() {
        return new ImmutableWorkDayModel(this);
    }

    /**
     * Get workday instances with its duration between two dates.
     *
     * @param from starting date
     * @param to   ending date
     * @return map from an instance to its duration
     */
    private Map<LocalDateTime, Duration> getWorkdayInstances(LocalDateTime from, LocalDateTime to) {
        Map<LocalDateTime, Duration> result = new HashMap<>();
        if (from.toLocalDate().isEqual(to.toLocalDate())) {
            result.put(from, from.getDayOfWeek().equals(this.dayOfWeek)
                ? getDurationSameDayForOneDayPeriod(from.toLocalTime(), to.toLocalTime())
                : Duration.ZERO);
            return result;
        }

        int daysInWeek = 7;
        int daysToAdd = (this.dayOfWeek.getValue() - from.getDayOfWeek().getValue() + daysInWeek) % daysInWeek;
        LocalDateTime instance = from.plusDays(daysToAdd);
        while (!instance.toLocalDate().isAfter(to.toLocalDate())) {
            result.put(instance,
                (instance.toLocalDate().isEqual(from.toLocalDate()))
                    ? getDurationSameDayAtStartOfMultiDayPeriod(from.toLocalTime())
                    : (
                    (instance.toLocalDate().isEqual(to.toLocalDate()))
                        ? getDurationSameDayAtEndOfMultiDayPeriod(to.toLocalTime())
                        : this.getDuration()
                ));
            instance = instance.plusDays(daysInWeek);
        }
        return result;
    }

    // periodStart and periodEnd are on the same day
    // startTime --- endTime ---> periodStart ---> periodEnd  : 0
    // periodStart --- periodEnd ---> startTime ---> endTime  : 0
    // startTime --- periodStart ---> endTime ---> periodEnd  : endTime - periodStart
    // periodStart ---> startTime ---> periodEnd ---> endTime : periodEnd - startTime
    // startTime --- periodStart ---> periodEnd ---> endTime  : periodEnd - periodStart
    // periodStart --- startTime ---> endTime ---> periodEnd  : endTime - startTime
    private Duration getDurationSameDayForOneDayPeriod(LocalTime periodStart, LocalTime periodEnd) {
        if (startTime.isAfter(periodEnd) || endTime.isBefore(periodStart)) {
            return Duration.ZERO;
        }
        return Duration.between(startTime.isBefore(periodStart) ? periodStart : startTime,
            endTime.isAfter(periodEnd) ? periodEnd : endTime);
    }

    // periodStart and periodEnd are on different days
    // startTime ---> endTime ---> periodStart ---> periodEnd(next day)  : periodStart - periodStart = 0
    // startTime ---> periodStart ---> endTime ---> periodEnd(next day) : endTime - periodStart
    // periodStart ---> startTime ---> endTime ---> periodEnd(next day) : endTime - startTime
    private Duration getDurationSameDayAtStartOfMultiDayPeriod(LocalTime periodStart) {
        return Duration.between(startTime.isBefore(periodStart) ? periodStart : startTime,
            endTime.isBefore(periodStart) ? periodStart : endTime);
    }

    // periodStart and periodEnd are on different days
    // periodStart(previous day) ---> startTime ---> endTime ---> periodEnd : endTime - startTime
    // periodStart(previous day) ---> startTime ---> periodEnd ---> endTime : periodEnd - startTime
    // periodStart(previous day) ---> periodEnd ---> startTime ---> endTime : periodEnd - periodEnd = 0
    private Duration getDurationSameDayAtEndOfMultiDayPeriod(LocalTime periodEnd) {
        return Duration.between(startTime.isAfter(periodEnd) ? periodEnd : startTime,
            endTime.isAfter(periodEnd) ? periodEnd : endTime);
    }

    @Override
    public int compareTo(@NonNull WorkDayModel d2) {
        int startCompare = this.getStartTime().compareTo(d2.getStartTime());
        return (startCompare != 0) ? startCompare : this.getEndTime().compareTo(d2.getEndTime());
    }
}
