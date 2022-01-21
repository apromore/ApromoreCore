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
package org.apromore.plugin.portal.calendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.config.ConfigBean;
import org.apromore.commons.datetime.DateTimeUtils;
import org.apromore.dao.model.User;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.pageutil.PageUtils;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.PermissionType;
import org.apromore.service.EventLogService;
import org.apromore.service.SecurityService;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@Component("calendarPlugin")
public class CalendarPlugin extends DefaultPortalPlugin implements LabelSupplier {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(CalendarPlugin.class);
    private static final String CREATE_NEW_CALENDAR_CONST="createNewCalendar";
    private static final String CAN_EDIT_CONST="canEdit";

    @Inject
    private EventLogService eventLogService;

    @Inject
    private SecurityService securityService;

    @Autowired
    private ConfigBean configBean;

    @Autowired
    private CalendarService calendarService;

    private String label = "Manage calendars";

    @Override
    public String getBundleName() {
        return Constants.BUNDLE_NAME;
    }

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public void execute(PortalContext portalContext) {
        if (!portalContext.getCurrentUser().hasAnyPermission(PermissionType.CALENDAR)) {
            LOGGER.error("User {} does not have calendar permissions", portalContext.getCurrentUser().getUsername());
            Messagebox.show(Labels.getLabel("noPermissionGeneral_message"));
            return;
        }

        try {
            boolean canEdit = false;
            // Present the user admin window
            Map arg = new HashMap<>(getSimpleParams());
            Integer logId = (Integer) arg.get("logId");
            User currentUser = securityService.getUserById(portalContext.getCurrentUser().getId());
            if (logId != null) {
                canEdit = ItemHelpers.canModifyCalendar(currentUser, logId);
            }
            arg.put(CAN_EDIT_CONST, canEdit);
            boolean createNewCalendar=arg.get(CREATE_NEW_CALENDAR_CONST)!=null && (boolean)arg.get(CREATE_NEW_CALENDAR_CONST);
            if(createNewCalendar) {
                createNewCalendar(canEdit);
                getSimpleParams().put(CREATE_NEW_CALENDAR_CONST,null);//clear
                return;
            }

            if (canEdit) {
                // Present the calendar window
                Window window = (Window) Executions.getCurrent()
                    .createComponents(PageUtils.getPageDefinition("calendar/zul/calendars.zul"), null, arg);
                window.doModal();
            } else {
                CalendarModel calendarModel = eventLogService.getCalendarFromLog(logId);
                if (calendarModel == null || calendarModel.getId() == null) {
                    Notification.info(getLabel("noAssociatedCalendar_message"));
                } else {
                    viewCalendarReadOnly(calendarModel.getId());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to create custom calendar dialog", e);
        }
    }

    public void viewCalendarReadOnly(Long calendarId) {
        try {
            Map arg = new HashMap<>();
            arg.put("calendarId", calendarId);
            arg.put("isNew", false);
            arg.put(CAN_EDIT_CONST, false);
            Window window = (Window) Executions.getCurrent()
                .createComponents(PageUtils.getPageDefinition("calendar/zul/calendar.zul"), null, arg);
            window.doModal();
        } catch (Exception e) {
            LOGGER.error("Unable to create custom calendar dialog", e);
        }
    }

    private void createNewCalendar(boolean canEdit) {
            String msg;
            try {
                msg = getLabels().getString("created_default_cal_message");
                String calendarName = msg + " " + DateTimeUtils.humanize(LocalDateTime.now());
                CalendarModel calendarModel = calendarService.createBusinessCalendar(calendarName, true, ZoneId.systemDefault().toString());
                Map<String, Object> arg = new HashMap<>();
                arg.put("calendarId", calendarModel.getId());
                arg.put("parentController", this);
                arg.put("isNew", true);
                arg.put(CAN_EDIT_CONST, canEdit);
                arg.put("directCreateNew", true);
                Window window = (Window) Executions.getCurrent()
                    .createComponents(PageUtils.getPageDefinition("calendar/zul/calendar.zul"), null, arg);
                window.doModal();
            } catch (Exception e) {
                msg = getLabels().getString("failed_create_message");
                LOGGER.error(msg, e);
                Notification.error(msg);
            }
    }

    @Override
    public Availability getAvailability() {
        return configBean.isEnableCalendar() &&
            UserSessionManager.getCurrentUser().hasAnyPermission(PermissionType.CALENDAR)
            ? Availability.AVAILABLE : Availability.UNAVAILABLE;
    }
}
