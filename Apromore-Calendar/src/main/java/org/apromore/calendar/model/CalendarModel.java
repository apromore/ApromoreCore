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
 * This is the calendar model, used for getting duration based on start and end time.
 * The start time can be a ZonedDateTime, or Unix timestamp.
 * This model is thread safe.
 * This is a model created from Calendar which is in the db layer.
 * The duration calculation is based on number of working days and holidays associated to the calendar
 * @see CalendarService.getCalendar(id) for details
 * 
 */

package org.apromore.calendar.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Data;


@Data
public class CalendarModel {

  private Long id = new Random().nextLong();
  private String name = "CalendarModel";
  private OffsetDateTime created = OffsetDateTime.now(ZoneOffset.UTC);
  private OffsetDateTime updated = OffsetDateTime.now(ZoneOffset.UTC);
  private String createdBy = "";
  private String updatedBy = "";
  private String zoneId = ZoneOffset.UTC.getId();
  private List<WorkDayModel> workDays = new ArrayList<WorkDayModel>();
  private List<HolidayModel> holidays = new ArrayList<HolidayModel>();

  public DurationModel getDuration(ZonedDateTime starDateTime, ZonedDateTime endDateTime) {

    Map<DayOfWeek, WorkDayModel> dayOfWeekWorkDayMap = getWorkDayMap();

    Map<LocalDate, HolidayModel> holidayLocalDateMap = getHolidayMap();

    DurationModel durationModel = new DurationModel();

    // if startdate and enddate is same
    LocalDate localStartDate = starDateTime.toLocalDate();
    LocalDate localEndDate = endDateTime.toLocalDate();

    if (isSameDay(localStartDate, localEndDate)) {

      Duration durationForSameDay = Duration.ZERO;

      WorkDayModel workDayModel = dayOfWeekWorkDayMap.get(starDateTime.getDayOfWeek());

      if (workDayModel.isWorkingDay() && holidayLocalDateMap.get(localStartDate) == null) {
        durationForSameDay = getDurationForSameDay(starDateTime
            .toOffsetDateTime()
            .toOffsetTime(),
            endDateTime
                .toOffsetDateTime()
                .toOffsetTime(),
            workDayModel);
      }
      durationModel.setAll(durationForSameDay);
      return durationModel;

    }
    // if Start Day and end day is not same.

    Duration totalDuration = Duration.ZERO;
    for (ZonedDateTime dayDateTime = starDateTime; !dayDateTime.toLocalDate()
        .isAfter(endDateTime.toLocalDate()); dayDateTime = dayDateTime.plus(1, ChronoUnit.DAYS)) {

      DayOfWeek currentDayOfWeek = dayDateTime.getDayOfWeek();

      WorkDayModel workDay = dayOfWeekWorkDayMap.get(currentDayOfWeek);

      Duration calculatedDuration = workDay.getDuration();
      if (!workDay.isWorkingDay() ||
          holidayLocalDateMap.get(localStartDate) != null) {

        calculatedDuration = Duration.ZERO;

      } else if (isStartDay(starDateTime, dayDateTime)) {

        calculatedDuration = workDay
            .getSameDayDurationByStartTime(starDateTime.toOffsetDateTime().toOffsetTime());

      } else if (isEndDay(endDateTime, dayDateTime)) {
        calculatedDuration =
            workDay.getSameDayDurationByEndTime(endDateTime.toOffsetDateTime().toOffsetTime());
      }

      totalDuration = totalDuration.plus(calculatedDuration);

    }
    durationModel.setAll(totalDuration);

    return durationModel;
  }

  public DurationModel getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {

    ZonedDateTime zonedStartDateTime =
        ZonedDateTime.ofInstant(starDateTime.toInstant(), ZoneId.of(zoneId));
    ZonedDateTime zonedEndDateTime =
        ZonedDateTime.ofInstant(endDateTime.toInstant(), ZoneId.of(zoneId));

    return getDuration(zonedStartDateTime, zonedEndDateTime);
  }

  public DurationModel getDuration(Long starDateTimeUnixTs, Long endDateTimeunixTs) {

    ZonedDateTime zonedStartDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(starDateTimeUnixTs), ZoneId.of(zoneId));
    ZonedDateTime zonedEndDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateTimeunixTs), ZoneId.of(zoneId));
    return getDuration(zonedStartDateTime, zonedEndDateTime);
  }


  public Long[] getDuration(Long[] starDateTimeUnixTs, Long[] endDateTimeunixTs) {
    Long[] resultList = new Long[starDateTimeUnixTs.length];

    ZoneId zone = ZoneId.of(zoneId);

    IntStream.range(0, starDateTimeUnixTs.length).parallel().forEach(i -> {

      ZonedDateTime zonedStartDateTime =
          ZonedDateTime.ofInstant(Instant.ofEpochMilli(starDateTimeUnixTs[i]), zone);
      ZonedDateTime zonedEndDateTime =
          ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateTimeunixTs[i]), zone);
      resultList[i] = getDuration(zonedStartDateTime, zonedEndDateTime).getDuration().toMillis();

    });

    return resultList;
  }
  
  public long[] getDuration(long[] starDateTimeUnixTs, long[] endDateTimeunixTs) {
      if (starDateTimeUnixTs == null || starDateTimeUnixTs.length == 0 ||
          endDateTimeunixTs == null || endDateTimeunixTs.length == 0) return new long[] {};
        
      long[] resultList = new long[starDateTimeUnixTs.length];

      ZoneId zone = ZoneId.of(zoneId);

      IntStream.range(0, starDateTimeUnixTs.length).parallel().forEach(i -> {

        ZonedDateTime zonedStartDateTime =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(starDateTimeUnixTs[i]), zone);
        ZonedDateTime zonedEndDateTime =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateTimeunixTs[i]), zone);
        resultList[i] = getDuration(zonedStartDateTime, zonedEndDateTime).getDuration().toMillis();

      });

      return resultList;
    }



  private Map<LocalDate, HolidayModel> getHolidayMap() {
    return holidays.parallelStream()
        .collect(Collectors.toMap(HolidayModel::getHolidayDate, Function.identity()));
  }


  private Map<DayOfWeek, WorkDayModel> getWorkDayMap() {
    return workDays.parallelStream()
        .collect(Collectors.toMap(WorkDayModel::getDayOfWeek, Function.identity()));
  }


  private boolean isEndDay(ZonedDateTime endDateTime, ZonedDateTime dayDateTime) {
    return dayDateTime.toLocalDate().isEqual(endDateTime.toLocalDate());
  }


  private boolean isStartDay(ZonedDateTime zonedStartDateTime, ZonedDateTime dayDateTime) {
    return isEndDay(zonedStartDateTime, dayDateTime);
  }


  private boolean isSameDay(LocalDate localStartDate, LocalDate localEndDate) {
    return localStartDate.equals(localEndDate);
  }


  private Duration getDurationForSameDay(OffsetTime startTime,
      OffsetTime endTime,
      WorkDayModel workDayModel) {
    DurationModel durationModel = new DurationModel();

    Duration duration = Duration.between(workDayModel.getAdjustedStartTime(startTime),
        workDayModel.getAdjustedEndTime(endTime));

    return duration.isNegative() ? Duration.ZERO : duration;

  }
  
  public List<WorkDayModel> getOrderedWorkDay()
  {
	  workDays.sort(Comparator.comparing(WorkDayModel::getDayOfWeek));
	  return workDays;
  }


}
