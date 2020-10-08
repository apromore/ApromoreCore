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
package org.apromore.dao.model;

import java.time.DayOfWeek;
import java.time.OffsetTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "work_day", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "day_of_week", "calendar_id", "start_time" }) })
@Configurable("work_day")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
@NoArgsConstructor
public class WorkDay {

	private Long id;
	private DayOfWeek dayOfWeek;

	private String startTimeString;
	

	private String endTimeString;
 
	private boolean isWorkingDay = true;

	private String createdBy;
	private String updatedBy;

	private CustomCalendar customCalendar;

	public WorkDay(DayOfWeek dayOfWeek, OffsetTime startTime, OffsetTime endTime, boolean isWorkingDay) {
		this.dayOfWeek = dayOfWeek;
		this.startTimeString = startTime.toString();		
		this.endTimeString = endTime.toString();
		this.isWorkingDay = isWorkingDay;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	@Column(name = "day_of_week")
	@Enumerated(EnumType.STRING)
	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	@Column(name = "start_time")
	public String getStartTimeString() {
		return startTimeString;
	}

	@Column(name = "end_time")
	public String getEndTimeString() {

		return endTimeString;
	}

	@Column(name = "is_working_day")
	public boolean isWorkingDay() {
		return isWorkingDay;
	}

	@ManyToOne
	@JoinColumn(name = "calendar_id")
	public CustomCalendar getCustomCalendar() {
		return customCalendar;
	}

	@Column(name = "created_by")
	public String getCreatedBy() {
		return createdBy;
	}

	@Transient
	public OffsetTime getStartTime() {
		return OffsetTime.parse(startTimeString);		
	}

	@Column(name = "updated_by")
	public String getUpdatedBy() {
		return updatedBy;
	}

	@Transient
	public OffsetTime getEndTime() {
		return OffsetTime.parse(endTimeString);		
	}

	public void setCustomCalendar(CustomCalendar customCalendar) {
		this.customCalendar = customCalendar;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
	}

	public void setEndTimeString(String endTimeString) {
		this.endTimeString = endTimeString;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	
	public void setWorkingDay(boolean isWorkingDay) {
		this.isWorkingDay = isWorkingDay;
	}

}
