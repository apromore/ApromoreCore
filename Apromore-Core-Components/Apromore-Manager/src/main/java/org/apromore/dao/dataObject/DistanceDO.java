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

package org.apromore.dao.dataObject;

import javax.persistence.Cacheable;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.config.CacheIsolationType;

/**
 * @author Chathura Ekanayake
 */
@Cacheable(true)
@org.eclipse.persistence.annotations.Cache(type = CacheType.WEAK, isolation = CacheIsolationType.SHARED, expiry = 60000, size = 1000, alwaysRefresh = true, disableHits = true, coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class DistanceDO {

    private Integer fragmentId1;
    private Integer fragmentId2;
    private Double ged;

    public DistanceDO(final Integer id1, final Integer id2, final Double distance) {
        fragmentId1 = id1;
        fragmentId2 = id2;
        ged = distance;
    }

    public Integer getFragmentId1() {
        return fragmentId1;
    }

    public void setFragmentId1(final Integer fragId1) {
        this.fragmentId1 = fragId1;
    }

    public Integer getFragmentId2() {
        return fragmentId2;
    }

    public void setFragmentId2(final Integer fragId2) {
        this.fragmentId2 = fragId2;
    }

    public Double getGed() {
        return ged;
    }

    public void setGed(final Double gedDistance) {
        this.ged = gedDistance;
    }
}
