/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

/**
 *
 */
package org.apromore.dao.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Cluster Data Object.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Entity
@Table(name = "cluster")
@Configurable("cluster")
@Cache(expiry = 180000, size = 1000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class Cluster implements Serializable {

    private Integer id;
    private int size = 0;
    private float avgFragmentSize = 0;
    private Integer medoidId = null;
    private double standardizingEffort = 0;
    private double BCR = 0;
    private int refactoringGain = 0;

//    private Folder folder;

    private Set<ClusterAssignment> clusterAssignments = new HashSet<>();

    /**
     * Public Constructor.
     */
    public Cluster() { }



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


    @Column(name = "size")
    public int getSize() {
        return size;
    }

    public void setSize(final int newSize) {
        this.size = newSize;
    }

    @Column(name = "avg_fragment_size")
    public float getAvgFragmentSize() {
        return avgFragmentSize;
    }

    public void setAvgFragmentSize(final float newAvgFragmentSize) {
        this.avgFragmentSize = newAvgFragmentSize;
    }

    @Column(name = "medoid_id")
    public Integer getMedoidId() {
        return medoidId;
    }

    public void setMedoidId(final Integer newMdoidId) {
        this.medoidId = newMdoidId;
    }

    @Column(name = "benifit_cost_ratio")
    public double getBCR() {
        return BCR;
    }

    public void setBCR(final double newBCR) {
        this.BCR = newBCR;
    }

    @Column(name = "std_effort")
    public double getStandardizingEffort() {
        return standardizingEffort;
    }

    public void setStandardizingEffort(final double newStandardizingEffort) {
        this.standardizingEffort = newStandardizingEffort;
    }

    @Column(name = "refactoring_gain")
    public int getRefactoringGain() {
        return refactoringGain;
    }

    public void setRefactoringGain(final int newRefactoringGain) {
        this.refactoringGain = newRefactoringGain;
    }


//    @ManyToOne
//    @JoinColumn(name = "folderId")
//    public Folder getFolder() {
//        return this.folder;
//    }
//
//    public void setFolder(final Folder newFolder) {
//        this.folder = newFolder;
//    }


    @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<ClusterAssignment> getClusterAssignments() {
        return this.clusterAssignments;
    }

    public void setClusterAssignments(final Set<ClusterAssignment> newClusterAssignment) {
        this.clusterAssignments = newClusterAssignment;
    }

    public void addClusterAssignment(final ClusterAssignment newClusterAssignment) {
        this.clusterAssignments.add(newClusterAssignment);
    }



    @Override
    public String toString() {
        return id + " | " + size + " | " + avgFragmentSize + " | " + BCR;
    }
}
