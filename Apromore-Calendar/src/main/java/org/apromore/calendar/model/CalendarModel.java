/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;
import sun.security.provider.certpath.CollectionCertStore;

@Data
public class CalendarModel {

  private Long id;
  private String name;
  private OffsetDateTime created;
  private OffsetDateTime updated;
  private String createdBy;
  private String updatedBy;
  private String zoneId;
  private List<WorkDayModel> workDays = new ArrayList<WorkDayModel>();
  private List<HolidayModel> holidays = new ArrayList<HolidayModel>();


  public DurationModel getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {

    Map<DayOfWeek, WorkDayModel> dayOfWeekWorkDayMap = getWorkDayMap();

    Map<LocalDate, HolidayModel> holidayLocalDateMap = getHolidayMap();

    DurationModel durationModel = new DurationModel();

    ZonedDateTime zonedStartDateTime =
        ZonedDateTime.ofInstant(starDateTime.toInstant(), ZoneId.of(zoneId));
    ZonedDateTime zonedEndDateTime =
        ZonedDateTime.ofInstant(endDateTime.toInstant(), ZoneId.of(zoneId));


    // if startdate and enddate is same
    LocalDate localStartDate = zonedStartDateTime.toLocalDate();
    LocalDate localEndDate = zonedEndDateTime.toLocalDate();


    if (isSameDay(localStartDate, localEndDate)) {

      Duration durationForSameDay = Duration.ZERO;

      WorkDayModel workDayModel = dayOfWeekWorkDayMap.get(zonedStartDateTime.getDayOfWeek());

      if (workDayModel.isWorkingDay() && holidayLocalDateMap.get(localStartDate) == null) {
        durationForSameDay = getDurationForSameDay(zonedStartDateTime
            .toOffsetDateTime()
            .toOffsetTime(),
            zonedEndDateTime
                .toOffsetDateTime()
                .toOffsetTime(),
            workDayModel);
      }
      durationModel.setAll(durationForSameDay);
      return durationModel;

    }

    // if Start Day and end day is not same.

    Duration totalDuration = Duration.ZERO;
    for (ZonedDateTime dayDateTime = zonedStartDateTime; !dayDateTime.toLocalDate()
        .isAfter(endDateTime.toLocalDate()); dayDateTime = dayDateTime.plus(1, ChronoUnit.DAYS)) {

      DayOfWeek currentDayOfWeek = dayDateTime.getDayOfWeek();

      WorkDayModel workDay = dayOfWeekWorkDayMap.get(currentDayOfWeek);

      Duration calculatedDuration = workDay.getDuration();
      if (!workDay.isWorkingDay() ||
          holidayLocalDateMap.get(localStartDate) != null) {

        calculatedDuration = Duration.ZERO;

      } else if (isStartDay(zonedStartDateTime, dayDateTime)) {

        calculatedDuration = workDay
            .getSameDayDurationByStartTime(zonedStartDateTime.toOffsetDateTime().toOffsetTime());

      } else if (isEndDay(zonedEndDateTime, dayDateTime)) {
        calculatedDuration =
            workDay.getSameDayDurationByEndTime(zonedEndDateTime.toOffsetDateTime().toOffsetTime());
      }

      totalDuration = totalDuration.plus(calculatedDuration);

    }
    durationModel.setAll(totalDuration);

    return durationModel;
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


}
