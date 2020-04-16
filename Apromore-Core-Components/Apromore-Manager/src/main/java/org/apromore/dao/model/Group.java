/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * A group of users for access control.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Entity
@Table(name = "\"group\"",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
                @UniqueConstraint(columnNames = {"row_guid"})
        }
)
@Configurable("group")
@Cache(expiry = 180000, size = 100, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Group implements Serializable {

    private static Logger LOGGER = Logger.getLogger(Group.class.getCanonicalName());

    /**
     * Different types of group.
     *
     * <dl>
     * <dt>USER</dt>   <dd>Every user has a singleton group of their own.</dd>
     * <dt>GROUP</dt>  <dd>General-purpose access control groups; currently there is no UI for creating/editing these</dd>
     * <dt>PUBLIC</dt> <dd>Every user is a member of the (unique) public group.</dd>
     * </dl>
     */
    public enum Type {USER, GROUP, PUBLIC};

    // Column fields
    private Integer id;
    private String rowGuid = UUID.randomUUID().toString();
    private String name;
    private Type type;

    /**
     * Default Constructor.
     */
    public Group() {
    }

    public Group(Integer groupId) {
        this.id = groupId;
    }


    /**
     * Get the primary key for the group.
     *
     * @return the primary key identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Set the primary key for the group.
     *
     * The identifier is intended to be opaque and immutable, so think twice before calling this method.
     *
     * @param newId the new primary key identifier
     */
    public void setId(final Integer newId) {
        this.id = newId;
    }


    /**
     * Get the row unique identifier for the Object.
     * @return Returns the row unique identifier.
     */
    @Column(name = "row_guid", unique = true)
    public String getRowGuid() {
        return rowGuid;
    }

    /**
     * Set the row unique identifier for the Object.
     * @param newRowGuid The row unique identifier to set.
     */
    public void setRowGuid(final String newRowGuid) {
        this.rowGuid = newRowGuid;
    }


    /**
     * @return name of the group
     */
    @Column(name = "name")
    public String getName() {
        return name;
    }

    /**
     * @param newName  the new name for the group
     */
    public void setName(final String newName) {
        this.name = newName;
    }


    /**
     * @return name of the group
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public Type getType() {
        return type;
    }

    /**
     * @param newName  the new name for the group
     */
    public void setType(final Type newType) {
        this.type = newType;
    }


    // Overridden object methods

    @Override
    public int hashCode() { return 113 * (id == null ? 0 : id.hashCode()); }

    @Override
    public boolean equals(java.lang.Object obj) {
       if (obj == null || !Group.class.equals(obj.getClass())) { return false; }
       return (obj != null) && (obj instanceof Group) && id.equals(((Group) obj).id);
    }
}
