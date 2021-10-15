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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apromore.dao.model.User;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.pageutil.PageUtils;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import javax.inject.Inject;

@Component("calendarPlugin")
public class CalendarPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(CalendarPlugin.class);

    @Inject
    private SecurityService securityService;

    private String label = "Manage calendars";

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            boolean canEdit = false;
            // Present the user admin window˙
            Map arg = new HashMap<>(getSimpleParams());
            Integer logId = (Integer) arg.get("logId");
            User currentUser = securityService.getUserById(portalContext.getCurrentUser().getId());
            if (logId != null) {
                canEdit = ItemHelpers.canModifyCalendar(currentUser, logId);
            }
            arg.put("canEdit", canEdit);

            // Present the calendar window
            Window window = (Window) Executions.getCurrent()
                    .createComponents(PageUtils.getPageDefinition("calendar/zul/calendars.zul"), null, arg);
            window.doModal();

        } catch (Exception e) {
            LOGGER.error("Unable to create custom calendar dialog", e);

        }
    }

    @Override
    public Availability getAvailability() {
        return Availability.AVAILABLE;
    }
}
