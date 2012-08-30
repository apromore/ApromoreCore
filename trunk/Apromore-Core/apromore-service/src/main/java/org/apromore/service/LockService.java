package org.apromore.service;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;

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
     * unLock a previously locked process Model.
     *
     * @param processModelVersionId the process model
     */
    void unlockProcessModelVersion(Integer processModelVersionId);

    /**
     * Lock a fragment.
     *
     * @param fragmentId the fragment to lock
     * @return if locked successfully or not
     */
    boolean lockFragment(String fragmentId);

    /**
     * Locks a single fragment.
     *
     * @param fragVersion the fragment
     * @return the fragment
     */
    boolean lockSingleFragment(FragmentVersion fragVersion);

    /**
     * unLock a previously locked fragment.
     *
     * @param fragmentId the fragment to unlock
     */
    void unlockFragment(String fragmentId);

    /**
     * Unlocks the ascendant fragments ????
     *
     * @param fragmentId the fragment id.
     */
    void unlockAscendantFragments(String fragmentId);

    /**
     * Unlocks the descendant Fragments ????
     *
     * @param fragmentId the fragmentId.
     */
    void unlockDescendantFragments(String fragmentId);

    /**
     * Unlocks the descendant Fragments ????
     *
     * @param fragmentVersionDag the fragmentVersionDag.
     */
    void unlockDescendantFragments(FragmentVersionDag fragmentVersionDag);


    /**
     * Is a model used in the current ProcessModel
     *
     * @param fragVersion the fragment
     * @return true or false
     */
    boolean isUsedInCurrentProcessModel(FragmentVersion fragVersion);

}

