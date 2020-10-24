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
package org.apromore.portal.common.calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.DayOfWeek;
import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.ToClientCommand;
import org.zkoss.bind.annotation.ToServerCommand;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.NotifyCommand;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.BindUtils;

import org.zkoss.zk.ui.util.Clients;

import org.apromore.portal.common.calendar.CalendarItem;

@NotifyCommand(value="doItemListChange", onChange="_vm_.itemList")
@ToServerCommand({"applyChanges", "dummy", "updateDate", "updateItem", "removeItem", "updateEndTime", "updateStartTime", "updateStartTimeExt"})
public class CalendarViewModel {

    static final ZoneOffset DEFAULT_ZONE_OFFSET = OffsetDateTime.now().getOffset();

    static final OffsetTime DEFAULT_START_TIME = OffsetTime.of(LocalTime.of(9, 0), DEFAULT_ZONE_OFFSET);
    static final OffsetTime DEFAULT_END_TIME = OffsetTime.of(LocalTime.of(17, 0), DEFAULT_ZONE_OFFSET);

    static final String WEEKDAY_FORMAT = "EEEE";
    static final String HOLIDAY_FORMAT = "E, yyyy MMM dd";
    static final SimpleDateFormat TIME_PARSER = new SimpleDateFormat("HH:mm");

    static DateTimeFormatter weekdayFormatter = DateTimeFormatter.ofPattern(WEEKDAY_FORMAT);
    static DateTimeFormatter holidayFormatter = DateTimeFormatter.ofPattern(HOLIDAY_FORMAT);
    static final LocalDate NOW = LocalDate.now();

    @Init
    public void init(@ExecutionArgParam("type") String type){
        System.out.println(type);
    }

    public Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Date localDateOffsetTimeToDate(LocalDate localDate, OffsetTime offsetTime) {
        OffsetDateTime offsetDateTime = localDate.atTime(offsetTime);
        long epochMilliseconds = offsetDateTime.toInstant().toEpochMilli();
        return new Date(epochMilliseconds);
    }

    private CalendarItem createWeekday(int i) {
        LocalDate localDate = NOW.with(TemporalAdjusters.next(DayOfWeek.of(i)));
        String label = localDate.format(weekdayFormatter);
        Date date = localDateToDate(localDate);
        Date startTime = localDateOffsetTimeToDate(localDate, weekdayStartTimes.get(i - 1));
        Date endTime = localDateOffsetTimeToDate(localDate, weekdayEndTimes.get(i - 1));
        return new CalendarItem(label, false, date, WEEKDAY_FORMAT, startTime, endTime);
    }

    private CalendarItem createHoliday(LocalDate localDate) {
        String label = localDate.format(holidayFormatter);
        Date date = localDateToDate(localDate);
        return new CalendarItem(label, true, date, HOLIDAY_FORMAT, null, null);
    }

    @Getter @Setter private List<OffsetTime> weekdayStartTimes;
    @Getter @Setter private List<OffsetTime> weekdayEndTimes;
    @Getter @Setter private List<LocalDate> holidays;

    @Getter @Setter private List<CalendarItem> itemList;

    public CalendarViewModel() {
        initialize();
    }

    @NotifyChange("itemList")
    public void initialize() {
        generateMock();
        itemList = new ArrayList<CalendarItem>();

        // Add workdays
        for (int i = 1; i < 8; i++) {
            itemList.add(createWeekday(i));
        }

        // Add holidays
        for (LocalDate holiday: holidays) {
            itemList.add(createHoliday(holiday));
        }
        addBlank();
        initHours();
    }

    private void addBlank() {
        itemList.add(new CalendarItem("", true, null, HOLIDAY_FORMAT, null, null));
    }

    private void generateMock() {
        weekdayStartTimes = Collections.nCopies(7, OffsetTime.from(DEFAULT_START_TIME));
        weekdayEndTimes = Collections.nCopies(7, OffsetTime.from(DEFAULT_END_TIME));
        holidays = Arrays.asList(LocalDate.parse("2020-12-25"), LocalDate.parse("2021-01-01"));
    }

