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
import net.time4j.Moment;
import net.time4j.range.ChronoInterval;
import net.time4j.range.MomentInterval;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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

  /**
   * Get all real intervals of this work day model within start to end instants in a time zone
   * @param start
   * @param end
   * @param zoneId
   * @return list of all real working day intervals within the period
   */
  public List<ChronoInterval<Moment>>  getRealIntervals(Instant start, Instant end, ZoneId zoneId) {
    ZonedDateTime startDate = ZonedDateTime.ofInstant(start, zoneId);
    ZonedDateTime endDate = ZonedDateTime.ofInstant(end, zoneId);
    return  LongStream.range(0, ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1)
                  .mapToObj(startDate::plusDays)
                  .filter(d -> d.getDayOfWeek().equals(dayOfWeek))
                  .map(this::getIntervalAtDate)
                  .collect(Collectors.toList());
  }

  public ChronoInterval<Moment> getIntervalAtDate(ZonedDateTime d) {
    return MomentInterval.between(startTime.atDate(d.toLocalDate()).toInstant(),
            endTime.atDate(d.toLocalDate()).toInstant());
  }

}
