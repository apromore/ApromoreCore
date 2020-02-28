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
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import static javax.persistence.FetchType.LAZY;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Local classes
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.Predictor;

@Entity
@Table(name = "predictor", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id"}),
    @UniqueConstraint(columnNames = {"name"}),
})
public class PredictorImpl implements Predictor {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictorImpl.class.getCanonicalName());

    private Integer id;
    private String name;
    private String type;
    //private Set<PredictiveMonitorImpl> predictiveMonitorImpls;
    private byte[] /*Blob*/ pkl;

    // Database field accessors

    /** @return primary key */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /** @param newId  the new primary key */
    public void setId(final Integer newId) {
        this.id = newId;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return this.name;
    }

    /** @param newId  the new primary key */
    public void setName(final String newName) {
        this.name = newName;
    }

    @Column(name = "type", nullable = false)
    public String getType() {
        return this.type;
    }

    /** @param newId  the new primary key */
    public void setType(final String newType) {
        this.type = newType;
    }

/*
    @ManyToMany(mappedBy = "predictor")
    public Set<PredictiveMonitorImpl> getPredictiveMonitorImpls() {
        return predictiveMonitorImpls;
    }

    public void setPredictiveMonitorImpls(Set<PredictiveMonitorImpl> newPredictiveMonitorImpls) {
        this.predictiveMonitorImpls = newPredictiveMonitorImpls;
    }
*/

    @Lob //@Basic(fetch=LAZY)
    @Column(name = "pkl", nullable = false)
    public byte[] /*Blob*/ getPkl() {
        return pkl;
    }

    /** @param newPkl  the new pkl data */
    public void setPkl(final byte[] /*Blob*/ newPkl) {
        this.pkl = newPkl;
    }
}
