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

package org.apromore.service;

import java.util.List;
import java.util.Map;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.LockFailedException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentDataObject;

/**
 * Analysis Service. Used for the Node Usage Analyser and parts of the Repository Analyser.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface FragmentService {

    /**
     * Finds a fragment and returns the Canonical process type for that so we can decanonise into a native language.
     * @param fragmentId the fragment id
     * @return the canonical process type.
     */
    CanonicalProcessType getFragmentToCanonicalProcessType(Integer fragmentId);

    /**
     * Get a particular Fragment based on fragmentId.
     * @param fragmentId the fragment to return
     * @param lock       do we lock the table or not.
     * @return the found processModel.
     * @throws LockFailedException if we failed to obtain a lock on the table
     */
    Canonical getFragment(Integer fragmentId, boolean lock) throws LockFailedException;

    /**
     * Get a particular Fragment version based on fragmentVersionId.
     *
     * @param fragmentVersionId the fragment version to return
     * @return the found fragment version.
     */
    FragmentVersion getFragmentVersion(Integer fragmentVersionId);

    /**
     * Used to save a new Fragment to the DB.
     *
     * @param processModel  the process Model we are building.
     * @param childMappings the child mappings
     * @param derivedFrom   what model was this derived from
     * @param lockStatus    is this model to be locked or not
     * @param lockCount     the lock count
     * @param originalSize  the original size of this fragment
     * @param fragmentType  the type of this fragment
     */
    FragmentVersion addFragmentVersion(ProcessModelVersion processModel, Map<String, String> childMappings,
                                       String derivedFrom, int lockStatus, int lockCount, int originalSize, String fragmentType);


    /**
     * Get a Fragment.
     *
     * @param fragmentUri the id of the fragment to get.
     * @param lock        do we lock or not.
     * @return the process Model Graph
     * @throws LockFailedException if the lock failed.
     */
    Canonical getFragment(String fragmentUri, boolean lock) throws LockFailedException;

    /**
     * The unprocessed fragments of the entire repository.
     *
     * @return the list of fragments in objects
     */
    List<FragmentDataObject> getUnprocessedFragments();

    /**
     * Returns all the unprocessed Fragments of a process.
     *
     * @param processIds the process Id's
     * @return the Unprocessed fragments
     */
    List<FragmentDataObject> getUnprocessedFragmentsOfProcesses(final List<Integer> processIds);


    /**
     * With this map of parent and child details we can now save the fragment version DAG
     *
     * @param childMappings the child mappings
     */
    void addFragmentVersionDag(Map<FragmentVersion, Map<String, String>> childMappings);
}

