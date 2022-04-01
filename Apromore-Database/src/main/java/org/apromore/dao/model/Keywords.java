/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Configurable;

/**
 * JPA facade to an SQL view that calculates the searchable text attributes of processes, logs, and folders.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Entity
@Table(name = "keywords")
@Configurable("keywords")
public class Keywords implements Serializable {

    private String value;
    private String type;
    private Integer processId;
    private Integer logId;
    private Integer folderId;

    @Id  // this is a kludge to fool the EclipseLink validator into thinking we have a primary key
    @Column(name = "value", unique = false, nullable = true, length = 250)
    public String getValue() { return value; }
    public void setValue(final String newValue) { this.value = newValue; }

    @Column(name = "type", unique = false, nullable = false)
    public String getType() { return this.type; }
    public void setType(final String newType) { this.type = newType; }

    @Column(name = "processid", unique = false, nullable = true)
    public Integer getProcessId() { return this.processId; }
    public void setProcessId(final Integer id) { this.processId = id; }

    @Column(name = "logid", unique = false, nullable = true)
    public Integer getLogId() { return this.logId; }
    public void setLogId(final Integer id) { this.logId = id; }

    @Column(name = "folderid", unique = false, nullable = true)
    public Integer getFolderId() { return this.folderId; }
    public void setFolderId(final Integer id) { this.folderId = id; }
}
