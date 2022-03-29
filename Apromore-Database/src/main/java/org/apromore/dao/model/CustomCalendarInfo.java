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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.springframework.beans.factory.annotation.Configurable;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "custom_calendar", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@Configurable("custom_calendar")
@NoArgsConstructor
public class CustomCalendarInfo implements Serializable {

  private Long id;

  private String name;

  // This will change when we upgrade Spring and jpa
  private String created = OffsetDateTime.now().toString();

  // This will change when we upgrade Spring and jpa
  private String updated = OffsetDateTime.now().toString();

  

  @Id
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

  @Transient
  public OffsetDateTime getCreateOffsetDateTime() {
    return OffsetDateTime.parse(created);

  }

  @Transient
  public OffsetDateTime getUpdateOffsetDateTime() {
    return OffsetDateTime.parse(updated);
  }



}
