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

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "group_usermetadata")
@Configurable("group_usermetadata")
@Cache(expiry = 180000, size = 100, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
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
    /**
     * Has read permission
     */
    private boolean hasRead;
    /**
     * Has write permission
     */
    private boolean hasWrite;
    /**
     * Has owner permission
     */
    private boolean hasOwnership;

    /**
     * Default Constructor.
     */
    public GroupUsermetadata() {
    }

    /**
     * Convenient constructor.
     */
    public GroupUsermetadata(Group newGroup, Usermetadata newUsermetadata, boolean newHasRead, boolean newHasWrite,
                             boolean newHasOwnership) {
        this.group = newGroup;
        this.usermetadata = newUsermetadata;
        this.hasRead = newHasRead;
        this.hasWrite = newHasWrite;
        this.hasOwnership = newHasOwnership;
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

    /**
     * Has read permission
     */
    @Column(name = "has_read")
    public boolean getHasRead() {
        return this.hasRead;
    }

    /**
     * Has read permission
     */
    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    /**
     * Has write permission
     */
    @Column(name = "has_write")
    public boolean getHasWrite() {
        return this.hasWrite;
    }

    /**
     * Has write permission
     */
    public void setHasWrite(boolean hasWrite) {
        this.hasWrite = hasWrite;
    }

    /**
     * Has owner permission
     */
    @Column(name = "has_ownership")
    public boolean getHasOwnership() {
        return this.hasOwnership;
    }

    /**
     * Has owner permission
     */
    public void setHasOwnership(boolean hasOwnership) {
        this.hasOwnership = hasOwnership;
    }
}
