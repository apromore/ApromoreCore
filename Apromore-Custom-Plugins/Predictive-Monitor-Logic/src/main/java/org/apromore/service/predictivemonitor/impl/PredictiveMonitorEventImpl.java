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

package org.apromore.service.predictivemonitor.impl;

// Java 2 Standard Edition
import java.sql.Blob;
import java.util.Set;

// Java 2 Enterprise Edition
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Local classes
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.PredictiveMonitorEvent;

@Entity
@Table(name = "predictive_monitor_event", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"})
})
public class PredictiveMonitorEventImpl implements PredictiveMonitorEvent {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictiveMonitorEventImpl.class.getCanonicalName());

    private Integer               id;
    private PredictiveMonitorImpl predictiveMonitor;
    private String                caseId;
    private Integer               eventNr;
    private String                json;

    // Database field accessors

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer newId) {
        this.id = newId;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "predictive_monitor_id", nullable = false, updatable = false)
    public PredictiveMonitorImpl getPredictiveMonitor() {
        return predictiveMonitor;
    }

    public void setPredictiveMonitor(final PredictiveMonitorImpl newPredictiveMonitor) {
        this.predictiveMonitor = newPredictiveMonitor;
    }

    @Column(name = "case_id", nullable = false)
    public String getCaseId() {
        return this.caseId;
    }

    public void setCaseId(final String newCaseId) {
        this.caseId = newCaseId;
    }

    @Column(name = "event_nr", nullable = false)
    public Integer getEventNr() {
        return this.eventNr;
    }

    public void setEventNr(final Integer newEventNr) {
        this.eventNr = newEventNr;
    }

    @Column(name = "json", nullable = false)
    public String getJson() {
        return json;
    }

    public void setJson(final String newJson) {
        this.json = newJson;
    }
}
