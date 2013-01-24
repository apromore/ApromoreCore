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

