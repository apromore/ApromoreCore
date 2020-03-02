/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.dao;

import org.apromore.dao.model.FragmentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object FragmentVersion.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.FragmentVersion
 */
@Repository
public interface FragmentVersionRepository extends JpaRepository<FragmentVersion, Integer>, FragmentVersionRepositoryCustom {

    /**
     * Find the Fragment version from it's URI.
     * @param uri the uri of the fragment version.
     * @return the fragment version
     */
    @Query("SELECT fv FROM FragmentVersion fv WHERE fv.uri = ?1")
    FragmentVersion findFragmentVersionByUri(String uri);

    /**
     * Return the parent fragment for a fragment.
     * @param childFragmentVersionId the child fragment id.
     * @return the list of parent fragments.
     */
    @Query("SELECT fvd FROM FragmentVersionDag fvd WHERE fvd.fragmentVersion.id = ?1")
    List<FragmentVersion> getParentFragments(Integer childFragmentVersionId);

    /**
     * Return the parent fragment for a fragment.
     * @param childFragmentVersionId the child fragment id.
     * @return the list of parent fragments.
     */
    @Query("SELECT fvd FROM FragmentVersionDag fvd WHERE fvd.childFragmentVersion.id = ?1")
    List<FragmentVersion> getRealParentFragments(Integer childFragmentVersionId);

    /**
     * find all the parent fragments that are locked.
     * @param childFragmentVersion the fragment we are searching for parents
     * @return the list of fragments Version's we are looking for.
     */
    @Query("SELECT fv FROM FragmentVersion fv, FragmentVersionDag fvd WHERE fv.id = fvd.fragmentVersion.id " +
            "AND fv.lockStatus = 1 AND fvd.childFragmentVersion = ?1")
    List<FragmentVersion> getLockedParentFragments(FragmentVersion childFragmentVersion);

    /**
     * the child Fragments from the fragment Version.
     * @param fragmentVersion the fragment version we are using to find it's children
     * @return the list of child fragments.
     */
    @Query("SELECT cfv FROM FragmentVersionDag fvd JOIN fvd.childFragmentVersion cfv JOIN fvd.fragmentVersion fv WHERE fv = ?1")
    List<FragmentVersion> getChildFragmentsByFragmentVersion(FragmentVersion fragmentVersion);

    /**
     * find a fragments of a cluster.
     * @param clusterId the cluster id
     * @return the list of fragments
     */
    @Query("SELECT f FROM FragmentVersion f JOIN f.processModelVersions pmv JOIN pmv.processBranch b JOIN b.process p WHERE p.id IN ?1")
    List<FragmentVersion> getFragmentsByProcessIds(List<Integer> clusterId);

    /**
     * Count the number of times this fragment version is used by other Fragment Versions.
     * @param fragmentVersion the fragment version we are checking to see if has been used multiple times.
     * @return the count of times used, 0 or more
     */
    @Query("SELECT count(fvd) from FragmentVersionDag fvd WHERE fvd.childFragmentVersion = ?1")
    long countFragmentUsesInFragmentVersions(FragmentVersion fragmentVersion);
}
