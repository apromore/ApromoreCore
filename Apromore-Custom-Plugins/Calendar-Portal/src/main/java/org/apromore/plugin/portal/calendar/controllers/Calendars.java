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

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.dao.model.Log;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.CalendarItemRenderer;
import org.apromore.plugin.portal.calendar.Constants;
import org.apromore.plugin.portal.calendar.model.Calendar;
import org.apromore.plugin.portal.calendar.model.CalendarFactory;
import org.apromore.plugin.portal.calendar.pageutil.PageUtils;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.service.EventLogService;
import org.apromore.zk.event.CalendarEvents;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;
import org.zkoss.zk.ui.Executions;
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

/**
 * Controller for handling calendar interface Corresponds to calendars.zul
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class Calendars extends SelectorComposer<Window> implements LabelSupplier {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(Calendars.class);

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
    private EventQueue<Event> localCalendarEventQueue;
    public static final String LOCAL_TOPIC = CalendarEvents.TOPIC + "LOCAL";
    private ListModelList<Calendar> calendarListModel;

    private Long appliedCalendarId;
    private boolean canEdit;
    private boolean canDelete;
    private Integer logId;
    private String username;
    private EventListener<Event> eventHandler;
    private Window win;
    private static final String CALENDAR_ID_CONST = "calendarId";
    private static final String CAN_EDIT_CONST = "canEdit";
    private static final String CAN_DELETE_CONST = "canDelete";
    private static final String IS_NEW_CONST = "isNew";

    public Calendars() {
    }

    @Override
    public String getBundleName() {
        return Constants.BUNDLE_NAME;
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        initialize();
        win.setTitle(getLabels().getString("title_text"));
        win.addEventListener("onClose", (Event event) -> cleanup());
        this.win = win;
        try {
            Boolean forwardFromContext = (Boolean) Executions.getCurrent().getArg().get("FOWARD_FROM_CONTEXT");
            if (forwardFromContext != null && forwardFromContext) {
                forwardToEditOrCreateNewCalendar();
            }
        } catch (Exception ex) {
            LOGGER.error("Error in retrieving information directCreateNew");
        }

    }

    public void cleanup() {
        localCalendarEventQueue.unsubscribe(eventHandler);
        EventQueues.remove(LOCAL_TOPIC);
    }

    public void initialize() {
        appliedCalendarId = (Long) Executions.getCurrent().getArg().get(CALENDAR_ID_CONST);
        logId = (Integer) Executions.getCurrent().getArg().get("logId");
        username = UserSessionManager.getCurrentUser().getUsername();
        if (username == null) {
            return;
        }
        canEdit = (boolean) Executions.getCurrent().getArg().get(CAN_EDIT_CONST);
        canDelete = (boolean) Executions.getCurrent().getArg().get(CAN_DELETE_CONST);
        applyCalendarBtn.setDisabled(!canEdit);
        restoreBtn.setDisabled(!canEdit);
        addNewCalendar.setDisabled(!canEdit);
        localCalendarEventQueue = EventQueues.lookup(LOCAL_TOPIC, EventQueues.DESKTOP, true);
        sessionCalendarEventQueue = EventQueues.lookup(CalendarEvents.TOPIC, EventQueues.SESSION, true);

        CalendarItemRenderer itemRenderer =
            new CalendarItemRenderer(calendarService, appliedCalendarId, canEdit, canDelete);
        calendarListbox.setItemRenderer(itemRenderer);
        calendarListModel = new ListModelList<>();
        calendarListModel.setMultiple(false);
        populateCalendarList();

        eventHandler = (Event event) -> {
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
                Calendar calendarItem = (Calendar) event.getData();
                beforeRemoveCalendar(calendarItem);
            } else if (CalendarEvents.ON_CALENDAR_CHANGED.equals(event.getName())) {
                // propagate to session queue (other tabs/plugins)
                Calendar calendarItem = (Calendar) event.getData();
                Long calendarId = calendarItem.getId();
                List<Integer> logIds = getAssociatedLogIds(calendarId);
                sessionCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_CHANGED, null,
                    calendarId));
                sessionCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_REFRESH, null, logIds));
            } else if (CalendarEvents.ON_CALENDAR_REMOVE.equals(event.getName())) {
                // propagate to session queue (other tabs/plugins)
                Calendar calendarItem = (Calendar) event.getData();
                Long calendarId = calendarItem.getId();
                List<Integer> logIds = getAssociatedLogIds(calendarId);
                if (logId != null && logIds.contains(logId)) {
                    appliedCalendarId = null;
                }
                sessionCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_REMOVE, null, calendarId));
                sessionCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_UNLINK, null, logIds));
                removeCalendar(calendarItem);
            }
        };
        localCalendarEventQueue.subscribe(eventHandler);
    }

    List<Integer> getAssociatedLogIds(Long calendarId) {
        return eventLogService.getLogListFromCalendarId(calendarId)
            .stream().map(Log::getId).collect(Collectors.toList());
    }

    public void populateCalendarList() {
        List<Calendar> models = CalendarFactory.INSTANCE.fromCalendarModels(calendarService.getCalendars(username));
        if (appliedCalendarId != null && appliedCalendarId > 0
            && models.stream().noneMatch(c -> c.getId().equals(appliedCalendarId))) {
            models.add(CalendarFactory.INSTANCE.fromCalendarModel(calendarService.getCalendar(appliedCalendarId)));
        }
        calendarListModel.clear();
        for (Calendar model : models) {
            calendarListModel.add(model);
            if (model.getId().equals(appliedCalendarId)) {
                calendarListModel.addToSelection(model);
            }
        }
        calendarListbox.setModel(calendarListModel);
        updateButtons();
    }

    private void applyCalendarForLog(Integer logId, Long calendarId) {
        eventLogService.updateCalendarForLog(logId, calendarId);
        if (calendarId == null) {
            sessionCalendarEventQueue
                .publish(new Event(CalendarEvents.ON_CALENDAR_UNLINK, null, Arrays.asList(logId)));
        } else {
            sessionCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_LINK, null, logId));
        }
    }

    private void beforeRemoveCalendar(Calendar calendarItem) {
        List<Log> relatedLogList = eventLogService.getLogListFromCalendarId(calendarItem.getId());
        if (relatedLogList == null || relatedLogList.isEmpty()) {
            localCalendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_REMOVE, null, calendarItem));
        } else {
            try {
                Map<String, Object> arg = new HashMap<>();
                arg.put("calendarItem", calendarItem);
                Window window = (Window) Executions.getCurrent()
                    .createComponents(PageUtils.getPageDefinition("calendar/zul/delete-confirm.zul"),
                        null, arg);
                window.doModal();
            } catch (Exception e) {
                String msg = getLabels().getString("failed_remove_cal_message");
                LOGGER.error(msg, e);
                Notification.error(msg);
            }
        }
    }

    public void removeCalendar(Calendar calendarItem) {
        try {
            // Reset the calendar of all owned logs associated with the calendar to remove
            List<Log> relatedLogs = eventLogService.getLogListFromCalendarId(calendarItem.getId(), username);
            relatedLogs.forEach(l -> applyCalendarForLog(l.getId(), null));

            //Only delete the calendar if there are no more logs associated with it
            if (CollectionUtils.isEmpty(eventLogService.getLogListFromCalendarId(calendarItem.getId()))) {
                calendarListModel.remove(calendarItem);
                calendarService.deleteCalendar(calendarItem.getId());
            }

            if (calendarItem.getId().equals(appliedCalendarId)) {
                appliedCalendarId = null;
            }
        } catch (Exception e) {
            String msg = getLabels().getString("failed_remove_cal_message");
            LOGGER.error(msg, e);
            Notification.error(msg);
        }
        populateCalendarList();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelBtn() {
        cleanup();
        getSelf().detach();
    }

    @Listen("onClick = #selectBtn")
    public void onClickPublishBtn() {
        String logName = selectedLog.getValue();
        String msg = getLabels().getString("success_apply_message");
        String infoText = String.format(msg, logName);
        Notification.info(infoText);
        if (!calendarListModel.getSelection().isEmpty()) {
            applyCalendarForLog(logId, getSelectedCalendarId());
        }
        cleanup();
        getSelf().detach();
    }

    @Listen("onClick = #addNewCalendarBtn")
    public void onClickAddNewCalendar() {
        Calendar model;
        try {
            String calendarName = MessageFormat.format(getLabels().getString("created_default_cal_message"),
                DateTimeUtils.humanize(LocalDateTime.now()), username);
            model = CalendarFactory.INSTANCE.fromCalendarModel(calendarService.createBusinessCalendar(
                calendarName, username, true,
                ZoneId.systemDefault().toString()));
            populateCalendarList();
            Long calendarId = model.getId();
            try {
                Map<String, Object> arg = new HashMap<>();
                arg.put(CALENDAR_ID_CONST, calendarId);
                arg.put("parentController", this);
                arg.put(IS_NEW_CONST, true);
                arg.put(CAN_EDIT_CONST, true);
                Window window = (Window) Executions.getCurrent()
                    .createComponents(PageUtils.getPageDefinition("calendar/zul/calendar.zul"), null, arg);
                window.doModal();
            } catch (Exception e) {
                String msg = getLabels().getString("failed_create_message");
                LOGGER.error(msg, e);
                Notification.error(msg);
            }
        } catch (CalendarAlreadyExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void forwardToEditOrCreateNewCalendar() {
        Map<String, Object> arg = new HashMap<>();
        if (appliedCalendarId == null || appliedCalendarId == 0) {
            try {
                if (!canEdit) {
                    LOGGER.error("Not Authorized to create calendar!");
                    return;
                }
                String calendarName = MessageFormat.format(getLabels().getString("created_default_cal_message"),
                    DateTimeUtils.humanize(LocalDateTime.now()), username);
                Calendar model =
                    CalendarFactory.INSTANCE.fromCalendarModel(calendarService.createBusinessCalendar(
                        calendarName,
                        username, true,
                        ZoneId.systemDefault().toString()));
                populateCalendarList();
                arg.put(CALENDAR_ID_CONST, model.getId());
                arg.put(IS_NEW_CONST, true);
            } catch (CalendarAlreadyExistsException e) {
                LOGGER.error("Error in creating Calendar", e);
                return;
            }
        } else {
            arg.put(CALENDAR_ID_CONST, appliedCalendarId);
            arg.put(IS_NEW_CONST, false);
        }
        reCheckOnExistingCalendar();
        try {
            arg.put(CAN_EDIT_CONST, canEdit);
            arg.put("parentController", this);
            Window window = (Window) Executions.getCurrent()
                .createComponents(PageUtils.getPageDefinition("calendar/zul/calendar.zul"), this.win, arg);
            this.win.doModal();// parent popup
            window.doModal(); // child popup
        } catch (Exception e) {
            LOGGER.error("Error in forward to edit/create calendar", e);
        }

    }

    private void reCheckOnExistingCalendar() {
        try {
            Long calendarIdFromLog = eventLogService.getCalendarIdFromLog(logId);
            if (calendarIdFromLog > 0 && !calendarIdFromLog.equals(appliedCalendarId)) {
                appliedCalendarId = calendarIdFromLog;
                calendarListbox
                    .setItemRenderer(new CalendarItemRenderer(calendarService, appliedCalendarId, canEdit, canDelete));
                populateCalendarList();
            }
        } catch (Exception ex) {
            LOGGER.error("Error in refreshing with existing calendar!", ex);
        }
    }

    private Long getSelectedCalendarId() {
        Set<Calendar> selection = calendarListModel.getSelection();
        if (selection.isEmpty()) {
            return null;
        }
        return (calendarListModel.getSelection().iterator().next()).getId();
    }

    @Listen("onSelect = #calendarListbox")
    public void onSelectCalendarItem() {
        updateButtons();
    }

    @Listen("onClick = #restoreBtn")
    public void onClickRestoreBtn() {
        applyCalendarForLog(logId, null);
        cleanup();
        getSelf().detach();
        String logName = selectedLog.getValue();
        String msg = getLabels().getString("success_restore_message");
        String infoText = String.format(msg, logName);
        Notification.info(infoText);
    }

    private void updateButtons() {
        Long selectedCalendarId = getSelectedCalendarId();
        applyCalendarBtn.setDisabled(
            calendarListbox.getSelectedCount() <= 0
                || !canEdit
                || (appliedCalendarId != null
                && selectedCalendarId != null
                && selectedCalendarId.equals(appliedCalendarId))
        );
        restoreBtn.setDisabled(
            !canEdit
                || appliedCalendarId == null
                || calendarService.getCalendars().stream().noneMatch(c -> c.getId().equals(appliedCalendarId))
        );
    }

}
