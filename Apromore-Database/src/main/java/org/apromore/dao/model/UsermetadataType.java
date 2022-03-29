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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Table(name = "usermetadata_type")
@Configurable("usermetadata_type")
public class UsermetadataType implements Serializable {
    /**
     * ID
     */
    private Integer id;
    /**
     * Metadata type
     */
    private String type;
    /**
     * Metadata type
     */
    private Integer version;
    /**
     * Indicate whether this record is valid
     */
    private boolean isValid;

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
     * Metadata type
     */
    @Column(name = "type")
    public String getType() {
        return this.type;
    }

    /**
     * Metadata type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Metadata type
     */
    @Column(name = "version")
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Metadata type
     */
    public void setVersion(Integer version) {
        this.version = version;
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
}
