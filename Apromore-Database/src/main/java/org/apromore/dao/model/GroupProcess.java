/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * The access control details corresponding to a particular group and process.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Entity
@Table(name = "group_process")
@Configurable("group_process")
@Cache(expiry = 180000, size = 100, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class GroupProcess implements Serializable {

    private static Logger LOGGER = Logger.getLogger(GroupProcess.class.getCanonicalName());

    private Integer id;

    private boolean hasRead;
    private boolean hasWrite;
    private boolean hasOwnership;

    private Group   group;
    private Process process;

    /**
     * Default Constructor.
     */
    public GroupProcess() {
    }

    /**
     * Convenient constructor.
     */
    public GroupProcess(Process newProcess, Group newGroup, boolean newHasRead, boolean newHasWrite, boolean newHasOwnership) {
        this.process      = newProcess;
        this.group        = newGroup;
        this.hasRead      = newHasRead;
        this.hasWrite     = newHasWrite;
        this.hasOwnership = newHasOwnership;
    }

    /**
     * Get the Primary Key for the Object.
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
     * @param newId The role name to set.
     */
    public void setId(final Integer newId) {
        this.id = newId;
    }


    /**
     * @return whether the group has read access to the process model
     */
    @Column(name = "has_read")
    public boolean getHasRead() {
        return this.hasRead;
    }

    /**
     * @param newHasRead  whether the group should have read access to the process model
     */
    public void setHasRead(final boolean newHasRead) {
        this.hasRead = newHasRead;
    }


    /**
     * @return whether the group has write access to the process model
     */
    @Column(name = "has_write")
    public boolean getHasWrite() {
        return this.hasWrite;
    }

    /**
     * @param newHasWrite  whether the group should have write access to the process model
     */
    public void setHasWrite(final boolean newHasWrite) {
        this.hasWrite = newHasWrite;
    }


    /**
     * @return whether the group has ownership of the process model
     */
    @Column(name = "has_ownership")
    public boolean getHasOwnership() {
        return this.hasOwnership;
    }

    /**
     * @param newHasRead  whether the group should have ownership of the process model
     */
    public void setHasOwnership(final boolean newHasOwnership) {
        this.hasOwnership = newHasOwnership;
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
    @JoinColumn(name = "processId")
    public Process getProcess() {
        return this.process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

}
