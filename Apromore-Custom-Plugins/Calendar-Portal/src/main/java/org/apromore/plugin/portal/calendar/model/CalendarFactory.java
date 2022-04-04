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

    private final ModelMapper modelMapper = new ModelMapper();

    public List<Calendar> fromCalendars(@NonNull List<CalendarModel> models) {
        return models.stream().map(this::fromCalendar).collect(Collectors.toList());
    }

    public Calendar fromCalendar(@NonNull  CalendarModel model) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper.map(model, Calendar.class);
    }

    public WorkDay createDefaultWorkDay(@NonNull DayOfWeek doW) {
        return new WorkDay(doW, LocalTime.of(9, 0), LocalTime.of(17, 0), true);
    }

    public Holiday createHoliday(@NonNull HolidayType type, @NonNull String name,
                                 @NonNull String description, @NonNull LocalDate holidayDate) {
        return new Holiday(type, name, description, holidayDate);
    }

    public WorkDay fromWorkDay(WorkDayModel workDay) {
        return modelMapper.map(workDay, WorkDay.class);
    }

    public WorkDayModel toWorkDay(WorkDay workDay) {
        return modelMapper.map(workDay, WorkDayModel.class);
    }

    public List<WorkDay> fromWorkDays(List<WorkDayModel> workDays) {
        return workDays.stream().map(this::fromWorkDay).collect(Collectors.toList());
    }

    public List<WorkDayModel> toWorkDays(List<WorkDay> workDays) {
        return workDays.stream().map(this::toWorkDay).collect(Collectors.toList());
    }

    public List<HolidayModel> toHolidays(List<Holiday> holidays) {
        return null;
    }
}
