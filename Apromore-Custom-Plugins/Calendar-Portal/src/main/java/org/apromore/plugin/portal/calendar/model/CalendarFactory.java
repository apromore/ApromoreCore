/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.HolidayModel;
import org.apromore.calendar.model.HolidayType;
import org.apromore.calendar.model.WorkDayModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

public enum CalendarFactory {
    INSTANCE;

    private static final ModelMapper modelMapper = new ModelMapper();
    static {
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public Calendar emptyCalendar() {
        return new Calendar();
    }

    public Calendar fromCalendarModel(@NonNull  CalendarModel model) {
        return modelMapper.map(model, Calendar.class);
    }

    public List<Calendar> fromCalendarModels(@NonNull List<CalendarModel> models) {
        return models.stream().map(this::fromCalendarModel).collect(Collectors.toList());
    }

    public WorkDay createDefaultWorkDay(@NonNull DayOfWeek doW) {
        return new WorkDay(doW, LocalTime.of(9, 0), LocalTime.of(17, 0), true);
    }

    public Holiday createHoliday(@NonNull HolidayType type, @NonNull String name,
                                 @NonNull String description, @NonNull LocalDate holidayDate) {
        return new Holiday(type, name, description, holidayDate);
    }

    public WorkDayModel toWorkDayModel(WorkDay workDay) {
        return modelMapper.map(workDay, WorkDayModel.class);
    }

    public List<WorkDayModel> toWorkDayModels(List<WorkDay> workDays) {
        return workDays.stream().map(this::toWorkDayModel).collect(Collectors.toList());
    }

    public HolidayModel toHolidayModel(Holiday holiday) {
        return modelMapper.map(holiday, HolidayModel.class);
    }

    public List<HolidayModel> toHolidayModels(List<Holiday> holidays) {
        return holidays.stream().map(this::toHolidayModel).collect(Collectors.toList());
    }
}
