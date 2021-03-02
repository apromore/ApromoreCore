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
import java.util.List;

import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.plugin.portal.calendar.CalendarItemRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

/**
 * Controller for handling calendar interface Corresponds to calendars.zul
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class Calendars extends SelectorComposer<Window> {

    private static Logger LOGGER = LoggerFactory.getLogger(Calendars.class);

    @Wire("#calendarListbox")
    Listbox calendarListbox;

    @Wire("#addNewCalendarBtn")
    Button addNewCalendar;

    @WireVariable("calendarService")
    CalendarService calendarService;

    private EventQueue calendarEventQueue;

    private ListModelList<CalendarModel> calendarListModel;

    public Calendars() throws Exception {
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
	super.doAfterCompose(win);
	initialize();
    }

    public void initialize() {

	Integer logId = (Integer) Executions.getCurrent().getArg().get("logId");
	calendarEventQueue = EventQueues.lookup(CalendarService.EVENT_TOPIC,
		false);

	CalendarItemRenderer itemRenderer = new CalendarItemRenderer(calendarService);
	calendarListbox.setItemRenderer(itemRenderer);
	calendarListModel = new ListModelList<CalendarModel>();
        calendarListModel.setMultiple(false);
        populateCalendarList();

    }

    public void populateCalendarList() {
        List<CalendarModel> models = calendarService.getCalendars();
        calendarListModel.clear();
        Long selectedCalendarId = (Long) Executions.getCurrent().getArg().get("calendarId");
        for (CalendarModel model : models) {
            calendarListModel.add(model);
            if (model.getId().equals(selectedCalendarId)) {
                calendarListModel.addToSelection(model);
            }

        }
        calendarListbox.setModel(calendarListModel);
    }

    @Listen("onClick = #okBtn")
    public void onClickOkBtn() {
	getSelf().detach();
    }

    @Listen("onClick = #selectBtn")
    public void onClickPublishBtn() {
	calendarEventQueue.publish(
		new Event("onCalendarPublish", null,
			((CalendarModel) calendarListModel.getSelection().iterator().next()).getId()));
	getSelf().detach();
    }

    @Listen("onClick = #addNewCalendarBtn")
    public void onClickAddNewCalendar() {

	CalendarModel model;
	try {
	    String calendarName = "Generic Calender 9 to 5 created on" + LocalDateTime.now();
	    model = calendarService.createBusinessCalendar(calendarName, true, ZoneId.systemDefault().toString());
            populateCalendarList();

	} catch (CalendarAlreadyExistsException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
