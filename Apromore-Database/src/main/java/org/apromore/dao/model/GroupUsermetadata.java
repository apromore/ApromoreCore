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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Table(name = "group_usermetadata")
@Configurable("group_usermetadata")
public class GroupUsermetadata implements Serializable {

    /**
     * ID
     */
    private Integer id;
    /**
     * FK GROUP ID
     */
    private Group group;
    /**
     * FK USER METADATA ID
     */
    private Usermetadata usermetadata;

    private AccessRights accessRights;

    /**
     * Default Constructor.
     */
    public GroupUsermetadata() {
    }

    /**
     * Convenient constructor.
     */
    public GroupUsermetadata(Group newGroup, Usermetadata newUsermetadata, AccessRights accessRights) {
        this.group = newGroup;
        this.usermetadata = newUsermetadata;
        this.accessRights = accessRights;
    }

    public GroupUsermetadata(Group group, Usermetadata usermetadata, boolean isRead, boolean isWrite,
                             boolean isOwnerShip) {
        this(group, usermetadata, new AccessRights(isRead, isWrite, isOwnerShip));
    }

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    @Embedded
    public AccessRights getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(AccessRights accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * FK GROUP ID
     */
    @ManyToOne
    @JoinColumn(name = "group_id")
    public Group getGroup() {
        return this.group;
    }

    /**
     * FK GROUP ID
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * FK USER METADATA ID
     */
    @ManyToOne
    @JoinColumn(name = "usermetadata_id")
    public Usermetadata getUsermetadata() {
        return this.usermetadata;
    }

    /**
     * FK USER METADATA ID
     */
    public void setUsermetadata(Usermetadata usermetadata) {
        this.usermetadata = usermetadata;
    }

    @Transient
    public Boolean getHasRead() {
        // TODO Auto-generated method stub
        return getAccessRights().isReadOnly();
    }

    @Transient
    public Boolean getHasWrite() {
        // TODO Auto-generated method stub
        return getAccessRights().isWriteOnly();
    }

    @Transient
    public Boolean getHasOwnership() {
        // TODO Auto-generated method stub
        return getAccessRights().isOwnerShip();
    }

}
