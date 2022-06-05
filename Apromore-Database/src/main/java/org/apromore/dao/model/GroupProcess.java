/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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
import java.util.Objects;
import java.util.logging.Logger;
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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The access control details corresponding to a particular group and process.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Entity
@Table(name = "group_process")
@Configurable("group_process")
@Getter
@Setter
@ToString
public class GroupProcess implements Serializable {

    private static Logger LOGGER = Logger.getLogger(GroupProcess.class.getCanonicalName());

    private Integer id;

    private AccessRights accessRights = new AccessRights();

    private Group group;
    private Process process;

    /**
     * Default Constructor.
     */
    public GroupProcess() {
    }

    /**
     * Convenient constructor.
     */
    public GroupProcess(Process newProcess, Group newGroup, AccessRights accessRights) {
        this.process = newProcess;
        this.group = newGroup;
        this.accessRights = accessRights;
    }

    public GroupProcess(Process process, Group group, boolean hasRead, boolean hasWrite, boolean hasOwnerShip) {
        this(process, group, new AccessRights(hasRead, hasWrite, hasOwnerShip));
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
    @JoinColumn(name = "groupid")
    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @ManyToOne
    @JoinColumn(name = "processid")
    public Process getProcess() {
        return this.process;
    }

    public void setProcess(Process process) {
        this.process = process;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        GroupProcess that = (GroupProcess) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
