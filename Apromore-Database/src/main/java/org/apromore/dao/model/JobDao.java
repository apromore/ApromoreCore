/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
    private List<DagConnection> connections = new ArrayList<>();
    private List<StaticLog> staticLogs = new ArrayList<>();
    private OutputLogInfo logInfo;
    private String finalTransformQuery;
    private String schedule;
    private String username;
    private String pipelineName;
    private boolean error;
    private int scheduleVersionNumber;
    private boolean paused;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<DagConnection> getConnections() {
        return connections;
    }

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<StaticLog> getStaticLogs() {
        return staticLogs;
    }

    @Column(name = "dag_id")
    public String getDagId() {
        return dagId;
    }

    @Column(name = "final_transform_query")
    public String getFinalTransformQuery() {
        return finalTransformQuery;
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

    @Embedded
    public OutputLogInfo getLogInfo() {
        return logInfo;
    }

    public void synchronizedMetaData() {
        for (DagConnection connection : connections) {
            connection.setJob(this);
        }
        for (StaticLog staticLog : staticLogs) {
            staticLog.setJob(this);
        }
    }
}
