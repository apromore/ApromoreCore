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

package org.apromore.plugin.portal.calendar.controllers;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.apromore.calendar.exception.CalendarNotExistsException;
import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.datetime.TimeUtils;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.Constants;
import org.apromore.plugin.portal.calendar.model.Calendar;
import org.apromore.plugin.portal.calendar.model.CalendarFactory;
import org.apromore.plugin.portal.calendar.model.Holiday;
import org.apromore.plugin.portal.calendar.model.WorkDay;
import org.apromore.plugin.portal.calendar.model.Zone;
import org.apromore.plugin.portal.calendar.pageutil.PageUtils;
import org.apromore.zk.event.CalendarEvents;
import org.apromore.zk.label.LabelSupplier;
import org.slf4j.Logger;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModels;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Controller for handling calendar interface Corresponds to calendar.zul
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class CalendarController extends SelectorComposer<Window> implements LabelSupplier {

    @WireVariable("calendarService")
    CalendarService calendarService;

    private Calendar calendarModel;
    private Long calendarId;
    private boolean isNew;
    private boolean canEdit;
    private boolean calendarExists = false;

    /**
     * For searching time zone id.
     */
    private final Comparator zoneComparator = (Object o1, Object o2) -> {
        String input = (String) o1;
        Zone zone = (Zone) o2;
        return zone.getZoneDisplayName().toLowerCase().contains(input.toLowerCase()) ? 0 : 1;
    };

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(CalendarController.class);

    @Wire("#actionBridge")
    Div actionBridge;

    @Wire("#dayOfWeekListbox")
    Listbox dayOfWeekListbox;

    @Wire("#holidayListbox")
    Listbox holidayListbox;

    @Wire("#holidayCustomListbox")
    Listbox holidayCustomListbox;

    @Wire("#zoneCombobox")
    Combobox zoneCombobox;

    @Wire("#applyBtn")
    Button applyBtn;

    @Wire("#viewerWarning")
    Div viewerWarning;
    @Wire("#editHelp")
    Div editHelp;

    @Wire("#template9To5Btn")
    Button template9To5Btn;
    @Wire("#template24hBtn")
    Button template24hBtn;
    @Wire("#template24x7Btn")
    Button template24x7Btn;

    @Wire("#importHolidaysBtn")
    Button importHolidaysBtn;
    @Wire("#addHolidayBtn")
    Button addHolidayBtn;
    @Wire("#deleteHolidaysBtn")
    Button deleteHolidaysBtn;

    // FIXME:
    // Replace with models from Apromore Calendar
    @Getter
    @Setter
    private List<OffsetTime> dayOfWeekStartTimes;
    @Getter
    @Setter
    private List<OffsetTime> dayOfWeekEndTimes;
    @Getter
    @Setter
    private List<LocalDate> holidayDates;
    @Getter
    @Setter
    private List<String> holidayDescriptions;
    @Getter
    @Setter
    private List<String> holidayTypes;

    private ListModelList<WorkDay> dayOfWeekListModel;
    private ListModelList<Holiday> holidayListModel;
    private ListModelList<Holiday> holidayCustomListModel;
    private ListModelList<Zone> zoneModel;
    Zone selectedZone = null;

    EventQueue<Event> localCalendarEventQueue;

    @Override
    public String getBundleName() {
        return Constants.BUNDLE_NAME;
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);

        Long calId = (Long) Executions.getCurrent().getArg().get("calendarId");
        isNew = (boolean) Executions.getCurrent().getArg().get("isNew");
        canEdit = (boolean) Executions.getCurrent().getArg().get("canEdit");
        calendarExists = calId != null;
        calendarModel = !calendarExists ? CalendarFactory.INSTANCE.emptyCalendar()
            : CalendarFactory.INSTANCE.fromCalendarModel(calendarService.getCalendar(calId));
        calendarId = calendarModel.getId();
        localCalendarEventQueue = EventQueues.lookup(CalendarEvents.TOPIC + "LOCAL", EventQueues.DESKTOP,
            true);

        populateTimeZone();
        initialize();
        updateWidgets();

        win.setClientDataAttribute("readonly", String.valueOf(!canEdit));
        win.setTitle(getLabel("custom_calendar_text", "Custom Calendar") + " - " + calendarModel.getName());
        win.addEventListener("onClose", (Event event) -> onClickCancelBtn());

        actionBridge.addEventListener("onLoaded", (Event event) -> rebuild());
        actionBridge.addEventListener("onSyncRows", (Event event) -> syncRows());
        actionBridge.addEventListener("onEditRange", (Event event) -> {
            if (!canEdit) {
                return;
            }
            JSONObject params = (JSONObject) event.getData();
            int dowIndex = (Integer) params.get("dow");
            int index = (Integer) params.get("index");
            int startHour = (Integer) params.get("startHour");
            int startMin = (Integer) params.get("startMin");
            int endHour = (Integer) params.get("endHour");
            int endMin = (Integer) params.get("endMin");
            if (endHour == 24 && endMin == 0) {
                endHour = 23;
                endMin = 59;
            }
            Date start = TimeUtils.localDateAndTimeToDate(Constants.LOCAL_DATE_REF, startHour, startMin);
            Date end = TimeUtils.localDateAndTimeToDate(Constants.LOCAL_DATE_REF, endHour, endMin);
            editWorkday(dowIndex, index, start, end);
        });

        actionBridge.addEventListener("onUpdateWorkday", (Event event) -> {
            if (!canEdit) {
                return;
            }
            JSONObject params = (JSONObject) event.getData();
            int dowIndex = (Integer) params.get("dow");
            Boolean workday = (Boolean) params.get("workday");
            WorkDay dowItem = getDayOfWeekItem(dowIndex);
            dowItem.setWorkingDay(workday);
            refresh(dowItem);
            Clients.evalJavaScript("Ap.calendar.buildRow(" + dowIndex + ")");
        });

        actionBridge.addEventListener("onUpdateRanges", (Event event) -> {
            if (!canEdit) {
                return;
            }
            JSONObject params = (JSONObject) event.getData();
            int dowIndex = (Integer) params.get("dow");
            JSONArray rangeArray = (JSONArray) params.get("ranges");
            boolean workday = (boolean) params.get("workday");
            WorkDay dowItem = getDayOfWeekItem(dowIndex);
            dowItem.setWorkingDay(workday);
            int startHour = 0;
            int startMin = 0;
            int endHour = 0;
            int endMin = 0;
            // TO DO: WorkDay currently only support a single range
            if (rangeArray.size() > 0) {
                Object range = rangeArray.get(0);
                JSONObject item = (JSONObject) range;
                startHour = (Integer) item.get("startHour");
                startMin = (Integer) item.get("startMin");
                endHour = (Integer) item.get("endHour");
                endMin = (Integer) item.get("endMin");
                if (endHour == 24 && endMin == 0) {
                    endHour = 23;
                    endMin = 59;
                }
            }
            dowItem.setStartTime(LocalTime.of(startHour, startMin));
            dowItem.setEndTime(LocalTime.of(endHour, endMin));
            refresh(dowItem);
            Clients.evalJavaScript("Ap.calendar.buildRow(" + dowIndex + ")");
        });
    }

    private void updateWidgets() {
        zoneCombobox.setDisabled(!canEdit);
        applyBtn.setDisabled(!canEdit);
        template9To5Btn.setDisabled(!canEdit);
        template24hBtn.setDisabled(!canEdit);
        template24x7Btn.setDisabled(!canEdit);
        importHolidaysBtn.setDisabled(!canEdit);
        addHolidayBtn.setDisabled(!canEdit);
        deleteHolidaysBtn.setDisabled(!canEdit);
        viewerWarning.setVisible(!canEdit);
        editHelp.setVisible(canEdit);
    }

    private void populateTimeZone() {
        Set<String> zoneIds = ZoneId.getAvailableZoneIds();
        zoneModel = new ListModelList<>();

        selectedZone = null;
        for (String id : zoneIds) {
            ZoneId zoneId = ZoneId.of(id);
            Zone currentZone = new Zone(zoneId.getId(), getZoneDisplayDescription(zoneId));

            zoneModel.add(currentZone);
            if (zoneId.equals(ZoneId.of(calendarModel.getZoneId()))
                || (selectedZone == null && zoneId.equals(ZoneId.systemDefault()))) {
                selectedZone = currentZone;
            }
        }
        if (selectedZone != null) {
            zoneModel.addToSelection(selectedZone);
        }
        zoneModel.setMultiple(false);
        zoneModel.sort(Comparator.comparing(Zone::getZoneDisplayName));
        ListModel listSubModel = ListModels.toListSubModel(zoneModel, zoneComparator, zoneIds.size());
        zoneCombobox.setModel(listSubModel);
        zoneCombobox.addEventListener("onClientTimeZone", (Event event) -> {
            if (zoneModel.getSelection().isEmpty()
                || (selectedZone != null && !ZoneOffset.UTC.getId().equals(selectedZone.getId()))) {
                return;
            }

            String zoneId = (String) event.getData();
            int zoneIdx = zoneModel.indexOf(new Zone(zoneId, getZoneDisplayDescription(ZoneId.of(zoneId))));
            if (zoneIdx != -1) {
                zoneModel.clearSelection();
                Zone newSelection = zoneModel.get(zoneIdx);
                zoneModel.addToSelection(newSelection);
            }
        });
        Clients.evalJavaScript("Ap.common.getClientTimeZone('$zoneCombobox', 'onClientTimeZone')");
    }

    private String getZoneDisplayDescription(ZoneId zoneId) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(zoneId.getId()).append(" ");
        buffer.append("(GMT " + zoneId.getRules().getOffset(LocalDateTime.now()) + ")");

        return buffer.toString();
    }

    public WorkDay getDayOfWeekItem(int dowIndex) {
        return (WorkDay) dayOfWeekListbox.getModel().getElementAt(dowIndex - 1);
    }

    public void refresh(WorkDay dowItem) {
        int index = dayOfWeekListModel.indexOf(dowItem);
        dayOfWeekListModel.set(index, dowItem); // trigger change
    }

    public void editWorkday(int dowIndex, int index, Date start, Date end) {
        if (!canEdit) {
            return;
        }
        try {
            Map<String, Object> arg = new HashMap<>();
            arg.put("parentController", this);
            arg.put("dowIndex", dowIndex);
            arg.put("index", index);
            arg.put("start", start);
            arg.put("end", end);
            Window window = (Window) Executions.getCurrent()
                .createComponents(PageUtils.getPageDefinition("calendar/zul/edit-range.zul"), getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create add holidays dialog", e);
        }
    }

    @Listen("onUpdateHolidayDescription = #holidayListbox")
    public void onUpdateHolidayDescription(ForwardEvent event) throws Exception {
        if (!canEdit) {
            return;
        }
        InputEvent inputEvent = (InputEvent) event.getOrigin();
        Textbox textbox = (Textbox) inputEvent.getTarget();
        String description = inputEvent.getValue();
        Listitem listItem = (Listitem) textbox.getParent().getParent();
        Holiday holiday = listItem.getValue();
        holiday.setDescription(description);
    }

    @Listen("onUpdateHolidayDate = #holidayListbox")
    public void onUpdateHolidayDate(ForwardEvent event) throws Exception {
        if (!canEdit) {
            return;
        }
        InputEvent inputEvent = (InputEvent) event.getOrigin();
        Datebox datebox = (Datebox) inputEvent.getTarget();
        Date date = new SimpleDateFormat("yyyy MMM dd").parse(inputEvent.getValue());
        LocalDate holidayDate = TimeUtils.dateToLocalDate(date);
        Listitem listItem = (Listitem) datebox.getParent().getParent();
        Holiday holiday = listItem.getValue();
        holiday.setHolidayDate(holidayDate);
    }

    @Listen("onRemoveHoliday = #holidayListbox")
    public void onRemoveHoliday(final Event event) {
        if (!canEdit) {
            return;
        }
        try {
            Holiday holiday = (Holiday) event.getData();
            removeHoliday(holiday);
        } catch (Exception e) {
            LOGGER.error("Unable to create remove a holiday", e);
        }
    }

    @Listen("onClick = #deleteHolidaysBtn")
    public void onClickDeleteHolidaysBtn() {
        if (!canEdit) {
            return;
        }
        try {
            removeAllHolidays();
        } catch (Exception e) {
            LOGGER.error("Unable to remove all holidays", e);
        }
    }

    @Listen("onClick = #importHolidaysBtn")
    public void onClickImportHolidaysBtn(Event event) {
        if (!canEdit) {
            return;
        }
        Clients.showBusy("Loading ..."); // show a busy message to user
        Events.echoEvent("onDelayedClick", event.getTarget(), null); // echo an event back
    }

    @Listen("onDelayedClick = #importHolidaysBtn")
    public void onDelayedClickImportHolidaysBtn() {
        if (!canEdit) {
            return;
        }
        try {
            Map<String, Object> arg = new HashMap<>();
            arg.put("country", "Australia");
            arg.put("parentController", this);
            Window window = (Window) Executions.getCurrent()
                .createComponents(PageUtils.getPageDefinition("calendar/zul/import-holidays.zul"), getSelf(), arg);
            window.doModal();
        } catch (Exception e) {
            Clients.clearBusy();
            LOGGER.error("Unable to create add holidays dialog", e);
        }
    }

    @Listen("onClick = #addHolidayBtn")
    public void onClickAddHolidayBtn() {
        if (!canEdit) {
            return;
        }
        try {
            Map<String, Object> arg = new HashMap<>();
            arg.put("parentController", this);
            Window window = (Window) Executions.getCurrent()
                .createComponents(PageUtils.getPageDefinition("calendar/zul/add-holiday.zul"), getSelf(), arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create add holidays dialog", e);
        }
    }

    public void updateRange(int dowIndex, int index, int startHour, int startMin, int endHour, int endMin) {
        if (!canEdit) {
            return;
        }
        if (endHour == 23 && endMin == 59) {
            endHour = 24;
            endMin = 0;
        }
        String cmd = String.format("Ap.calendar.updateRange(%d, %d, %d, %d, %d, %d)", dowIndex, index, startHour,
            startMin, endHour, endMin);
        Clients.evalJavaScript(cmd);
    }

    public void deleteRange(int dowIndex, int index) {
        if (!canEdit) {
            return;
        }
        String cmd = String.format("Ap.calendar.deleteRange(%d, %d)", dowIndex, index);
        Clients.evalJavaScript(cmd);
    }

    public void addHoliday(Holiday holiday) {
        if (holiday.isPublic()) {
            holidayListModel.add(holiday);
        } else {
            holidayCustomListModel.add(holiday);
        }
    }

    public void removeHoliday(Holiday holiday) {
        if (holiday.isPublic()) {
            holidayListModel.remove(holiday);
        } else {
            holidayCustomListModel.remove(holiday);
        }
    }

    public void addHolidays(List<Holiday> holidays) {
        holidayListModel.addAll(holidays);
    }

    public void removeAllHolidays() {
        holidayListModel.clear();
    }

    public void rebuild() {
        Clients.evalJavaScript(
            "(function () { if (Ap.calendar && Ap.calendar.rebuild) { Ap.calendar.rebuild(); } })()");
    }

    public void rebuildRow(int dowIndex, String json, boolean workday) {
        Clients.evalJavaScript(
            "(function () { if (Ap.calendar && Ap.calendar.updateRanges) { Ap.calendar.updateRanges("
            + dowIndex + "," + json + "," + (workday ? "true" : "false") + "); } })()");
    }

    /**
     * Sync server model to client side.
     */
    public void syncRows() {
        for (int i = 1; i < 8; i++) {
            String json = toJson(i);
            rebuildRow(i, json, dayOfWeekListModel.getElementAt(i - 1).isWorkingDay());
        }
    }

    public void initialize() {
        dayOfWeekListModel = new ListModelList<>();
        holidayListModel = new ListModelList<>();
        holidayCustomListModel = new ListModelList<>();

        rebuild();
        if (calendarModel != null) {
            fromModels();
        } else {
            mock();
        }
        // This is called subsequently

        dayOfWeekListbox.setModel(dayOfWeekListModel);
        holidayListbox.setModel(holidayListModel);
        holidayCustomListbox.setModel(holidayCustomListModel);
    }

    private void fromModels() {
        List<Holiday> holidays = calendarModel.getHolidays();
        dayOfWeekStartTimes = new ArrayList<>();
        dayOfWeekEndTimes = new ArrayList<>();
        for (Holiday holiday : holidays) {
            if (holiday.isPublic()) {
                holidayListModel.add(holiday);
            } else {
                holidayCustomListModel.add(holiday);
            }
        }

        List<WorkDay> workDays = calendarModel.getOrderedWorkDay();
        dayOfWeekListModel.addAll(workDays);
    }

    private String toJson(int dowIndex) {
        String json = "[";
        WorkDay dowItem = (WorkDay) dayOfWeekListbox.getModel().getElementAt(dowIndex - 1);

        json += "{";
        json += "startHour: " + dowItem.getStartTime().getHour() + ",";
        json += "startMin: " + dowItem.getStartTime().getMinute() + ",";
        int endHour = dowItem.getEndTime().getHour();
        int endMin = dowItem.getEndTime().getMinute();
        if (endHour == 23 && endMin == 59) {
            endHour = 24;
            endMin = 0;
        }
        json += "endHour: " + endHour + ",";
        json += "endMin: " + endMin;
        json += "}";
        json += "]";
        return json;
    }

    @SuppressWarnings("unchecked")
    private void toModels() {
        if (calendarExists) {
            try {
                List<Holiday> allHolidays = new ArrayList<>((List<Holiday>) holidayListbox.getModel());
                allHolidays.addAll((List<Holiday>) holidayCustomListbox.getModel());
                calendarService.updateZoneInfo(calendarId, zoneModel.getSelection().iterator().next().getId());
                calendarService.updateWorkDays(calendarId,
                    CalendarFactory.INSTANCE.toWorkDayModels((List<WorkDay>) dayOfWeekListbox.getModel()));
                calendarService.updateHoliday(calendarId, CalendarFactory.INSTANCE.toHolidayModels(allHolidays));
            } catch (CalendarNotExistsException e) {
                // Post event to notificaton
            }
        }
    }

    private void mock() {
        for (DayOfWeek d : DayOfWeek.values()) {
            dayOfWeekListModel.add(CalendarFactory.INSTANCE.createDefaultWorkDay(d));
        }
    }

    @Listen("onClick = #applyBtn")
    public void onClickApplyBtn() {
        if (!canEdit) {
            return;
        }
        toModels();
        if (!isNew) {
            localCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_CHANGED, null, calendarModel));
        }
        getSelf().detach();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelBtn() {
        if (isNew) {
            localCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_ABANDON, null, calendarId));
        }
        getSelf().detach();
    }

}
