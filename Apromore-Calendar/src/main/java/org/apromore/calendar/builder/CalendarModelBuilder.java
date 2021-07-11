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
package org.apromore.calendar.builder;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.WorkDayModel;

public class CalendarModelBuilder {

  CalendarModel model = new CalendarModel();

  public CalendarModel build() {

    return model;
  }

  public CalendarModelBuilder withWorkDay(DayOfWeek dayOfWeek, OffsetTime starOffsetTime,
      OffsetTime endOffsetTime, boolean isWorkingDay) {

    WorkDayModel workDayModel = new WorkDayModel();
    workDayModel.setDayOfWeek(dayOfWeek);
    workDayModel.setStartTime(starOffsetTime);
    workDayModel.setEndTime(endOffsetTime);
    workDayModel.setWorkingDay(isWorkingDay);
    workDayModel.setDuration(Duration.between(starOffsetTime, endOffsetTime));
    model.getWorkDays().add(workDayModel);
    return this;
  }
  
  public CalendarModelBuilder withAllDayAllTime() {
      for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
          withWorkDay(dayOfWeek, OffsetTime.of(0, 0, 0, 0, ZoneOffset.of(model.getZoneId())),
                  OffsetTime.of(23, 59, 59, 0, ZoneOffset.of(model.getZoneId())), true);
      }
      return this;
  }

  public CalendarModelBuilder withWork9to5Day(DayOfWeek dayOfWeek) {

    return withWorkDay(dayOfWeek, OffsetTime.of(9, 0, 0, 0, ZoneOffset.of(model.getZoneId())),
        OffsetTime.of(5, 0, 0, 0, ZoneOffset.of(model.getZoneId())), true);

  }

  public CalendarModelBuilder withNotWorkDay(DayOfWeek dayOfWeek) {

    return withWorkDay(dayOfWeek, OffsetTime.of(9, 0, 0, 0, ZoneOffset.of(model.getZoneId())),
        OffsetTime.of(5, 0, 0, 0, ZoneOffset.of(model.getZoneId())), false);

  }
  
  public CalendarModelBuilder withZoneId(String zoneId) {
    model.setZoneId(zoneId);
    for (WorkDayModel workDay : model.getWorkDays()) {
        workDay.setStartTime(workDay.getStartTime().withOffsetSameInstant(ZoneOffset.of(zoneId)));
        workDay.setEndTime(workDay.getEndTime().withOffsetSameInstant(ZoneOffset.of(zoneId)));
    }
    return this;
  }

  public CalendarModelBuilder with7DayWorking() {

    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      withWorkDay(dayOfWeek, OffsetTime.of(9, 0, 0, 0, ZoneOffset.of(model.getZoneId())),
          OffsetTime.of(17, 0, 0, 0, ZoneOffset.of(model.getZoneId())), true);

    }
    return this;
  }

  public CalendarModelBuilder with5DayWorking() {

    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      boolean isWorking = true;
      if (dayOfWeek.equals(DayOfWeek.SATURDAY) || dayOfWeek.equals(DayOfWeek.SUNDAY)) {
        isWorking = false;
      }

      withWorkDay(dayOfWeek, OffsetTime.of(9, 0, 0, 0, ZoneOffset.of(model.getZoneId())),
          OffsetTime.of(17, 0, 0, 0, ZoneOffset.of(model.getZoneId())), isWorking);

    }
    return this;
  }

//


}
