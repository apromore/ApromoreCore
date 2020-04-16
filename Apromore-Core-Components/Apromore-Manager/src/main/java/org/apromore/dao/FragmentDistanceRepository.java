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

package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.FragmentDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object FragmentDistance.
 *
 * @see org.apromore.dao.model.FragmentDistance
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface FragmentDistanceRepository extends JpaRepository<FragmentDistance, Integer>, FragmentDistanceRepositoryCustom {

    /**
     * Searches for a a particular fragment distance.
     * @param fragmentVersionId1 first fragment version id
     * @param fragmentVersionId2 second fragment version id
     * @return the found fragment distance or null.
     */
    @Query("SELECT fd FROM FragmentDistance fd WHERE (fd.fragmentVersionId1.id = ?1 AND fd.fragmentVersionId2.id = ?2) OR (fd.fragmentVersionId1.id = ?2 AND fd.fragmentVersionId2.id = ?1)")
    FragmentDistance findByFragmentVersionId1AndFragmentVersionId2(Integer fragmentVersionId1, Integer fragmentVersionId2);

    /**
     * Find all the distances that have a distance less than the threashold.
     * @param threshold the distance threshold
     * @return the list of found distances
     */
    List<FragmentDistance> findByDistanceLessThan(double threshold);

}
