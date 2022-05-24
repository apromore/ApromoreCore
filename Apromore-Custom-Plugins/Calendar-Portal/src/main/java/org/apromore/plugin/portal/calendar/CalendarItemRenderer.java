/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal.calendar;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.apromore.calendar.exception.CalendarNotExistsException;
import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.model.Calendar;
import org.apromore.plugin.portal.calendar.pageutil.PageUtils;
import org.apromore.zk.event.CalendarEvents;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Span;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class CalendarItemRenderer implements ListitemRenderer<Calendar>, LabelSupplier {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(CalendarItemRenderer.class);

    private CalendarService calendarService;
    private long appliedCalendarId;
    private boolean canEdit;
    private boolean canDelete;
    private String failedMessage;
    private static final String UNABLE_TO_RENAME_CALENDAR = "unable_rename_calendar";

    @Override
    public String getBundleName() {
        return Constants.BUNDLE_NAME;
    }

    public CalendarItemRenderer(CalendarService calendarService, long appliedCalendarId, boolean canEdit,
                                boolean canDelete) {
        super();
        this.calendarService = calendarService;
        this.appliedCalendarId = appliedCalendarId;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.failedMessage = getLabel("failed_create_message");
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

    public Span renderIcon(Component parent, String sclass, String tooltip, boolean disabled) {
        Span span = new Span();
        span.setSclass(sclass + (disabled ? " ap-icon-disabled" : ""));
        span.setTooltiptext(tooltip);
        span.setParent(parent);
        return span;
    }

    public void editCalendar(Long calendarId) {
        if (!canEdit) {
            return;
        }
        try {
            Map arg = new HashMap<>();
            arg.put("calendarId", calendarId);
            arg.put("isNew", false);
            arg.put("canEdit", true);
            Window window = (Window) Executions.getCurrent()
                .createComponents(PageUtils.getPageDefinition("calendar/zul/calendar.zul"), null, arg);
            window.doModal();
        } catch (Exception e) {
            LOGGER.error(failedMessage, e);
            Notification.error(failedMessage);
        }
    }

    public void removeCalendar(Long calendarId) {
        try {
            calendarService.deleteCalendar(calendarId);
        } catch (Exception e) {
            LOGGER.error(failedMessage, e);
            Notification.error(failedMessage);
        }
    }

    public boolean updateCalendarName(String newName, Long calendarId) {
        try {
            calendarService.updateCalendarName(calendarId, newName);
            return true;
        } catch (CalendarNotExistsException e) {
            // Need to handle this via publishing message in event queue
            LOGGER.error(UNABLE_TO_RENAME_CALENDAR, e);
            Notification.error(UNABLE_TO_RENAME_CALENDAR);
        } catch (Exception e) {
            LOGGER.error(UNABLE_TO_RENAME_CALENDAR, e);
            Notification.error(getLabel(UNABLE_TO_RENAME_CALENDAR));
        }
        LOGGER.info("Edit name", newName);
        return false;
    }

    @Override
    public void render(Listitem listItem, Calendar calendarItem, int index) {
        if (calendarItem.getId().equals(appliedCalendarId)) {
            renderIconCell(listItem, "ap-icon ap-icon-static ap-icon-check-circle", "Applied calendar");
        } else {
            renderTextCell(listItem, "");
        }

        Textbox textbox = new Textbox(calendarItem.getName());
        textbox.setSubmitByEnter(true);
        textbox.setSclass("ap-inline-textbox");
        textbox.setHflex("1");
        textbox.setReadonly(true);
        textbox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                boolean updateSuccessful = updateCalendarName(textbox.getValue(), calendarItem.getId());
                textbox.setReadonly(true);
                if (!updateSuccessful) {
                    textbox.setValue(calendarItem.getName());
                }
            }
        });

        textbox.addEventListener(Events.ON_OK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                textbox.setReadonly(true);
            }
        });

        textbox.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                textbox.setReadonly(true);
            }
        });

        Listcell nameCell = renderCell(listItem, textbox);
        nameCell.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                ((ListModelList) listItem.getListbox().getModel()).addToSelection(calendarItem);
                listItem.setSelected(true);
                Events.sendEvent(Events.ON_SELECT, listItem.getListbox(), null);
            }
        });

        OffsetDateTime created = calendarItem.getCreated();
        renderTextCell(listItem, DateTimeUtils.humanize(created));
        Hlayout actionBar = new Hlayout();

        Span renameAction = renderIcon(actionBar, "ap-icon ap-icon-rename", "Rename", !canEdit);
        renameAction.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                textbox.setFocus(true);
                textbox.setReadonly(false);
            }
        });

        Span editAction = renderIcon(actionBar, "ap-icon ap-icon-calendar-edit", "Edit", !canEdit);
        editAction.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                editCalendar(calendarItem.getId());
            }
        });

        Span removeAction = renderIcon(actionBar, "ap-icon ap-icon-trash", "Remove", !canEdit || !canDelete);
        removeAction.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                EventQueue<Event> calendarEventQueue =
                    EventQueues.lookup(CalendarEvents.TOPIC + "LOCAL", EventQueues.DESKTOP, true);
                calendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_BEFORE_REMOVE, null, calendarItem));
            }
        });

        renderCell(listItem, actionBar);

        listItem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                editCalendar(calendarItem.getId());
            }
        });
    }

}
