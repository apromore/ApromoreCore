/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the process in apromore.
 * @author Cameron James
 */
@Entity
@Table(name = "workspace",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
                @UniqueConstraint(columnNames = {"workspace_name"})
        }
)
@Configurable("workspace")
public class Workspace implements Serializable {

    private Integer id;
    private String name;
    private String description;
    private Date dateCreated;

    private User createdBy;

    private Set<Folder> folders = new HashSet<>();

    /**
     * Default Constructor.
     */
    public Workspace() {
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
     * Get the role name for the Object.
     * @return Returns the role name.
     */
    @Column(name = "workspace_name")
    public String getName() {
        return name;
    }

    /**
     * Set the role name for the Object.
     * @param newName The role name to set.
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Get the description for the Object.
     * @return Returns the description description.
     */
    @Column(name = "workspace_description")
    public String getDescription() {
        return description;
    }

    /**
     * Set the description for the Object.
     * @param newDescription The workspace description to set.
     */
    public void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    /**
     * Get the date created for the Object.
     * @return Returns the date created.
     */
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Set the date created for the Object.
     * @param newDateCreated The date created to set.
     */
    public void setDateCreated(final Date newDateCreated) {
        this.dateCreated = newDateCreated;
    }


    /**
     * Get the created by for the Object.
     * @return Returns the createdBy.
     */
    @ManyToOne
    @JoinColumn(name = "userid")
    public User getCreatedBy() {
        return this.createdBy;
    }

    /**
     * Set the created by for the Object.
     * @param newCreatedBy The created by to set.
     */
    public void setCreatedBy(final User newCreatedBy) {
        this.createdBy = newCreatedBy;
    }


    /**
     * Getter for the folders collection.
     * @return Returns folders.
     */
    @OneToMany(mappedBy = "workspace")
    public Set<Folder> getFolders() {
        return this.folders;
    }

    /**
     * Setter for the folders Collection.
     * @param newFolders The folders to set.
     */
    public void setFolders(Set<Folder> newFolders) {
        this.folders = newFolders;
    }

}
