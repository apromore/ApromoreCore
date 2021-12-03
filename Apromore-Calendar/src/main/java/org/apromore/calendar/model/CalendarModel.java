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

import lombok.Data;
import net.time4j.Moment;
import net.time4j.range.ChronoInterval;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Nolan Tellis:
 *    - Created this module
 * @author Bruce Nguyen:
 *    - Add new duration calculation based on intervals
 */
@Data
public class CalendarModel {

  private Long id = new Random().nextLong();
  private String name = "CalendarModel";
  private OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);
  private OffsetDateTime updated = OffsetDateTime.now(ZoneOffset.UTC);
  private String createdBy = "";
  private String updatedBy = "";
  private String zoneId = ZoneOffset.UTC.getId();
  private List<WorkDayModel> workDays = new ArrayList<>();
  private List<HolidayModel> holidays = new ArrayList<>();

  public static CalendarModel ABSOLUTE_CALENDAR = new AbsoluteCalendarModel();

  private List<ChronoInterval<Moment>> holidayIntervals;

  public DurationModel getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {
    DurationModel durationModel = new DurationModel();
    durationModel.setAll(getDuration(starDateTime.toInstant(), endDateTime.toInstant()));
    return durationModel;
  }

  public DurationModel getDuration(Long starDateTimeUnixTs, Long endDateTimeunixTs) {
      DurationModel durationModel = new DurationModel();
      durationModel.setAll(getDuration(Instant.ofEpochMilli(starDateTimeUnixTs), Instant.ofEpochMilli(endDateTimeunixTs)));
      return durationModel;
  }

  public Duration getDuration(Instant start, Instant end) {
    if (end.isBefore(start) || end.equals(start)) return Duration.ZERO;
    ZonedDateTime startDate = ZonedDateTime.ofInstant(start, ZoneId.of(zoneId));
    ZonedDateTime endDate = ZonedDateTime.ofInstant(end, ZoneId.of(zoneId));
    Set<LocalDate> holidays = getHolidayDates();
    return workDays.parallelStream()
            .filter(WorkDayModel::isWorkingDay)
            .map(workDay -> workDay.getWorkDuration(startDate, endDate, holidays))
            .reduce(Duration.ZERO, (d1, d2) -> d2.plus(d1));
  }

  // This duration is rounded to the nearest milliseconds
  public long getDurationMillis(Instant start, Instant end) {
    Duration dur = getDuration(start, end);
    return dur.getNano() > 500_000
              ? dur.truncatedTo(ChronoUnit.MILLIS).plusMillis(1).toMillis()
              : dur.toMillis();
  }

  public Long[] getDuration(Long[] starDateTimeUnixTs, Long[] endDateTimeunixTs) {
    Long[] resultList = new Long[starDateTimeUnixTs.length];
    IntStream.range(0, starDateTimeUnixTs.length).parallel().forEach(i ->
      resultList[i] = getDurationMillis(Instant.ofEpochMilli(starDateTimeUnixTs[i]),
              Instant.ofEpochMilli(endDateTimeunixTs[i])));

    return resultList;
  }

  public long[] getDuration(long[] starDateTimeUnixTs, long[] endDateTimeunixTs) {
    if (starDateTimeUnixTs == null || starDateTimeUnixTs.length == 0 || endDateTimeunixTs == null
        || endDateTimeunixTs.length == 0)
      return new long[] {};
    long[] resultList = new long[starDateTimeUnixTs.length];
    IntStream.range(0, starDateTimeUnixTs.length).parallel().forEach(i ->
      resultList[i] = getDurationMillis(Instant.ofEpochMilli(starDateTimeUnixTs[i]),
                                  Instant.ofEpochMilli(endDateTimeunixTs[i])));

    return resultList;
  }

  private Set<LocalDate> getHolidayDates() {
    return holidays.stream().map(HolidayModel::getHolidayDate).collect(Collectors.toSet());
  }

  public List<WorkDayModel> getOrderedWorkDay() {
    workDays.sort(Comparator.comparing(WorkDayModel::getDayOfWeek));
    return workDays;
  }

}