    @Command
    public void initHours() {
        // dummy to trigger update
    }

    private Date toTime(LocalDate localDate, int hour, int min) {
        OffsetTime offsetTime = OffsetTime.of(LocalTime.of(hour, min), DEFAULT_ZONE_OFFSET);
        return localDateOffsetTimeToDate(localDate, offsetTime);
    }

    private void updateWeekday(
            CalendarItem item,
            int startHour,
            int startMin,
            int endHour,
            int endMin
    ) {
        Date date = item.getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (startHour >= 0) {
            Date startTime = toTime(localDate, startHour, startMin);
            item.setStartTime(startTime);
        }
        if (endHour  >= 0) {
            Date endTime = toTime(localDate, endHour, endMin);
            item.setEndTime(endTime);
        }
    }

    @Command
    @NotifyChange("itemList")
    public void dummy() {
        System.out.println("dummy");
    }

    @Command
    @NotifyChange("itemList")
    public void removeItem(@BindingParam("item") CalendarItem item) {
        System.out.println(item.getLabel());
        itemList.remove(item);
        Clients.evalJavaScript("Ap.calendar.rebuild()");
    }

    @Command
    @NotifyChange("itemList")
    public void updateItem(
            @BindingParam("row") int row,
            @BindingParam("startHour") int startHour,
            @BindingParam("startMin") int startMin,
            @BindingParam("endHour") int endHour,
            @BindingParam("endMin") int endMin
        ) {
        CalendarItem item = itemList.get(row);
        updateWeekday(item, startHour, startMin, endHour, endMin);
        // BindUtils.postNotifyChange(null, null, CalendarViewModel.this, "itemList");
        Clients.evalJavaScript("Ap.calendar.updateRow(" + row + "," + startHour + ","  +  endHour + ")");
    }

    @Command
    @NotifyChange("itemList")
    public void updateDate(@BindingParam("item") CalendarItem item) {
        int index = itemList.indexOf(item);
        int size = itemList.size();
        if (item.getDate() != null && index == size - 1) {
            addBlank();
        }
        Clients.evalJavaScript("Ap.calendar.rebuild()");
    }

    @Command
    @NotifyChange("itemList")
    public void updateStartTime(@BindingParam("item") CalendarItem item, @BindingParam("date") String startTime) {
        try {
            Date date = TIME_PARSER.parse(startTime);
            int startHour = date.getHours();
            int startMin = date.getMinutes();
            updateWeekday(item, startHour, startMin, -1, -1);
            int index = itemList.indexOf(item);
            int endHour = item.getEndTime().getHours();
            Clients.evalJavaScript("Ap.calendar.updateRow(" + index + "," + startHour + ","  +  endHour + ")");
        } catch(Exception e) {
            // pass
        }
    }

    @Command
    @NotifyChange("itemList")
    public void updateEndTime(@BindingParam("item") CalendarItem item, @BindingParam("date") String endTime) {
        try {
            Date date = TIME_PARSER.parse(endTime);
            int endHour = date.getHours();
            int endMin = date.getMinutes();
            updateWeekday(item, -1, -1, endHour, endMin);
            int index = itemList.indexOf(item);
            int startHour = item.getStartTime().getHours();
            Clients.evalJavaScript("Ap.calendar.updateRow(" + index + "," + startHour + ","  +  endHour + ")");
        } catch(Exception e) {
            // pass
        }
    }

    @Command
    @NotifyChange("itemList")
    public void updateStartTimeExt(@BindingParam("item") CalendarItem item) {
        System.out.println(item.getStartTime());
        itemList = new ArrayList<CalendarItem>(itemList);
    }

    @Command
    public void applyChanges(){
        System.out.println("applyChanges");
    }
}
