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

import lombok.Setter;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "process_publish",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"publishid"})
        }
)
@Configurable("process_publish")
@Setter
public class ProcessPublish {

    private String publishId;
    private boolean published;
    private Process process;

    @Id
    @Column(name = "publishid", unique = true, nullable = false)
    public String getPublishId() {
        return publishId;
    }

    @Column(name = "published")
    public boolean isPublished() {
        return published;
    }

    @OneToOne
    @JoinColumn(name = "processid")
    public Process getProcess() {
        return process;
    }
}
