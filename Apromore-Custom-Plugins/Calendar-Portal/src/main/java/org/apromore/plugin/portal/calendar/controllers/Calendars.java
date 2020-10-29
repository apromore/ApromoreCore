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
 * Corresponds to calendars.zul
 */
public class Calendars extends SelectorComposer<Window> {

    private static Logger LOGGER = LoggerFactory.getLogger(Calendars.class);

    @Wire("#calendarListbox")
    Listbox calendarListbox;

    private ListModelList<CalendarItem> calendarListModel;

    private Window mainWindow;

    public Calendars() throws Exception {
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        mainWindow = win;
        initialize();
    }

    public void initialize() {
        CalendarItemRenderer itemRenderer = new CalendarItemRenderer();
        calendarListbox.setItemRenderer(itemRenderer);
        calendarListModel = new ListModelList<CalendarItem>();
        calendarListbox.setModel(calendarListModel);
        mock();
    }

    private void mock() {
        calendarListModel.add(new CalendarItem(1L, "Calendar 1", OffsetDateTime.now()));
        calendarListModel.add(new CalendarItem(2L, "Calendar 2", OffsetDateTime.now()));
    }

    @Listen("onClick = #okBtn")
    public void onClickOkBtn() {
        getSelf().detach();
    }

}