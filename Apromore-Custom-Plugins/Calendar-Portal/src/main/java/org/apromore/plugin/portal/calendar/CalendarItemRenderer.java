/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal.calendar;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apromore.calendar.exception.CalendarNotExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.pageutil.PageUtils;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class CalendarItemRenderer implements ListitemRenderer {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(CalendarItemRenderer.class);

    CalendarService calendarService;

    public CalendarItemRenderer(CalendarService calendarService) {
	super();
	this.calendarService = calendarService;
    }

    public Listcell renderCell(Listitem listItem, Component comp) {
	Listcell listCell = new Listcell();
	listCell.appendChild(comp);
	listItem.appendChild(listCell);
	return listCell;
    }

    public Listcell renderTextCell(Listitem listItem, String content) {
	return renderCell(listItem, new Label(content));
    }

    public Listcell renderIconCell(Listitem listItem, String sclass, String tooltip) {
	Span span = new Span();
	span.setSclass(sclass);
	span.setTooltiptext(tooltip);
	return renderCell(listItem, span);
    }

    public void editCalendar(Long calendarId) {
	try {
	    Map arg = new HashMap<>();
	    arg.put("calendarId", calendarId);
	    Window window = (Window) Executions.getCurrent()
	            .createComponents(PageUtils.getPageDefinition("calendar/zul/calendar.zul"), null, arg);
	    window.doModal();
	} catch (Exception e) {
	    LOGGER.error("Unable to create custom calendar dialog", e);
	    // Notification.error("Unable to create custom calendar dialog");
	}
    }

    public void removeCalendar(Long calendarId) {
	try {
	    calendarService.deleteCalendar(calendarId);
	} catch (Exception e) {
	    LOGGER.error("Unable to create custom calendar dialog", e);
	    // Notification.error("Unable to create custom calendar dialog");
	}
    }

    public void updateCalendarName(String newName, Long calendarId) {
	try {
	    calendarService.updateCalendarName(calendarId, newName);
	} catch (CalendarNotExistsException e) {
//			Need to handle this via publishing message in event queue
	    LOGGER.error("Unable to update custom calendar dialog", e);
	    e.printStackTrace();
	}
	LOGGER.info("Edit name", newName);
    }

    @Override
    public void render(Listitem listItem, Object obj, int index) {
	CalendarModel calendarItem = (CalendarModel) obj;

	Textbox textbox = new Textbox(calendarItem.getName());
	textbox.setSubmitByEnter(true);
	textbox.setSclass("ap-inline-textbox");
	textbox.setHflex("1");
	Listcell nameCell = renderCell(listItem, textbox);
	textbox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
	    @Override
	    public void onEvent(Event event) throws Exception {
		updateCalendarName(textbox.getValue(), calendarItem.getId());
	    }
	});

	OffsetDateTime created = calendarItem.getCreated();
	renderTextCell(listItem, created.format(DateTimeFormatter.ofPattern("dd MMM yy")));
	Listcell editAction = renderIconCell(listItem, "ap-icon ap-icon-user-edit", "Edit calendar");
	Listcell removeAction = renderIconCell(listItem, "ap-icon ap-icon-trash", "Delete calendar");

	nameCell.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
	    @Override
	    public void onEvent(Event event) throws Exception {
		textbox.setFocus(true);
	    }
	});

	editAction.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
	    @Override
	    public void onEvent(Event event) throws Exception {
		editCalendar(calendarItem.getId());
	    }
	});

	removeAction.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
	    @Override
	    public void onEvent(Event event) throws Exception {
		removeCalendar(calendarItem.getId());
		listItem.detach();
	    }
	});

	listItem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
	    @Override
	    public void onEvent(Event event) throws Exception {
		editCalendar(calendarItem.getId());
	    }
	});
    }

}
