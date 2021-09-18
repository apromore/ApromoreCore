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
package org.apromore.plugin.portal.calendar.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import org.apromore.dao.model.Log;
import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.service.EventLogService;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.CalendarItemRenderer;
import org.apromore.plugin.portal.calendar.pageutil.PageUtils;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import org.apromore.plugin.portal.calendar.CalendarEvents;
import org.apromore.plugin.portal.calendar.LabelSupplier;

/**
 * Controller for handling calendar interface Corresponds to calendars.zul
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class Calendars extends SelectorComposer<Window> implements LabelSupplier {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(Calendars.class);

    @Wire("#calendarListbox")
    Listbox calendarListbox;

    @Wire("#addNewCalendarBtn")
    Button addNewCalendar;

	@Wire("#selectBtn")
	Button applyCalendarBtn;

	@Wire("#restoreBtn")
	Button restoreBtn;

	@Wire("#selectedName")
	Label selectedLog;

    @WireVariable("calendarService")
    CalendarService calendarService;

    @WireVariable("eventLogService")
    private EventLogService eventLogService;

    private EventQueue<Event> sessionCalendarEventQueue;

    private ListModelList<CalendarModel> calendarListModel;

    private Long appliedCalendarId;
    private boolean canEdit;

    public Calendars() throws Exception {
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        initialize();
        win.setTitle(getLabels().getString("title_text"));
        win.addEventListener("onClose", (Event event) -> EventQueues.remove(CalendarService.EVENT_TOPIC));
    }

    public void initialize() {
        appliedCalendarId = (Long) Executions.getCurrent().getArg().get("calendarId");
        Integer logId = (Integer) Executions.getCurrent().getArg().get("logId");
        canEdit = (boolean) Executions.getCurrent().getArg().get("canEdit");
        applyCalendarBtn.setDisabled(!canEdit);
        restoreBtn.setDisabled(!canEdit);
        addNewCalendar.setDisabled(!canEdit);
        EventQueue<Event> localCalendarEventQueue = EventQueues.lookup(CalendarService.EVENT_TOPIC + "LOCAL", EventQueues.DESKTOP,true);
        sessionCalendarEventQueue = EventQueues.lookup(CalendarService.EVENT_TOPIC, EventQueues.SESSION,true);

        CalendarItemRenderer itemRenderer = new CalendarItemRenderer(calendarService, appliedCalendarId, canEdit);
        calendarListbox.setItemRenderer(itemRenderer);
        calendarListModel = new ListModelList<CalendarModel>();
        calendarListModel.setMultiple(false);
        populateCalendarList();

        localCalendarEventQueue.subscribe((Event event) -> {
            // Abandon newly created calendar
            if (CalendarEvents.ON_CALENDAR_ABANDON.equals(event.getName())) {
                Long calendarId = (Long) event.getData();
                try {
                    calendarService.deleteCalendar(calendarId);
                } catch (Exception e) {
                    LOGGER.warn("Double deletion might have occurred");
                }
                populateCalendarList();
            } else if (CalendarEvents.ON_CALENDAR_BEFORE_REMOVE.equals(event.getName())) {
                CalendarModel calendarItem = (CalendarModel) event.getData();
                beforeRemoveCalendar(calendarItem);
            } else if (CalendarEvents.ON_CALENDAR_REMOVE.equals(event.getName())) {
                CalendarModel calendarItem = (CalendarModel) event.getData();
                removeCalendar(calendarItem);
            }
        });
    }

    public void populateCalendarList() {
        List<CalendarModel> models = calendarService.getCalendars();
        calendarListModel.clear();
        for (CalendarModel model : models) {
            calendarListModel.add(model);
            if (model.getId().equals(appliedCalendarId)) {
                calendarListModel.addToSelection(model);
                applyCalendarBtn.setDisabled(!canEdit);
                restoreBtn.setDisabled(!canEdit);
            }
        }
        calendarListbox.setModel(calendarListModel);
    }

    public void beforeRemoveCalendar(CalendarModel calendarItem) {
        List<Log> relatedLogList = eventLogService.getLogListFromCalendarId(calendarItem.getId());
        if (relatedLogList == null || relatedLogList.isEmpty()) {
            sessionCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_REMOVE, null, calendarItem));
        } else {
            try {
                Map<String, Object> arg = new HashMap<>();
                arg.put("calendarItem", calendarItem);
                Window window = (Window) Executions.getCurrent()
                        .createComponents(PageUtils.getPageDefinition("calendar/zul/delete-confirm.zul"), null, arg);
                window.doModal();
            } catch (Exception e) {
                String msg = getLabels().getString("failed_remove_cal_message");
                LOGGER.error(msg, e);
                Notification.error(msg);
            }
        }
    }

    public void removeCalendar(CalendarModel calendarItem) {
        try {
            // Update listbox. onSelect is sent when an item is selected or deselected.
            calendarListModel.remove(calendarItem);
            calendarService.deleteCalendar(calendarItem.getId());
            updateApplyCalendarButton();
            restoreBtn.setDisabled(
                   !canEdit || calendarService.getCalendars().stream().noneMatch(c -> c.getId().equals(appliedCalendarId))
            );
        } catch (Exception e) {
            String msg = getLabels().getString("failed_remove_cal_message");
            LOGGER.error(msg, e);
            Notification.error(msg);
        }
        populateCalendarList();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelBtn() {
	    getSelf().detach();
    }

    @Listen("onClick = #selectBtn")
    public void onClickPublishBtn() {
        String logName = selectedLog.getValue();
        String msg = getLabels().getString("success_apply_message");
        String infoText = String.format(msg, logName);
        Notification.info(infoText);
        sessionCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_PUBLISH, null,
                ((CalendarModel) calendarListModel.getSelection().iterator().next()).getId()));
        getSelf().detach();
       
    }

    @Listen("onClick = #addNewCalendarBtn")
    public void onClickAddNewCalendar() {
        CalendarModel model;
        try {
            String msg = getLabels().getString("created_default_cal_message");
            String calendarName = msg + " " + DateTimeUtils.humanize(LocalDateTime.now());
            model = calendarService.createBusinessCalendar(calendarName, true, ZoneId.systemDefault().toString());
            populateCalendarList();
            updateApplyCalendarButton();
            Long calendarId = model.getId();
            try {
                Map arg = new HashMap<>();
                arg.put("calendarId", calendarId);
                arg.put("parentController", this);
                arg.put("isNew", true);
                Window window = (Window) Executions.getCurrent()
                        .createComponents(PageUtils.getPageDefinition("calendar/zul/calendar.zul"), null, arg);
                window.doModal();
            } catch (Exception e) {
                msg = getLabels().getString("failed_create_message");
                LOGGER.error(msg, e);
                Notification.error(msg);
            }
        } catch (CalendarAlreadyExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Listen("onSelect = #calendarListbox")
    public void onSelectCalendarItem() {
        updateApplyCalendarButton();
    }

    @Listen("onClick = #restoreBtn")
    public void onClickRestoreBtn() {
        sessionCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_PUBLISH, null,null));
        getSelf().detach();
        String logName = selectedLog.getValue();
        String msg = getLabels().getString("success_restore_message");
        String infoText = String.format(msg, logName);
        Notification.info(infoText);
    }

    private void updateApplyCalendarButton() {
        applyCalendarBtn.setDisabled(calendarListbox.getSelectedCount() <= 0 || !canEdit);
    }

}
