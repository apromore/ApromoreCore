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

package org.apromore.service;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.ProcessModelVersion;

/**
 * Lock Service, This service is used to to control the Locks that are held on the database tables and records.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface LockService {

    /**
     * Lock a process Model.
     *
     * @param processModelVersionId the process model
     * @return if locked successfully or not
     */
    boolean lockProcessModelVersion(Integer processModelVersionId);

    /**
     * Lock a fragment.
     *
     * @param fragmentId the fragment to lock
     * @return if locked successfully or not
     */
    boolean lockFragment(Integer fragmentId);

    /**
     * Lock a fragment.
     * @param fragmentUri the fragment to lock
     * @return if locked successfully or not
     */
    boolean lockFragmentByUri(String fragmentUri);

    /**
     * Locks a single fragment.
     * @param fragVersion the fragment
     * @return the fragment
     */
    boolean lockSingleFragment(FragmentVersion fragVersion);



    /**
     * unLock a previously locked process Model.
     * @param processModelVersion the process model
     */
    void unlockProcessModelVersion(ProcessModelVersion processModelVersion);

    /**
     * unLock a previously locked fragment.
     * @param fragmentVersion the fragment to unlock
     */
    void unlockFragment(FragmentVersion fragmentVersion);

    /**
     * unLock a previously locked fragment.
     * @param uri the fragment to unlock
     */
    void unlockFragmentByURI(String uri);

    /**
     * Unlocks the ascendant fragments ????
     * @param fragmentVersion the fragment id.
     */
    void unlockAscendantFragments(FragmentVersion fragmentVersion);

    /**
     * Unlocks the descendant Fragments ????
     * @param uri the fragment uri.
     */
    void unlockDescendantFragmentsByURI(String uri);

    /**
     * Unlocks the descendant Fragments ????
     * @param fragmentVersion the fragmentVersionDag.
     */
    void unlockDescendantFragments(FragmentVersion fragmentVersion);




    /**
     * Is a model used in the current ProcessModel
     * @param fragVersion the fragment
     * @return true or false
     */
    boolean isUsedInCurrentProcessModel(FragmentVersion fragVersion);

}

