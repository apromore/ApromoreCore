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
import net.time4j.ClockUnit;
import net.time4j.Moment;
import net.time4j.range.ChronoInterval;
import net.time4j.range.IntervalCollection;
import net.time4j.range.MomentInterval;
import net.time4j.tz.Timezone;
import org.apache.commons.collections.map.HashedMap;

import java.time.*;
import java.util.*;
import java.util.function.Function;
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

  Map<LocalDate, HolidayModel> holidayLocalDateMap = new HashedMap();

  public static CalendarModel ABSOLUTE_CALENDAR = new AbsoluteCalendarModel();

  /**
   * The two inputs must be of the same offset, otherwise the calculation is not correct.
   * @param starDateTime
   * @param endDateTime
   * @return
   */
  public DurationModel getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {
    DurationModel durationModel = new DurationModel();
    durationModel.setAll(Duration.ofMillis(getDuration(starDateTime.toInstant(), endDateTime.toInstant())));
    return durationModel;
  }

  public DurationModel getDuration(Long starDateTimeUnixTs, Long endDateTimeunixTs) {
      DurationModel durationModel = new DurationModel();
      durationModel.setAll(Duration.ofMillis(getDuration(Instant.ofEpochMilli(starDateTimeUnixTs),
                                                         Instant.ofEpochMilli(endDateTimeunixTs))));
      return durationModel;
  }

  public Long[] getDuration(Long[] starDateTimeUnixTs, Long[] endDateTimeunixTs) {
    Long[] resultList = new Long[starDateTimeUnixTs.length];
    IntStream.range(0, starDateTimeUnixTs.length).parallel().forEach(i -> {
      resultList[i] = getDuration(Instant.ofEpochMilli(starDateTimeUnixTs[i]),
              Instant.ofEpochMilli(endDateTimeunixTs[i]));
    });

    return resultList;
  }

  public long[] getDuration(long[] starDateTimeUnixTs, long[] endDateTimeunixTs) {
    if (starDateTimeUnixTs == null || starDateTimeUnixTs.length == 0 || endDateTimeunixTs == null
        || endDateTimeunixTs.length == 0)
      return new long[] {};
    long[] resultList = new long[starDateTimeUnixTs.length];
    IntStream.range(0, starDateTimeUnixTs.length).parallel().forEach(i -> {
      resultList[i] = getDuration(Instant.ofEpochMilli(starDateTimeUnixTs[i]),
                                  Instant.ofEpochMilli(endDateTimeunixTs[i]));
    });

    return resultList;
  }

  public long getDuration(Instant start, Instant end) {
    IntervalCollection<Moment> intervals = IntervalCollection.onMomentAxis();
    return intervals
              .plus(getWorkDayIntervals(start, end))
              .minus(getHolidayIntervals())
              .withTimeWindow(MomentInterval.between(start, end)).stream()
                    .map(v -> (MomentInterval)v)
                    .map(v -> v.getNominalDuration(Timezone.of(zoneId), ClockUnit.MILLIS))
                    .collect(net.time4j.Duration.summingUp())
                    .getPartialAmount(ClockUnit.MILLIS);
  }

  private List<ChronoInterval<Moment>> getWorkDayIntervals(Instant start, Instant end) {
    return workDays.stream()
            .filter(WorkDayModel::isWorkingDay)
            .map(workDay -> workDay.getRealIntervals(start, end, ZoneId.of(zoneId)).stream())
            .flatMap(Function.identity())
            .collect(Collectors.toList());
  }

  private List<ChronoInterval<Moment>> getHolidayIntervals() {
    return holidays.stream()
            .map(d -> d.getInterval(ZoneId.of(zoneId)))
            .collect(Collectors.toList());
  }

  public void populateHolidayMap() {
    if (holidayLocalDateMap.isEmpty()) {
      holidayLocalDateMap = holidays.parallelStream().collect(
          Collectors.toMap(HolidayModel::getHolidayDate, Function.identity(), (e1, e2) -> e1));
    }
  }

  public List<WorkDayModel> getOrderedWorkDay() {
    workDays.sort(Comparator.comparing(WorkDayModel::getDayOfWeek));
    return workDays;
  }

}
