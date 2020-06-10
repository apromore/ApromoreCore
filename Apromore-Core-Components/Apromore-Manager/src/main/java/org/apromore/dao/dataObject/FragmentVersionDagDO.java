/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
public class FragmentVersionDagDO {

    private Integer id;
    private Integer fragmentVersionId;
    private Integer childFragmentVersionId;
    private String pocketId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFragmentVersionId() {
        return fragmentVersionId;
    }

    public void setFragmentVersionId(Integer fragmentVersionId) {
        this.fragmentVersionId = fragmentVersionId;
    }

    public Integer getChildFragmentVersionId() {
        return childFragmentVersionId;
    }

    public void setChildFragmentVersionId(Integer childFragmentVersionId) {
        this.childFragmentVersionId = childFragmentVersionId;
    }

    public String getPocketId() {
        return pocketId;
    }

    public void setPocketId(String pocketId) {
        this.pocketId = pocketId;
    }
}
