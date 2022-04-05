/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
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
import org.modelmapper.convention.MatchingStrategies;

public enum CalendarFactory {
    INSTANCE;

    private static final ModelMapper modelMapper = new ModelMapper();
    static {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
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
