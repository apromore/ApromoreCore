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

import java.util.List;
import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.dao.model.Log;
import org.apromore.plugin.portal.calendar.Constants;
import org.apromore.plugin.portal.calendar.model.Calendar;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.service.EventLogService;
import org.apromore.zk.event.CalendarEvents;
import org.apromore.zk.label.LabelSupplier;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class DeleteConfirm extends SelectorComposer<Window> implements LabelSupplier {

    private Calendar calendarItem;
    private Long calendarId;

    @WireVariable("calendarService")
    CalendarService calendarService;

    @WireVariable("eventLogService")
    EventLogService eventLogService;

    @Wire("#relatedLogListbox")
    Listbox relatedLogListbox;

    public class RelatedLog {
        private Integer id;
        private String name;
        private String date;

        public RelatedLog(Integer id, String name, String date) {
            this.id = id;
            this.name = name;
            this.date = date;
        }

        public Integer getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getDate() {
            return this.date;
        }

        @Override
        public int hashCode() {
            return id == null ? 0 : name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !RelatedLog.class.equals(obj.getClass())) {
                return false;
            }
            return (obj instanceof RelatedLog) && id.equals(((RelatedLog) obj).id);
        }
    }

    @Override
    public String getBundleName() {
        return Constants.BUNDLE_NAME;
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);

        calendarItem = (Calendar) Executions.getCurrent().getArg().get("calendarItem");
        calendarId = calendarItem.getId();
        populateRelatedLogs();
    }

    public void populateRelatedLogs() {
        String currentUser = UserSessionManager.getCurrentUser().getUsername();
        List<Log> relatedLogList = eventLogService.getLogListFromCalendarId(calendarId, currentUser);
        ListModelList<RelatedLog> relatedLogModel = new ListModelList<>();
        for (Log log : relatedLogList) {
            relatedLogModel.add(new RelatedLog(
                log.getId(),
                log.getName(),
                DateTimeUtils.normalize(log.getCreateDate())
            ));
        }
        relatedLogListbox.setModel(relatedLogModel);
    }

    @Listen("onClick = #continueBtn")
    public void onClickContinueBtn() {
        EventQueue<Event> calendarEventQueue = EventQueues.lookup(Calendars.LOCAL_TOPIC, EventQueues.DESKTOP, true);
        calendarEventQueue.publish(new Event(CalendarEvents.ON_CALENDAR_REMOVE, null, calendarItem));
        getSelf().detach();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelBtn() {
        getSelf().detach();
    }
}
