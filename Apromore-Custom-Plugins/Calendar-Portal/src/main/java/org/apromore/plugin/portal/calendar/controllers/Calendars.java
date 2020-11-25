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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.exception.CalendarNotExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.HolidayModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.plugin.portal.calendar.CalendarItemRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

/**
 * Controller for handling calendar interface
 * Corresponds to calendars.zul
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class Calendars extends SelectorComposer<Window> {

    private static Logger LOGGER = LoggerFactory.getLogger(Calendars.class);

    @Wire("#calendarListbox")
    Listbox calendarListbox;
    
    @Wire("#addNewCalendarBtn")
    Button addNewCalender;
    
    @WireVariable("calendarService")
    CalendarService calendarService;

    private ListModelList<CalendarModel> calendarListModel;


    public Calendars() throws Exception {
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
       
        initialize();
    }

    public void initialize() {
        CalendarItemRenderer itemRenderer = new CalendarItemRenderer(calendarService);
        calendarListbox.setItemRenderer(itemRenderer);
        calendarListModel = new ListModelList<CalendarModel>();
        calendarListbox.setModel(calendarListModel);
        mock();
    }

    private void mock() {
      
//      create
    	try {    		
			CalendarModel model=calendarService.createBusinessCalendar("Austrailia 2020", true, ZoneId.systemDefault().toString());
			HolidayModel holiday3 = new HolidayModel("CUSTOM","Test Holiday3", "Test Holiday Desc3", LocalDate.of(2020, 01, 03));
			calendarService.updateHoliday(model.getId(),Arrays.asList(holiday3));
		   
		} catch (CalendarAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CalendarNotExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     List<CalendarModel> calendars=calendarService.getCalendars();
     
        calendarListModel.addAll(calendars);
        
    }

    @Listen("onClick = #okBtn")
    public void onClickOkBtn() {
        getSelf().detach();
    }
    
    @Listen("onClick = #addNewCalendarBtn")
    public void onClickAddNewCalender() {
      Map arg = new HashMap<>();
      arg.put("source", "addNewCalendarBtn");
      Window window = (Window) Executions.getCurrent().createComponents("calendar/zul/calendar.zul", null, arg);
      window.doModal();
    }

}