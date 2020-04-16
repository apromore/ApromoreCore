/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

/**
 *
 */
package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Cluster Assignment.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Entity
@Table(name = "cluster_assignment")
@Configurable("clusterAssignment")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class ClusterAssignment implements Serializable {

    private Integer id;
    private String cloneId;
    private Boolean maximal;
    private Integer coreObjectNb;

    private Cluster cluster;
    private FragmentVersion fragment;


    /**
     * Public Constructor.
     */
    public ClusterAssignment() { }



    /**
     * returns the Id of this Object.
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Sets the Id of this Object
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }



    @ManyToOne
    @JoinColumn(name = "fragmentVersionId")
    public FragmentVersion getFragment() {
        return this.fragment;
    }

    public void setFragment(FragmentVersion newFragment) {
        this.fragment = newFragment;
    }

    @ManyToOne
    @JoinColumn(name = "clusterId")
    public Cluster getCluster() {
        return this.cluster;
    }

    public void setCluster(Cluster newClusters) {
        this.cluster = newClusters;
    }

    @Column(name = "clone_id", length = 40)
    public String getCloneId() {
        return this.cloneId;
    }

    public void setCloneId(String newCloneId) {
        this.cloneId = newCloneId;
    }

    @Column(name = "maximal")
    public Boolean getMaximal() {
        return this.maximal;
    }

    public void setMaximal(Boolean newMaximal) {
        this.maximal = newMaximal;
    }

    @Column(name = "core_object_nb")
    public Integer getCoreObjectNb() {
        return this.coreObjectNb;
    }

    public void setCoreObjectNb(Integer newCoreObjectNb) {
        this.coreObjectNb = newCoreObjectNb;
    }
}
