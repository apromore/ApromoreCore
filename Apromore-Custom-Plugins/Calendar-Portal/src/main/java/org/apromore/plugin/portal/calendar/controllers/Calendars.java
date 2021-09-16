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

import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.CalendarItemRenderer;
import org.apromore.plugin.portal.calendar.pageutil.PageUtils;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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
/**
 * Controller for handling calendar interface Corresponds to calendars.zul
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class Calendars extends SelectorComposer<Window> {

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

    private EventQueue calendarEventQueue;

    private ListModelList<CalendarModel> calendarListModel;

    private Long appliedCalendarId;

    public Calendars() throws Exception {
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        initialize();
        win.addEventListener("onClose", new EventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {
                 EventQueues.remove(CalendarService.EVENT_TOPIC);
            }
        });
    }

    public void initialize() {
        Integer logId = (Integer) Executions.getCurrent().getArg().get("logId");
        applyCalendarBtn.setDisabled(true);
        restoreBtn.setDisabled(true);
        calendarEventQueue = EventQueues.lookup(CalendarService.EVENT_TOPIC, false);

        appliedCalendarId = (Long) Executions.getCurrent().getArg().get("calendarId");
        CalendarItemRenderer itemRenderer = new CalendarItemRenderer(calendarService, appliedCalendarId);
        calendarListbox.setItemRenderer(itemRenderer);
        calendarListModel = new ListModelList<CalendarModel>();
        calendarListModel.setMultiple(false);
        populateCalendarList();

        calendarEventQueue.subscribe((Event event) -> {
            // Abandon newly created calendar
            if (CalendarEvents.ON_CALENDAR_ABANDON.equals(event.getName())) {
                Long calendarId = (Long) event.getData();
                try {
                    calendarService.deleteCalendar(calendarId);
                } catch (Exception e) {
                    LOGGER.warn("Double deletion might have occurred");
                }
                populateCalendarList();
            }
        });
    }

    public ResourceBundle getLabels() {
        Locale locale = (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE);
        return ResourceBundle.getBundle("calendar", locale,
                Calendars.class.getClassLoader());
    }

    public void populateCalendarList() {
        List<CalendarModel> models = calendarService.getCalendars();
        calendarListModel.clear();
        for (CalendarModel model : models) {
            calendarListModel.add(model);
            if (model.getId().equals(appliedCalendarId)) {
                calendarListModel.addToSelection(model);
                applyCalendarBtn.setDisabled(false);
                restoreBtn.setDisabled(false);
            }

        }
        calendarListbox.setModel(calendarListModel);
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelBtn() {
        EventQueues.remove(CalendarService.EVENT_TOPIC);
	    getSelf().detach();
    }

    @Listen("onClick = #selectBtn")
    public void onClickPublishBtn() {
        String logName = selectedLog.getValue();
        String infoText = String.format("Custom calendar applied to log %s", logName);
        Notification.info(infoText);
        calendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_PUBLISH, null,
                ((CalendarModel) calendarListModel.getSelection().iterator().next()).getId()));
        getSelf().detach();
       
    }

    @Listen("onClick = #addNewCalendarBtn")
    public void onClickAddNewCalendar() {
        CalendarModel model;
        try {
            String calendarName = "Business Calendar 9 to 5 created on " + DateTimeUtils.humanize(LocalDateTime.now());
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
                LOGGER.error("Unable to create custom calendar dialog", e);
                // Notification.error("Unable to create custom calendar dialog");
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

    @Listen("onDeleteCalendar = #calendarListbox")
    public void onDeleteCalendarItem() {
        updateApplyCalendarButton();
        restoreBtn.setDisabled(calendarService.getCalendars().stream().noneMatch(c -> c.getId().equals(appliedCalendarId)));
    }

    @Listen("onClick = #restoreBtn")
    public void onClickRestoreBtn() {
      calendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_PUBLISH, null,null));
        getSelf().detach();
        String logName = selectedLog.getValue();
        String infoText = String.format("Log %s's original calendar has been restored", logName);
        Notification.info(infoText);
    }

    private void updateApplyCalendarButton() {
        applyCalendarBtn.setDisabled(calendarListbox.getSelectedCount() <= 0);
    }

}
