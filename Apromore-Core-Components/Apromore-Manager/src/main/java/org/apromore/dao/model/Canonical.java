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

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Stores the Canonical in apromore.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "canonical")
@Configurable("canonical")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Canonical implements Serializable {

    private Integer id;
    private String content;
    private String lastUpdateDate;

    private ProcessModelVersion processModelVersion;

    /**
     * Default Constructor.
     */
    public Canonical() { }



    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }


    @Lob
    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(final String newContent) {
        this.content = newContent;
    }

    @Column(name = "lastUpdateDate")
    public String getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    public void setLastUpdateDate(final String newLastUpdate) {
        this.lastUpdateDate = newLastUpdate;
    }


    @OneToOne(mappedBy = "canonicalDocument")
    public ProcessModelVersion getProcessModelVersion() {
        return this.processModelVersion;
    }

    public void setProcessModelVersion(ProcessModelVersion newProcessModelVersion) {
        this.processModelVersion = newProcessModelVersion;
    }

}
