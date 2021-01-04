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

/*
 * This is a Pojo which is a DTO to Holiday model in JPA.
 * This contains all holiday information which is associated with a calendar.
 * This is used in calculation of duration, where the number of hours is 0 for a holiday period.
 */

package org.apromore.calendar.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import org.apromore.commons.datetime.TimeUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class HolidayModel implements Serializable {

	@EqualsAndHashCode.Exclude
	private Long id;
	@EqualsAndHashCode.Exclude
	private Long referenceId;

	private String holidayType = "PUBLIC";

	private String name;
	private String description;
	private LocalDate holidayDate;

	@EqualsAndHashCode.Exclude
	private String createdBy;

	@EqualsAndHashCode.Exclude
	private String updatedBy;

	
	public HolidayModel(String holidayType, String name, String description, LocalDate holidayDate) {
		super();
		this.holidayType = holidayType;
		this.name = name;
		this.description = description;
		this.holidayDate = holidayDate;
	}


	public HolidayModel() {
		super();
	}
	
	public Date getDate()
	{
		return TimeUtils.localDateToDate(holidayDate);
		
	}

}
