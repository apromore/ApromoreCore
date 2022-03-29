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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.beans.factory.annotation.Configurable;
import lombok.Setter;

@Entity
@Table(name = "job", uniqueConstraints = {@UniqueConstraint(columnNames = {"dag_id"})})
@Configurable("job")
@Setter
public class JobDao implements Serializable {

    private Long id;
    private String dagId;
    private OutputLogInfo logInfo;
    private String schedule;
    private String username;
    private String pipelineName;
    private boolean error;
    private int scheduleVersionNumber;
    private boolean paused;
    private String timeZoneId;
    private boolean isDraft;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    @Column(name = "dag_id")
    public String getDagId() {
        return dagId;
    }

    @Column(name = "schedule")
    public String getSchedule() {
        return schedule;
    }

    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    @Column(name = "pipeline_name")
    public String getPipelineName() {
        return pipelineName;
    }

    @Column(name = "error")
    public boolean getError() {
        return error;
    }

    @Column(name = "schedule_version_number")
    public int getScheduleVersionNumber() {
        return scheduleVersionNumber;
    }

    @Column(name = "paused")
    public boolean getPaused() {
        return paused;
    }

    @Column(name = "time_zone_id")
    public String getTimeZoneId() {
        return timeZoneId;
    }

    @Column(name = "is_draft")
    public boolean isDraft() {
        return isDraft;
    }

    @Embedded
    public OutputLogInfo getLogInfo() {
        return logInfo;
    }
}
