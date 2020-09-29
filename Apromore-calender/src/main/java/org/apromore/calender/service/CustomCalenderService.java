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
package org.apromore.calender.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.apromore.calender.exception.CalenderAlreadyExistsException;
import org.apromore.calender.util.CalenderUtil;
import org.apromore.dao.CustomCalenderRepository;
import org.apromore.dao.model.CustomCalender;
import org.apromore.dao.model.WorkDay;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.Data;

@Data
public class CustomCalenderService {

  @Autowired
  public CustomCalenderRepository calenderRepo;

  public Long createGenericCalender(String description, boolean weekendsOff)
      throws CalenderAlreadyExistsException {


    OffsetTime startTime = OffsetTime.of(LocalTime.MIN, ZoneOffset.UTC);
    OffsetTime endTime = OffsetTime.of(LocalTime.MAX, ZoneOffset.UTC);


    return createCalender(description, weekendsOff, startTime, endTime);

  }

  public Long createBusinessCalender(String description, boolean weekendsOff)
      throws CalenderAlreadyExistsException {

    OffsetTime startTime = OffsetTime.of(LocalTime.of(9, 0), ZoneOffset.UTC);
    OffsetTime endTime = OffsetTime.of(LocalTime.of(5, 0), ZoneOffset.UTC);

    return createCalender(description, weekendsOff, startTime, endTime);

  }



  private Long createCalender(String description, boolean weekendsOff, OffsetTime start,
      OffsetTime end)
      throws CalenderAlreadyExistsException {

    validateCalenderExists(calenderRepo.findByDescription(description));

    final CustomCalender calender = new CustomCalender(description);

    for(WorkDay workDay : getWorkDays(start, end, weekendsOff))
    {
        calender.addWorkDay(workDay);
    }
    CustomCalender newcalender = calenderRepo.saveAndFlush(calender);
    return newcalender.getId();


  }

  private List<WorkDay> getWorkDays(OffsetTime start, OffsetTime end, boolean weekendOff) {

    Predicate<DayOfWeek> isWeekendOff = CalenderUtil.getWeekendOffPRedicate(weekendOff);
    List<WorkDay> workDaysList = new ArrayList<WorkDay>();
    
    for(DayOfWeek dayOfWeek : DayOfWeek.values())
    {
      workDaysList.add(new WorkDay(dayOfWeek, start, end, isWeekendOff.test(dayOfWeek)));
    }
    return workDaysList;

  }

  


  public CustomCalender getCalender(Long id) {

    return calenderRepo.findById(id);

  }

  private void validateCalenderExists(CustomCalender calender)
      throws CalenderAlreadyExistsException {

    if (calender != null) {
      throw new CalenderAlreadyExistsException("Calender already exists");
    }

  }

}
