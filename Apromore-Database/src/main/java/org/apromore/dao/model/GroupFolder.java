/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

/**
 * The access control details corresponding to a particular group and folder.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Entity
@Table(name = "group_folder")
@Configurable("group_folder")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class GroupFolder implements Serializable {

    private Integer id;
    private AccessRights accessRights;

    private Group group;
    private Folder folder;


    /**
     * Default Constructor.
     */
    public GroupFolder() {
    }

    /**
     * Convenient constructor.
     */
    public GroupFolder(Group newGroup, Folder newFolder, AccessRights accessRights) {
        this.group = newGroup;
        this.folder = newFolder;
        this.accessRights = accessRights;
    }

    public GroupFolder(Group group, Folder folder, boolean hasRead, boolean hasWrite, boolean hasOwnerShip) {
        this(group, folder, new AccessRights(hasRead, hasWrite, hasOwnerShip));
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


    @ManyToOne
    @JoinColumn(name = "groupId", nullable = false)
    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @ManyToOne
    @JoinColumn(name = "folderId", nullable = true)
    public Folder getFolder() {
        return this.folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    @Embedded
    public AccessRights getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(AccessRights accessRights) {
        this.accessRights = accessRights;
    }

    public boolean isHasRead() {
        // TODO Auto-generated method stub
        return accessRights.isReadOnly();
    }

    public boolean isHasWrite() {
        // TODO Auto-generated method stub
        return accessRights.isWriteOnly();
    }

    public boolean isHasOwnership() {
        // TODO Auto-generated method stub
        return accessRights.isOwnerShip();
    }


}
