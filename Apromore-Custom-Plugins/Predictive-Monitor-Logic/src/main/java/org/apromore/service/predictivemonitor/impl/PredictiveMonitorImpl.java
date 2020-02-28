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

// Java 2 Standard Editions
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

// Java 2 Enterprise Edition
import javax.persistence.CascadeType;
import javax.persistence.Column;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

// Third party
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Local classes
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.PredictiveMonitorEvent;
import org.apromore.service.predictivemonitor.Predictor;

@Entity
@Table(name = "predictive_monitor",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"}),
        }
)
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class PredictiveMonitorImpl implements PredictiveMonitor {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictiveMonitorImpl.class.getCanonicalName());

    private PredictiveMonitorRepository predictiveMonitorRepository;

    private Integer id;
    private String name;
    private Set<PredictorImpl> predictorImpls;
    private Set<PredictiveMonitorEventImpl> predictiveMonitorEventImpls;


    // Database field accessors

    /** @return primary key */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    /** @param newId  the new primary key */
    public void setId(final Integer newId) {
        this.id = newId;
    }

    @Column(name = "name", unique = false, nullable = true)
    public String getName() {
        return name;
    }

    /** @param newName  the new name */
    public void setName(final String newName) {
        this.name = newName;
    }

    @ManyToMany
    @JoinTable(name               = "predictive_monitor_predictor",
               joinColumns        = @JoinColumn(name = "predictive_monitor_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "predictor_id",          referencedColumnName = "id"))
    public Set<PredictorImpl> getPredictorImpls() {
        return predictorImpls;
    }

    public void setPredictorImpls(Set<PredictorImpl> newPredictorImpls) {
        predictorImpls = newPredictorImpls;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy="predictiveMonitor")
    public Set<PredictiveMonitorEventImpl> getPredictiveMonitorEventImpls() {
        return predictiveMonitorEventImpls;
    }

    public void setPredictiveMonitorEventImpls(Set<PredictiveMonitorEventImpl> newPredictiveMonitorEventImpls) {
        predictiveMonitorEventImpls = newPredictiveMonitorEventImpls;
    }

    public void close() {
        predictiveMonitorRepository.deleteInBatch(Collections.singleton(this));
        predictiveMonitorRepository = null;
    }

    // PredictiveMonitor interface

    public Set<Predictor> getPredictors() {
        return Collections.unmodifiableSet(getPredictorImpls());
    }

    public Set<PredictiveMonitorEvent> getPredictiveMonitorEvents() {
        return Collections.unmodifiableSet(getPredictiveMonitorEventImpls());
    }
}
