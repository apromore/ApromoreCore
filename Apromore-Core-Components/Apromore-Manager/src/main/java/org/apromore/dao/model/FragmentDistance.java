/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The Fragment Distance.
 *
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
@Entity
@Table(name = "fragment_distance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fragmentVersionId1", "fragmentVersionId2"}))
@Configurable("fragmentDistance")
@Cache(expiry = 180000, size = 5000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class FragmentDistance implements Serializable {

    private Integer id;
    private FragmentVersion fragmentVersionId1;
    private FragmentVersion fragmentVersionId2;
    private double distance;


    /**
     * Public Constructor.
     */
    public FragmentDistance() {
    }


    /**
     * returns the Id of this Object.
     *
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
     *
     * @param id the new Id.
     */
    public void setId(final Integer id) {
        this.id = id;
    }


    @ManyToOne
    @JoinColumn(name = "fragmentVersionId2")
    public FragmentVersion getFragmentVersionId2() {
        return this.fragmentVersionId2;
    }

    public void setFragmentVersionId2(FragmentVersion newFragmentVersionId2) {
        this.fragmentVersionId2 = newFragmentVersionId2;
    }

    @ManyToOne
    @JoinColumn(name = "fragmentVersionId1")
    public FragmentVersion getFragmentVersionId1() {
        return this.fragmentVersionId1;
    }

    public void setFragmentVersionId1(final FragmentVersion newFragmentVersionId1) {
        this.fragmentVersionId1 = newFragmentVersionId1;
    }

    @Column(name = "ged")
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
