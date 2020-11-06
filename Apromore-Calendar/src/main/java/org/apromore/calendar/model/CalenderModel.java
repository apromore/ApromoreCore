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
public class CalenderModel {

  private Long id;
  private String name;
  private OffsetDateTime created;
  private OffsetDateTime updated;
  private String createdBy;
  private String updatedBy;
  private List<WorkDayModel> workDays = new ArrayList<WorkDayModel>();
  private List<HolidayModel> holidays = new ArrayList<HolidayModel>();


  public DurationModel getDuration(OffsetDateTime starDateTime, OffsetDateTime endDateTime) {


    Map<DayOfWeek, WorkDayModel> dayOfWeekWorkDayMap = workDays.parallelStream()
        .collect(Collectors.toMap(WorkDayModel::getDayOfWeek, Function.identity()));
    
    Map<LocalDate, HolidayModel> holidayLocalDateMap = holidays.parallelStream()
        .collect(Collectors.toMap(HolidayModel::getHolidayDate, Function.identity()));

    DurationModel durationModel = new DurationModel();


    // if startdate and enddate is same
    LocalDate localStartDate = starDateTime.toLocalDate();
    LocalDate localEndDate = endDateTime.toLocalDate();

    if (isSameDay(localStartDate, localEndDate)) {

      Duration durationForSameDay = Duration.ZERO;

      WorkDayModel workDayModel = dayOfWeekWorkDayMap.get(starDateTime.getDayOfWeek());
      if (workDayModel.isWorkingDay() && holidayLocalDateMap.get(localStartDate)==null) {
        durationForSameDay = getDurationForSameDay(starDateTime.toOffsetTime(),
            endDateTime.toOffsetTime(),
            workDayModel);
      }
      durationModel.setAll(durationForSameDay);
      return durationModel;

    }
    
//    if Start Day and end day is not same.
    
    Duration totalDuration = Duration.ZERO;
    for (OffsetDateTime dayDateTime = starDateTime; !dayDateTime.toLocalDate()
        .isAfter(endDateTime.toLocalDate()); dayDateTime = dayDateTime.plus(1, ChronoUnit.DAYS)) {

      DayOfWeek currentDayOfWeek = dayDateTime.getDayOfWeek();

      WorkDayModel workDay = dayOfWeekWorkDayMap.get(currentDayOfWeek);

      Duration calculatedDuration = workDay.getDuration();
      if (!workDay.isWorkingDay() || holidayLocalDateMap.get(localStartDate)!=null) {
        calculatedDuration = Duration.ZERO;
      } else if (isStartDay(starDateTime, dayDateTime)) {

        calculatedDuration = workDay.getSameDayDurationByStartTime(starDateTime.toOffsetTime());

      } else if (isEndDay(endDateTime, dayDateTime)) {
        calculatedDuration = workDay.getSameDayDurationByEndTime(endDateTime.toOffsetTime());
      }

      totalDuration = totalDuration.plus(calculatedDuration);

    }
    durationModel.setAll(totalDuration);

    return durationModel;
  }


  private boolean isEndDay(OffsetDateTime endDateTime, OffsetDateTime dayDateTime) {
    return dayDateTime.toLocalDate().isEqual(endDateTime.toLocalDate());
  }


  private boolean isStartDay(OffsetDateTime starDateTime, OffsetDateTime dayDateTime) {
    return isEndDay(starDateTime, dayDateTime);
  }


  private boolean isSameDay(LocalDate localStartDate, LocalDate localEndDate) {
    return localStartDate.equals(localEndDate);
  }


  private Duration getDurationForSameDay(OffsetTime startTime, OffsetTime endTime,
      WorkDayModel workDayModel) {
    DurationModel durationModel = new DurationModel();
    Duration duration = Duration.between(workDayModel.getAdjustedStartTime(startTime),
        workDayModel.getAdjustedEndTime(endTime));
    return duration.isNegative() ? Duration.ZERO : duration;

  }


}
