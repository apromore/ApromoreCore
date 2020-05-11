/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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

package org.apromore.dao;

import java.util.List;

import org.apache.commons.collections15.map.MultiKeyMap;
import org.apromore.dao.dataObject.DistanceDO;

/**
 * Interface domain model Data access object FragmentDistance Custom Methods.
 *
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 * @see org.apromore.dao.model.FragmentDistance
 */
public interface FragmentDistanceRepositoryCustom {

    /**
     * Persist fragment distances.
     * @param distanceMap the distance map, contains two fragments and the distance.
     */
    void saveDistances(MultiKeyMap distanceMap);

    /**
     * ** SPECIAL method for fast access to Fragment Distances.
     *
     * @param FragmentId1 the first fragment version id
     * @param FragmentId2 the second fragment id
     * @return the distance found
     */
    Double getDistance(Integer FragmentId1, Integer FragmentId2);

    /**
     * Save a distance calculation into the DB.
     * @param distances the the distance Data in a special Data object
     */
    void persistDistance(List<DistanceDO> distances);

}
