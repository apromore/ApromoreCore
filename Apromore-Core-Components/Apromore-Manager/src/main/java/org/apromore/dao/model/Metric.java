/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Stores the Metrics for models in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "metric",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"processModelVersionId", "name"})
        }
)
@Configurable("metric")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Metric implements Serializable {

    private Integer id;
    private String name;
    private Double value;
    private String lastUpdateDate;

    private ProcessModelVersion processModelVersion;


    /**
     * Default Constructor.
     */
    public Metric() { }



    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }


    @Column(name = "name", unique = false, nullable = true, length = 40)
    public String getName() {
        return name;
    }

    public void setName(final String newName) {
        this.name = newName;
    }


    @Column(name = "value")
    public Double getValue() {
        return value;
    }

    public void setValue(final Double newValue) {
        this.value = newValue;
    }

    @Column(name = "lastUpdateDate")
    public String getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(final String newLastUpdate) {
        this.lastUpdateDate = newLastUpdate;
    }



    @ManyToOne
    @JoinColumn(name = "processModelVersionId")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(final ProcessModelVersion newProcessModelVersion) {
        this.processModelVersion = newProcessModelVersion;
    }

}
