/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */

package org.apromore.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the Annotation in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "keywords")
@Configurable("keywords")
@Cache(expiry = 180000, size = 200, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Keywords implements Serializable {

    private Integer processId;
    private String value;

    /**
     * Default Constructor.
     */
    public Keywords() { }



    @Id
    @Column(name = "processId", unique = true, nullable = false)
    public Integer getProcessId() {
        return this.processId;
    }

    public void setProcessId(final Integer id) {
        this.processId = id;
    }


    @Column(name = "value", unique = false, nullable = true, length = 250)
    public String getValue() {
        return value;
    }

    public void setValue(final String newValue) {
        this.value = newValue;
    }

}
