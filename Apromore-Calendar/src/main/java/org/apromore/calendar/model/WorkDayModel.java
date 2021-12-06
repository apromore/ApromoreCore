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
/*-
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

/*
 * This is a Pojo which is a DTO to WorkDay model in JPA.
 * This contains all WorkDay information which is associated with a calendar.
 * This is used in calculation of duration, where the number of hours is the difference between start and end
 * time for a holiday period.
 * The start and end time is adjusted for the start and end day, based on the time provided in the argument.
 */
package org.apromore.calendar.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * WorkDayModel is a template for actual working days.
 *
 * TODO: Using OffsetTime is not a relevant design because WorkDayModel is only a template, not a specific workday in time.
 * It duplicates the offset information in CalendarModel's zoneID.
 * TODO: field Duration is not timezone-aware, e.g. daylight savings, because it's not attached to any actual date yet
 * However, duration with timezone consideration takes longer to compute.
 *
 * @author Nolan Tellis - created
 * @author Bruce Nguyen: add documentation, todo, revised
 */
@Data
@EqualsAndHashCode
public class WorkDayModel {

  @EqualsAndHashCode.Exclude
  private Long id;

  private DayOfWeek dayOfWeek;

  private OffsetTime startTime;

  private OffsetTime endTime;

  private boolean workingDay = true;

  @EqualsAndHashCode.Exclude
  private String createdBy;

  @EqualsAndHashCode.Exclude
  private String updatedBy;

  @EqualsAndHashCode.Exclude
  private Duration duration;

  static LocalDate refDate=Instant
  .ofEpochMilli( 0L )
  .atOffset(ZoneOffset.UTC)
  .toLocalDate();

  public Duration  getWorkDuration(ZonedDateTime start, ZonedDateTime end, Set<LocalDate> holidays) {
    Map<LocalDateTime, Duration> instances = getWorkdayInstances(start.toLocalDateTime(), end.toLocalDateTime());
//    return instances.keySet().stream()
//              .filter(d -> !holidays.contains(d.toLocalDate()))
//              .map(d -> instances.get(d))
//              .reduce(Duration.ZERO, (d1, d2) -> d2.plus(d1));
    Duration totalDuration = Duration.ZERO;
    for (LocalDateTime instance : instances.keySet()) {
      if (!holidays.contains(instance.toLocalDate())) {
        totalDuration = totalDuration.plus(instances.get(instance));
      }
    }
    return totalDuration;
  }

  /**
   * Get workday instances with its duration between two dates
   * @param from starting date
   * @param to ending date
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

  private Duration getDurationSameDayForOneDayPeriod(LocalTime periodStart, LocalTime periodEnd) {
    return Duration.between(startTime.toLocalTime().isBefore(periodStart) ? periodStart : startTime.toLocalTime(),
            endTime.toLocalTime().isAfter(periodEnd) ? periodEnd : endTime.toLocalTime());
  }

  private Duration getDurationSameDayAtStartOfMultiDayPeriod(LocalTime periodStart) {
    return Duration.between(startTime.toLocalTime().isBefore(periodStart) ? periodStart : startTime.toLocalTime(),
            endTime.toLocalTime().isBefore(periodStart) ? periodStart : endTime.toLocalTime());
  }

  private Duration getDurationSameDayAtEndOfMultiDayPeriod(LocalTime periodEnd) {
    return Duration.between(startTime.toLocalTime().isAfter(periodEnd) ? periodEnd : startTime.toLocalTime(),
            endTime.toLocalTime().isAfter(periodEnd) ? periodEnd : endTime.toLocalTime());
  }

}
