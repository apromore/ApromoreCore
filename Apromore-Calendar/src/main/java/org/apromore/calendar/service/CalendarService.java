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
package org.apromore.calendar.service;

import java.util.List;
import org.apromore.calendar.exception.CalendarAlreadyExistsException;
import org.apromore.calendar.exception.CalendarNotExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.HolidayModel;
import org.apromore.calendar.model.WorkDayModel;
import org.apromore.dao.model.CustomCalendar;

public interface CalendarService {

	final String EVENT_TOPIC = "org/apromore/service/CALENDAR";

	public CalendarModel createGenericCalendar(String description, boolean weekendsOff, String zoneId)
			throws CalendarAlreadyExistsException;

	CalendarModel getGenericCalendar();

	public CalendarModel createBusinessCalendar(String description, boolean weekendsOff, String zoneId)
			throws CalendarAlreadyExistsException;

	public CalendarModel getCalendar(Long id);

	public List<CalendarModel> getCalendars();

	public void deleteCalendar(Long calendarId);

	public void updateHoliday(Long id, List<HolidayModel> holidayModels) throws CalendarNotExistsException;
	
	public void updateCalendarName(Long id, String calendarName) throws CalendarNotExistsException;

	public void updateWorkDays(Long id, List<WorkDayModel> model) throws CalendarNotExistsException;

	public void updateZoneInfo(Long id, String zoneId) throws CalendarNotExistsException;;
	

}
