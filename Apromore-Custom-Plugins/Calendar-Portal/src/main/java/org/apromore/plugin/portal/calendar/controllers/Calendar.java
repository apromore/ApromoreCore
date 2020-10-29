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
package org.apromore.plugin.portal.calendar.controllers;

import java.util.Comparator;
import java.lang.Object;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModels;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Span;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.spring.SpringUtil;

import org.apromore.dao.model.Group;
import org.apromore.dao.model.Group.Type;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.manager.client.ManagerService;
import org.apromore.service.AuthorizationService;
import org.apromore.service.SecurityService;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.model.GroupAccessType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.common.access.Assignee;
import org.apromore.portal.common.access.Assignment;
import org.apromore.util.AccessType;

import org.apromore.commons.datetime.TimeUtils;
import org.apromore.plugin.portal.calendar.*;


/**
 * Controller for handling calendar interface
 * Corresponds to calendar.zul
 */
public class Calendar extends SelectorComposer<Window> {

    /**
     * For searching time zone id
     */
    private Comparator zoneComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            String input = (String) o1;
            Zone zone = (Zone) o2;
            return zone.getDisplayName().toLowerCase().contains(input.toLowerCase()) ? 0 : 1;
        }
    };

    private static Logger LOGGER = LoggerFactory.getLogger(Calendar.class);
    private static final OffsetTime DEFAULT_START_TIME = OffsetTime.of(LocalTime.of(9, 0), ZoneOffset.UTC);
    private static final OffsetTime DEFAULT_END_TIME = OffsetTime.of(LocalTime.of(17, 0), ZoneOffset.UTC);

    @Wire("#actionBridge")
    Div actionBridge;

    @Wire("#dayOfWeekListbox")
    Listbox dayOfWeekListbox;

    @Wire("#holidayListbox")
    Listbox holidayListbox;

    @Wire("#zoneCombobox")
    Combobox zoneCombobox;

    // FIXME:
    // Replace with models from Apromore Calendar
    @Getter @Setter private List<OffsetTime> dayOfWeekStartTimes;
    @Getter @Setter private List<OffsetTime> dayOfWeekEndTimes;
    @Getter @Setter private List<LocalDate> holidayDates;
    @Getter @Setter private List<String> holidayDescriptions;
    @Getter @Setter private List<String> holidayTypes;

    private ListModelList<DayOfWeekItem> dayOfWeekListModel;
    private ListModelList<HolidayItem> holidayListModel;
    private ListModelList<Zone> zoneModel;
    Map<DayOfWeek, DayOfWeekItem> dayOfWeekMap;
    Map<LocalDate, HolidayItem> holidayMap;

    private Window mainWindow;

    public Calendar() throws Exception {
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        mainWindow = win;

        populateTimeZone();

        actionBridge.addEventListener("onLoaded", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                initialize();
                Clients.evalJavaScript("Ap.calendar.rebuild()");
            }
        });

        actionBridge.addEventListener("onEditRange", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                JSONObject params = (JSONObject) event.getData();
                int dowIndex = (Integer) params.get("dow");
                int index = (Integer)params.get("index");
                int startHour = (Integer)params.get("startHour");
                int startMin = (Integer)params.get("startMin");
                int endHour = (Integer)params.get("endHour");
                int endMin = (Integer) params.get("endMin");
                Date start = TimeUtils.localDateAndTimeToDate(Constants.LOCAL_DATE_REF, startHour, startMin);
                Date end = TimeUtils.localDateAndTimeToDate(Constants.LOCAL_DATE_REF, endHour, endMin);
                editWorkday(dowIndex, index, start, end);
            }
        });

        actionBridge.addEventListener("onUpdateWorkday", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                JSONObject params = (JSONObject) event.getData();
                int dowIndex = (Integer)params.get("dow");
                Boolean workday = (Boolean)params.get("workday");
                DayOfWeekItem dowItem = getDayOfWeekItem(dowIndex);
                dowItem.setWorkday(workday);
                refresh(dowItem);
                Clients.evalJavaScript("Ap.calendar.buildRow(" + dowIndex + ")");
            }
        });

        actionBridge.addEventListener("onUpdateRanges", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                List<TimeRange> ranges = new ArrayList<TimeRange>();
                JSONObject params = (JSONObject) event.getData();
                int dowIndex = (Integer)params.get("dow");
                JSONArray rangeArray = (JSONArray) params.get("ranges");
                for (Object range: rangeArray) {
                    JSONObject item = (JSONObject) range;
                    int startHour = (Integer)item.get("startHour");
                    int startMin = (Integer)item.get("startMin");
                    int endHour = (Integer)item.get("endHour");
                    int endMin = (Integer) item.get("endMin");
                    ranges.add(new TimeRange(
                            OffsetTime.from(OffsetTime.of(LocalTime.of(startHour, startMin), ZoneOffset.UTC)),
                            OffsetTime.from(OffsetTime.of(LocalTime.of(endHour, endMin), ZoneOffset.UTC))
                    ));
                }
                DayOfWeekItem dowItem = getDayOfWeekItem(dowIndex);
                dowItem.setRanges(ranges);
                refresh(dowItem);
                Clients.evalJavaScript("Ap.calendar.buildRow(" + dowIndex + ")");
            }
        });
    }

    public void populateTimeZone() {
        zoneModel = new ListModelList<Zone>();
        Set<String> zoneIds = ZoneId.getAvailableZoneIds();
        for (String id : zoneIds) {
            ZoneId zoneId = ZoneId.of(id);
            zoneModel.add(new Zone(zoneId.getId(), zoneId.getDisplayName(TextStyle.FULL, Locale.US)));
        }
        zoneModel.setMultiple(false);
        zoneCombobox.setModel(ListModels.toListSubModel(zoneModel, zoneComparator, 20));
    }

    public DayOfWeekItem getDayOfWeekItem(int dowIndex) {
        DayOfWeek dow = DayOfWeek.of(dowIndex);
        return dayOfWeekMap.get(dow);
    }

    public void refresh(DayOfWeekItem dowItem) {
        int index = dayOfWeekListModel.indexOf(dowItem);
        dayOfWeekListModel.set(index, dowItem); // trigger change
    }

    public void editWorkday(int dowIndex, int index, Date start, Date end) {
        try {
            Map arg = new HashMap<>();
            arg.put("parentController", this);
            arg.put("dowIndex", dowIndex);
            arg.put("index", index);
            arg.put("start", start);
            arg.put("end", end);
            Window window = (Window) Executions.getCurrent().createComponents("calendar/zul/edit-range.zul", getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create add holidays dialog", e);
        }
    }

    @Listen("onUpdateHolidayDescription = #holidayListbox")
    public void onUpdateHolidayDescription(ForwardEvent event) throws Exception {
        InputEvent inputEvent = (InputEvent) event.getOrigin();
        Textbox textbox = (Textbox) inputEvent.getTarget();
        String description = (String) inputEvent.getValue();
        Listitem listItem = (Listitem)textbox.getParent().getParent();
        HolidayItem holiday = (HolidayItem)listItem.getValue();
        holiday.setDescription(description);
    }

    @Listen("onUpdateHolidayDate = #holidayListbox")
    public void onUpdateHolidayDate(ForwardEvent event) throws Exception {
        InputEvent inputEvent = (InputEvent) event.getOrigin();
        Datebox datebox = (Datebox) inputEvent.getTarget();
        Object val = (Object) inputEvent.getValue();
        Date date;
        if (val instanceof Date) {
            date = (Date)val;
        } else {
            date = new SimpleDateFormat("MMM dd").parse((String)val);
        }
        LocalDate holidayDate = TimeUtils.dateToLocalDate(date);
        Listitem listItem = (Listitem)datebox.getParent().getParent();
        HolidayItem holiday = (HolidayItem)listItem.getValue();
        if (holidayMap.containsKey(holidayDate)) {
            Date oldDate = TimeUtils.localDateToDate(holiday.getHolidayDate());
            datebox.setValue(oldDate);
        } else {
            holiday.setHolidayDate(holidayDate);
            holidayMap.remove(holidayDate);
            holidayMap.put(holidayDate, holiday);
        }
    }

    @Listen("onRemoveHoliday = #holidayListbox")
    public void onRemoveHoliday(final Event event) {
        try {
            HolidayItem holiday = (HolidayItem)event.getData();
            removeHoliday(holiday);
        } catch (Exception e) {
            LOGGER.error("Unable to create remove a holiday", e);
        }
    }

    @Listen("onClick = #importHolidaysBtn")
    public void onClickImportHolidaysBtn() {
        try {
            Map arg = new HashMap<>();
            arg.put("country", "Australia");
            arg.put("parentController", this);
            Window window = (Window) Executions.getCurrent().createComponents("calendar/zul/import-holidays.zul", getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create add holidays dialog", e);
        }
    }

    @Listen("onClick = #addHolidayBtn")
    public void onClickAddHolidayBtn() {
        try {
            Map arg = new HashMap<>();
            arg.put("parentController", this);
            Window window = (Window) Executions.getCurrent().createComponents("calendar/zul/add-holiday.zul", getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create add holidays dialog", e);
        }
    }

    public void updateRange(int dowIndex, int index, int startHour, int startMin, int endHour, int endMin) {
        String cmd = String.format("Ap.calendar.updateRange(%d, %d, %d, %d, %d, %d)", dowIndex, index, startHour, startMin, endHour, endMin);
        Clients.evalJavaScript(cmd);
    }

    public void deleteRange(int dowIndex, int index) {
        String cmd = String.format("Ap.calendar.deleteRange(%d, %d)", dowIndex, index);
        Clients.evalJavaScript(cmd);
    }

    public void addHoliday(HolidayItem holiday) {
        LocalDate holidayDate = holiday.getHolidayDate();
        if (!holidayMap.containsKey(holidayDate)) {
            holidayListModel.add(holiday);
            holidayMap.put(holidayDate, holiday);
        }
    }

    public void removeHoliday(HolidayItem holiday) {
        LocalDate holidayDate = holiday.getHolidayDate();
        holidayListModel.remove(holiday);
        holidayMap.remove(holidayDate);
    }

    public void addHolidays(List<HolidayItem> holidays) {
        for (HolidayItem holiday: holidays) {
            addHoliday(holiday);
        }
    }

    public void initialize() {
        dayOfWeekListModel = new ListModelList<DayOfWeekItem>();
        dayOfWeekMap = new HashMap<DayOfWeek, DayOfWeekItem>();
        holidayListModel = new ListModelList<HolidayItem>();
        holidayMap = new HashMap<LocalDate, HolidayItem>();

        mock();
        dayOfWeekListbox.setModel(dayOfWeekListModel);
        holidayListbox.setModel(holidayListModel);
    }

    private void fromModels() {
        // FIXME
        // transfer from Apromore Calendar's models to ZK ListModels
        // Currently use Lists

        for (int i = 1; i < 8; i++) {
            List<TimeRange> ranges = new ArrayList<>();
            ranges.add(new TimeRange(dayOfWeekStartTimes.get(i - 1), dayOfWeekEndTimes.get(i - 1)));
            DayOfWeek dow = DayOfWeek.of(i);
            DayOfWeekItem dowItem = new DayOfWeekItem(dow, true, ranges);
            dayOfWeekListModel.add(dowItem);
            dayOfWeekMap.put(dow, dowItem);
        }
        for (int j = 0; j < holidayDates.size(); j++) {
            LocalDate holidayDate = holidayDates.get(j);
            String holidayDescription = holidayDescriptions.get(j);
            String holidayType = holidayTypes.get(j);
            HolidayItem holiday = new HolidayItem(holidayDate, holidayDescription, holidayType);
            holidayListModel.add(holiday);
            holidayMap.put(holidayDate, holiday);
        }
    }

    private void toModels() {
        // transfer ZK ListModels to Apromore Calendar's models
    }

    private void mock() {
        for (int i = 1; i < 8; i++) {
            List<TimeRange> ranges = new ArrayList<>();
            ranges.add(new TimeRange(OffsetTime.from(DEFAULT_START_TIME), OffsetTime.from(DEFAULT_END_TIME)));
            DayOfWeek dow = DayOfWeek.of(i);
            DayOfWeekItem dowItem = new DayOfWeekItem(dow, true, ranges);
            dayOfWeekListModel.add(dowItem);
            dayOfWeekMap.put(dow, dowItem);
        }
        // holidayListModel.add(new HolidayItem(LocalDate.parse("2020-12-25"), "Christmas"));
        // holidayListModel.add(new HolidayItem(LocalDate.parse("2020-01-01"), "New Year Day"));
    }

    @Listen("onClick = #applyBtn")
    public void onClickApplyBtn() {
        toModels();
        getSelf().detach();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelBtn() {
        getSelf().detach();
    }

}