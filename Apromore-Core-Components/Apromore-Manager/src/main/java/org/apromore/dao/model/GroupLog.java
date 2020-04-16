/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
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
@Cache(expiry = 180000, size = 100, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class GroupLog implements Serializable {

    private static Logger LOGGER = Logger.getLogger(GroupLog.class.getCanonicalName());

    private Integer id;

    private boolean hasRead;
    private boolean hasWrite;
    private boolean hasOwnership;

    private Group   group;
    private Log log;

    /**
     * Default Constructor.
     */
    public GroupLog() {
    }

    /**
     * Convenient constructor.
     */
    public GroupLog(Group newGroup, Log newLog, boolean newHasRead, boolean newHasWrite, boolean newHasOwnership) {
        this.group        = newGroup;
        this.log          = newLog;
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
     * @param newHasOwnership  whether the group should have ownership of the process model
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
    @JoinColumn(name = "logId")
    public Log getLog() {
        return this.log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

}
