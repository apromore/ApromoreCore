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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usermetadata")
@Configurable("usermetadata")
@Cache(expiry = 180000, size = 100, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Usermetadata implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * The name of user metadata
     */
    private String name;
    /**
     * FK User metadata type id
     */
    private UsermetadataType usermetadataType;
    /**
     * The user create this metadata
     */
    private String createdBy;
    /**
     * Create time
     */
    private String createdTime;
    /**
     * The user updated this metadata
     */
    private String updatedBy;
    /**
     * Last update time
     */
    private String updatedTime;
    /**
     * Content of user metadata
     */
    private String content;
    /**
     * reserve for optimistic lock
     */
    private Integer revision;
    /**
     * Indicate whether this record is valid
     */
    private boolean isValid;
    /**
     * Set of GroupUserMetadata from linked table
     */
    private Set<GroupUsermetadata> groupUserMetadata = new HashSet<>();
    /**
     * Set of UserMetadataLog from linked table
     */
    private Set<UsermetadataLog> usermetadataLogSet = new HashSet<>();
    /**
     * Set of UserMetadataLog from linked table
     */
    private Set<UsermetadataProcess> usermetadataProcessSet = new HashSet<>();

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
     * The name of user metadata
     */
    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    /**
     * The name of user metadata
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * FK User metadata type id
     */
    @ManyToOne
    @JoinColumn(name = "type_id")
    public UsermetadataType getUsermetadataType() {
        return this.usermetadataType;
    }

    /**
     * FK User metadata type id
     */
    public void setUsermetadataType(UsermetadataType usermetadataType) {
        this.usermetadataType = usermetadataType;
    }

    /**
     * The user create this metadata
     */
    @Column(name = "created_by")
    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * The user create this metadata
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Create time
     */
    @Column(name = "created_time")
    public String getCreatedTime() {
        return this.createdTime;
    }

    /**
     * Create time
     */
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * The user updated this metadata
     */
    @Column(name = "updated_by")
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    /**
     * The user updated this metadata
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Last update time
     */
    @Column(name = "updated_time")
    public String getUpdatedTime() {
        return this.updatedTime;
    }

    /**
     * Last update time
     */
    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * Content of user metadata
     */
    @Column(name = "content")
    public String getContent() {
        return this.content;
    }

    /**
     * Content of user metadata
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * reserve for optimistic lock
     */
    @Column(name = "revision")
    public Integer getRevision() {
        return this.revision;
    }

    /**
     * reserve for optimistic lock
     */
    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    /**
     * Indicate whether this record is valid
     */
    @Column(name = "is_valid")
    public boolean getIsValid() {
        return this.isValid;
    }

    /**
     * Indicate whether this record is valid
     */
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Set of GroupUserMetadata from linked table
     */
    @OneToMany(mappedBy = "usermetadata", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<GroupUsermetadata> getGroupUserMetadata() {
        return this.groupUserMetadata;
    }

    public void setGroupUserMetadata(Set<GroupUsermetadata> newGroupUserMetadata) {
        this.groupUserMetadata = newGroupUserMetadata;
    }

    /**
     * Set of UserMetadataLog from linked table
     */
    @OneToMany(mappedBy = "usermetadata", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<UsermetadataLog> getUsermetadataLog() {
        return this.usermetadataLogSet;
    }

    public void setUsermetadataLog(Set<UsermetadataLog> newUsermetadataLogSet) {
        this.usermetadataLogSet = newUsermetadataLogSet;
    }

    /**
     * Set of UserMetadataProcess from linked table
     */
    @OneToMany(mappedBy = "usermetadata", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<UsermetadataProcess> getUsermetadataProcess() {
        return this.usermetadataProcessSet;
    }

    public void setUsermetadataProcess(Set<UsermetadataProcess> newUsermetadataProcessSet) {
        this.usermetadataProcessSet = newUsermetadataProcessSet;
    }

    /**
     * Test equality of another object
     *
     * @return true if equal
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Usermetadata)){
            return false;
        }
        Usermetadata u = (Usermetadata) obj;

        return (this.id.equals(u.id));
    }

    /**
     * Override default hashCode
     *
     * @return hash code value
     */
    @Override
    public int hashCode() {
        if (id == null) {
            return 0;
        }
        return id.hashCode();
    }

}
