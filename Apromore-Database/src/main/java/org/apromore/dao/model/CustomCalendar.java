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
package org.apromore.dao.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.springframework.beans.factory.annotation.Configurable;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "custom_calendar", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@Configurable("custom_calendar")
@NoArgsConstructor
public class CustomCalendar implements Serializable {

	private Long id;

	private String name;

	// This will change when we upgrade Spring and jpa
	private String created = OffsetDateTime.now().toString();

	// This will change when we upgrade Spring and jpa
	private String updated = OffsetDateTime.now().toString();

	private String createdBy;

	private String updatedBy;

	private User user;

	private List<WorkDay> workDays = new ArrayList<WorkDay>();
	private List<Holiday> holidays = new ArrayList<Holiday>();

	private String zoneId;

	public CustomCalendar(String name, ZoneId zoneId) {
		this.name = name;
		this.zoneId = zoneId.toString();
	}

	public CustomCalendar(String name) {
		this(name, ZoneId.systemDefault());
	}


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	@Column(name = "name")
	public String getName() {
		return name;
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

	public void setName(String name) {
		this.name = name;
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
		return OffsetDateTime.parse(created);

	}

	@Transient
	public OffsetDateTime getUpdateOffsetDateTime() {
		return OffsetDateTime.parse(updated);
	}

	@OneToMany(mappedBy = "customCalendar", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<WorkDay> getWorkDays() {
		return workDays;
	}

	public void setWorkDays(List<WorkDay> workDays) {
		this.workDays = workDays;
	}

	public void addWorkDay(WorkDay workDay) {
		workDays.add(workDay);
		workDay.setCustomCalendar(this);
	}

	public void removeWorkDay(WorkDay workDay) {
		workDays.remove(workDay);
		workDay.setCustomCalendar(null);
	}

	public void addHoliday(Holiday holiday) {
		holidays.add(holiday);
		holiday.setCustomCalendar(this);
	}

	public void removeHoliday(Holiday holiday) {
		holidays.remove(holiday);
		holiday.setCustomCalendar(null);
	}

	@OneToMany(mappedBy = "customCalendar", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Holiday> getHolidays() {
		return holidays;
	}

	public void setHolidays(List<Holiday> holidays) {
		this.holidays = holidays;
	}

	@Column(name = "zone_id")
	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	@ManyToOne
	@JoinColumn(name = "owner")
	public User getUser() {
		return this.user;
	}

	public void setUser(final User owner) {
		this.user = owner;
	}

}
