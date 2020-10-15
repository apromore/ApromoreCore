/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * The access control details corresponding to a particular group and process.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Entity
@Table(name = "group_log")
@Configurable("group_log")
@Cache(expiry = 180000, size = 100,
    coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class GroupLog implements Serializable {

  private static Logger LOGGER = Logger.getLogger(GroupLog.class.getCanonicalName());

  private Integer id;

  private AccessRights accessRights;

  private Group group;
  private Log log;

  /**
   * Default Constructor.
   */
  public GroupLog() {}

  /**
   * Convenient constructor.
   */
  public GroupLog(Group newGroup, Log newLog, AccessRights accessRights) {
    this.group = newGroup;
    this.log = newLog;
    this.accessRights = accessRights;
  }

  public GroupLog(Group group, Log log, boolean isRead, boolean isWrite, boolean isOwnerShip) {
     this(group, log, new  AccessRights(isRead,isWrite,isOwnerShip));
  }

  /**
   * Get the Primary Key for the Object.
   * 
   * @return Returns the Id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  public Integer getId() {
    return id;
  }

  /**
   * Set the id for the Object.
   * 
   * @param newId The role name to set.
   */
  public void setId(final Integer newId) {
    this.id = newId;
  }



  @Embedded
  public AccessRights getAccessRights() {
    return accessRights;
  }

  public void setAccessRights(AccessRights accessRights) {
    this.accessRights = accessRights;
  }

  @ManyToOne
  @JoinColumn(name = "groupId")
  public Group getGroup() {
    return this.group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  @ManyToOne
  @JoinColumn(name = "logId")
  public Log getLog() {
    return this.log;
  }

  public void setLog(Log log) {
    this.log = log;
  }

  public Boolean getHasRead() {
    // TODO Auto-generated method stub
    return getAccessRights().isReadOnly();
  }

  public Boolean getHasWrite() {
    // TODO Auto-generated method stub
    return getAccessRights().isWriteOnly();
  }

  public Boolean getHasOwnership() {
    // TODO Auto-generated method stub
    return getAccessRights().isOwnerShip();
  }

}
