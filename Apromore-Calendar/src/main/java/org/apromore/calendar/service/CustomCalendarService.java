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

package org.apromore.calendar.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.transaction.Transactional;
import lombok.Setter;
import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.exception.CalendarNotExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.Calendars;
import org.apromore.calendar.model.HolidayModel;
import org.apromore.calendar.model.WorkDayModel;
import org.apromore.calendar.util.CalendarUtil;
import org.apromore.commons.mapper.CustomMapper;
import org.apromore.dao.CustomCalendarInfoRepository;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.HolidayRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.model.CustomCalendar;
import org.apromore.dao.model.CustomCalendarInfo;
import org.apromore.dao.model.HOLIDAYTYPE;
import org.apromore.dao.model.Holiday;
import org.apromore.dao.model.WorkDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("calendarService")
@Transactional
public class CustomCalendarService implements CalendarService {

    @Autowired
    public CustomCalendarRepository calendarRepo;

    @Autowired
    public CustomCalendarInfoRepository calendarInfoRepo;

    @Autowired
    public HolidayRepository holidayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("customMapper")
    @Setter
    private CustomMapper modelMapper;

    @Override
    public CalendarModel createGenericCalendar(String desc, String username, boolean weekendsOff, String zoneId)
        throws CalendarAlreadyExistsException {
        CustomCalendar customCalendar = createCalendar(desc, username, weekendsOff, LocalTime.MIN, LocalTime.MAX);
        CalendarModel calendarModel = modelMapper.getMapper().map(customCalendar, CalendarModel.class);
        return calendarModel;
    }

    // Return an absolute calendar object to calculate duration by the diff between start/end
    // for performance efficiency.
    @Override
    public CalendarModel getGenericCalendar() {
        return Calendars.INSTANCE.absoluteCalendar();
    }

    @Override
    public CalendarModel createBusinessCalendar(String description, String username, boolean weekendsOff, String zoneId)
        throws CalendarAlreadyExistsException {

        CustomCalendar customCalendar = createCalendar(description, username, weekendsOff,
            LocalTime.of(9, 0), LocalTime.of(17, 0));
        CalendarModel calendarModel = modelMapper.getMapper().map(customCalendar, CalendarModel.class);

        return calendarModel;

    }

    @Override
    public CalendarModel getCalendar(Long id) {

        return modelMapper.getMapper().map(calendarRepo.findById(id).orElse(null), CalendarModel.class);

    }

    @Override
    public List<CalendarModel> getCalendars() {

        List<CustomCalendarInfo> calendarInfos = calendarInfoRepo.findAll();
        List<CalendarModel> calendarModels = new ArrayList<CalendarModel>();

        for (CustomCalendarInfo info : calendarInfos) {
            calendarModels.add(modelMapper.getMapper().map(info, CalendarModel.class));
        }

        return calendarModels;

    }

    @Override
    public List<CalendarModel> getCalendars(String username) {
        Set<CustomCalendar> calendars = calendarRepo.findByUser(userRepository.findByUsername(username));
        List<CalendarModel> calendarModels = new ArrayList<>();

        for (CustomCalendar c : calendars) {
            calendarModels.add(modelMapper.getMapper().map(c, CalendarModel.class));
        }

        return calendarModels;
    }

    private CustomCalendar createCalendar(String description, String username, boolean weekendsOff, LocalTime start,
                                          LocalTime end)
        throws CalendarAlreadyExistsException {

        String name = getUniqueCalendarName(description);

        final CustomCalendar calendar = new CustomCalendar(name);
        calendar.setCreatedBy(username);
        calendar.setUser(userRepository.findByUsername(username));
        for (WorkDay workDay : getWorkDays(start, end, weekendsOff)) {
            calendar.addWorkDay(workDay);
        }
        CustomCalendar newcalendar = calendarRepo.saveAndFlush(calendar);
        return newcalendar;

    }

    private List<WorkDay> getWorkDays(LocalTime start, LocalTime end, boolean weekendOff) {

        Predicate<DayOfWeek> isWeekendOff = CalendarUtil.getWeekendOffPRedicate(weekendOff);
        List<WorkDay> workDaysList = new ArrayList<WorkDay>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            workDaysList.add(new WorkDay(dayOfWeek, start, end, !isWeekendOff.test(dayOfWeek)));
        }
        return workDaysList;

    }

    @Override
    public void updateHoliday(Long id, List<HolidayModel> holidayModels) throws CalendarNotExistsException {
        CustomCalendar calendar = getExistingCalendar(id);

        List<Holiday> holidays = new ArrayList<Holiday>();
        for (HolidayModel h : holidayModels) {
            holidays.add(new Holiday(h.getId(), h.getName(), h.getDescription(), h.getHolidayDate(),
                HOLIDAYTYPE.valueOf(h.getHolidayType().getLabel())));
        }

        calendar.getHolidays().clear();
        for (Holiday holiday : holidays) {
            calendar.addHoliday(holiday);
        }
        calendarRepo.saveAndFlush(calendar);

    }

    private CustomCalendar getExistingCalendar(Long id) throws CalendarNotExistsException {
        CustomCalendar calendar = calendarRepo.findById(id).orElse(null);

        if (calendar == null) {
            throw new CalendarNotExistsException("calendar does not exist");
        }
        return calendar;
    }

    public void removeHoliday(Long id, List<Long> holidayIds) {

        List<Holiday> holidays = new ArrayList<Holiday>();
        // Bad way to delete, but we need to upgrade jpa of springs to make use of
        // better ways of delete
        for (Long idLong : holidayIds) {
            holidayRepository.deleteById(idLong);

        }

    }

    @Override
    public void deleteCalendar(Long calendarId) {
        calendarRepo.deleteById(calendarId);

    }

    @Override
    public void updateCalendarName(Long id, String calendarName) throws CalendarNotExistsException {
        CustomCalendar calendar = getExistingCalendar(id);

        calendar.setName(calendarName);

        calendarRepo.save(calendar);

    }

    @Override
    public void updateWorkDays(Long id, List<WorkDayModel> workDayModels) throws CalendarNotExistsException {
        CustomCalendar calendar = getExistingCalendar(id);

        List<WorkDay> workDays = new ArrayList<WorkDay>();
        for (WorkDayModel w : workDayModels) {

            workDays.add(new WorkDay(w.getId(), w.getDayOfWeek(), w.getStartTime(), w.getEndTime(), w.isWorkingDay()));
        }

        calendar.getWorkDays().clear();
        for (WorkDay workDay : workDays) {
            calendar.addWorkDay(workDay);
        }
        calendarRepo.saveAndFlush(calendar);

    }

    @Override
    public void updateZoneInfo(Long id, String zoneId) throws CalendarNotExistsException {
        CustomCalendar calendar = getExistingCalendar(id);
        calendar.setZoneId(zoneId);
        calendarRepo.save(calendar);
    }

    private String getUniqueCalendarName(String name) throws CalendarAlreadyExistsException {
        int maxDuplicates = 1000;
        String duplicateNameFormat = "%s (%d)";

        String uniqueName = name;
        int duplicates = 0;

        while (calendarRepo.findByName(uniqueName) != null) {
            if (duplicates == maxDuplicates) {
                //Prevent endless loop if too many calendars are created with the same name
                throw new CalendarAlreadyExistsException("Calendar already exists");
            }
            uniqueName = String.format(duplicateNameFormat, name, ++duplicates);
        }

        return uniqueName;

    }

}
// public WorkDay(Long id,DayOfWeek dayOfWeek, OffsetTime startTime, OffsetTime endTime,
// boolean isWorkingDay) {
