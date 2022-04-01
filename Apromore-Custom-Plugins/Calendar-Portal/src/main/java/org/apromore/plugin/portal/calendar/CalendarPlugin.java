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

package org.apromore.plugin.portal.calendar;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.config.ConfigBean;
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
    private static final String FOWARD_FROM_CONTEXT_CONST = "FOWARD_FROM_CONTEXT";
    private static final String CAN_EDIT_CONST = "canEdit";
    private static final String CAN_DELETE_CONST = "canDelete";

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
            boolean canDelete = false;
            // Present the user admin window
            Map arg = new HashMap<>(getSimpleParams());
            Integer logId = (Integer) arg.get("logId");
            User currentUser = securityService.getUserById(portalContext.getCurrentUser().getId());
            if (logId != null) {
                canEdit = ItemHelpers.canModifyCalendar(currentUser, logId);
                canDelete = ItemHelpers.canDeleteCalendar(currentUser, logId);
            }
            arg.put(CAN_EDIT_CONST, canEdit);
            arg.put(CAN_DELETE_CONST, canDelete);
            boolean forwardFromContext =
                arg.get(FOWARD_FROM_CONTEXT_CONST) != null && (boolean) arg.get(FOWARD_FROM_CONTEXT_CONST);
            if (forwardFromContext) {
                editOrCreateNewCalendar(arg);
                getSimpleParams().put(FOWARD_FROM_CONTEXT_CONST, null);//clear
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

    private void editOrCreateNewCalendar(Map<String, Object> arg) {
        try {
            if (arg != null) {
                Executions.getCurrent()
                    .createComponents(PageUtils.getPageDefinition("calendar/zul/calendars.zul"), null, arg);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to edit or create custom calendar dialog", e);
        }
    }

    @Override
    public Availability getAvailability() {
        return configBean.isEnableCalendar()
            && UserSessionManager.getCurrentUser().hasAnyPermission(PermissionType.CALENDAR)
            ? Availability.AVAILABLE : Availability.UNAVAILABLE;
    }
}
