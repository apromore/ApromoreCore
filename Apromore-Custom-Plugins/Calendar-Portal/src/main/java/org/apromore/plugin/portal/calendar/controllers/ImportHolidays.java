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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apromore.calendar.model.HolidayType;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.plugin.portal.calendar.Constants;
import org.apromore.plugin.portal.calendar.model.CalendarFactory;
import org.apromore.plugin.portal.calendar.model.Holiday;
import org.apromore.zk.label.LabelSupplier;
import org.slf4j.Logger;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class ImportHolidays extends SelectorComposer<Window> implements LabelSupplier {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ImportHolidays.class);

    private CalendarController parentController = (CalendarController) Executions.getCurrent().getArg()
        .get("parentController");

    @Wire("#saveBtn")
    Button saveBtn;
    @Wire("#cancelBtn")
    Button cancelBtn;

    @Override
    public String getBundleName() {
        return Constants.BUNDLE_NAME;
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        Clients.clearBusy();

        saveBtn.addEventListener("onSubmit", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                List<Holiday> holidays = new ArrayList<Holiday>();
                Object[] params = (Object[]) event.getData();
                for (Object param : params) {
                    JSONObject item = (JSONObject) param;
                    String name = (String) item.get("name");
                    String date = (String) item.get("date");
                    holidays.add(CalendarFactory.INSTANCE.createHoliday(HolidayType.PUBLIC, name, name,
                        LocalDate.parse(date)));
                }
                if (holidays.size() == 0) {
                    return;
                }
                try {
                    parentController.addHolidays(holidays);
                    getSelf().detach();
                } catch (Exception e) {
                    LOGGER.error("Error", e);
                }
            }
        });
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelButton() {
        getSelf().detach();
    }
}
