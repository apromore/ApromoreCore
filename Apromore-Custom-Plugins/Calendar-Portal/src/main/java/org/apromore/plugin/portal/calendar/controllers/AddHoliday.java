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

import java.time.LocalDate;
import java.util.Date;

import org.apromore.calendar.model.HolidayModel;
import org.apromore.commons.datetime.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AddHoliday extends SelectorComposer<Window> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AddHoliday.class);

    private Calendar parentController = (Calendar) Executions.getCurrent().getArg().get("parentController");

    @Wire("#holidayDate") Datebox holidayDate;
    @Wire("#holidayDescription") Textbox holidayDescription;
    @Wire("#holidayType") Radiogroup holidayType;

    @Wire("#saveBtn") Button saveBtn;
    @Wire("#cancelBtn") Button cancelBtn;

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);

        saveBtn.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                Date date = holidayDate.getValue();
                LocalDate localDate = TimeUtils.dateToLocalDate(date);
                String description = holidayDescription.getValue();
                String type = holidayType.getSelectedItem().getLabel();
                HolidayModel holiday = new HolidayModel(type,description,description,localDate);
                try {
                    parentController.addHoliday(holiday);
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
