/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Table(name = "edge_mapping",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fragmentVersionId", "edgeId"})
        }
)
@Configurable("edgeMapping")
@Cache(expiry = 180000, size = 10000, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class EdgeMapping {

    private Integer id;

    private FragmentVersion fragmentVersion;
    private Edge edge;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "fragmentVersionId")
    public FragmentVersion getFragmentVersion() {
        return fragmentVersion;
    }

    public void setFragmentVersion(final FragmentVersion fragmentVersion) {
        this.fragmentVersion = fragmentVersion;
    }

    @ManyToOne
    @JoinColumn(name = "edgeId")
    public Edge getEdge() {
        return edge;
    }

    public void setEdge(final Edge edge) {
        this.edge = edge;
    }
}
