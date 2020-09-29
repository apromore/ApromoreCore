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

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "custom_calender", uniqueConstraints = { @UniqueConstraint(columnNames = { "description" }) })
@Configurable("custom_calender")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
@NoArgsConstructor
public class CustomCalender implements Serializable {

	private Long id;

	private String description;

	private String created = OffsetDateTime.now().toString();

	private String updated = OffsetDateTime.now().toString();

	private String createdBy;

	private String updatedBy;

	private OffsetDateTime createOffsetDateTime;
	private OffsetDateTime updateOffsetDateTime;

	private List<WorkDay> workDays = new ArrayList<WorkDay>();
	private List<Holiday> holidays = new ArrayList<Holiday>();

	public CustomCalender(String description) {
		this.description = description;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	@Column(name = "created")
	public String getCreated() {
		return created;
	}

	@Column(name = "updated")
	public String getUpdated() {
		return updated;
	}

	@Column(name = "created_by")
	public String getCreatedBy() {
		return createdBy;
	}

	@Column(name = "updated_by")
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public void setUpdated(String updated) {
		this.updated = updated;

	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Transient
	public OffsetDateTime getCreateOffsetDateTime() {
		createOffsetDateTime = OffsetDateTime.parse(created);
		return createOffsetDateTime;
	}

	@Transient
	public OffsetDateTime getUpdateOffsetDateTime() {
		updateOffsetDateTime = OffsetDateTime.parse(updated);
		return updateOffsetDateTime;
	}

	@OneToMany(mappedBy = "customCalender", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<WorkDay> getWorkDays() {
		return workDays;
	}
	
	
	public void setWorkDays(List<WorkDay> workDays) {
		this.workDays = workDays;
	}

	public void addWorkDay(WorkDay workDay) {
		workDays.add(workDay);
		workDay.setCustomCalender(this);
	}

	public void removeWorkDay(WorkDay workDay) {
		workDays.remove(workDay);
		workDay.setCustomCalender(null);
	}
	
	public void addHoliday(Holiday holiday) {
		holidays.add(holiday);
		holiday.setCustomCalender(this);
	}

	public void removeHoliday(Holiday holiday) {
		holidays.remove(holiday);
		holiday.setCustomCalender(null);
	}

	@OneToMany(mappedBy = "customCalender", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Holiday> getHolidays() {
		return holidays;
	}

	public void setHolidays(List<Holiday> holidays) {
		this.holidays = holidays;
	}
	
	

}
