package org.apromore.calender.builder;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import org.apromore.calendar.model.CalenderModel;
import org.apromore.calendar.model.WorkDayModel;

public class CalenderModelBuilder {

  CalenderModel model = new CalenderModel();

  public CalenderModel build() {

    return model;
  }

  public CalenderModelBuilder withWorkDay(DayOfWeek dayOfWeek, OffsetTime starOffsetTime,
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

  public CalenderModelBuilder withWork9to5Day(DayOfWeek dayOfWeek) {

    return withWorkDay(dayOfWeek, OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
        OffsetTime.of(5, 0, 0, 0, ZoneOffset.UTC), true);

  }

  public CalenderModelBuilder withNotWorkDay(DayOfWeek dayOfWeek) {

    return withWorkDay(dayOfWeek, OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
        OffsetTime.of(5, 0, 0, 0, ZoneOffset.UTC), false);

  }
  
  public CalenderModelBuilder with7DayWorking() {

    for(DayOfWeek dayOfWeek : DayOfWeek.values())
    {
     withWorkDay(dayOfWeek, OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
        OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC), true);

    }
    return this;
  }
  
  

}
