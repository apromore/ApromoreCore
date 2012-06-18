package org.apromore.dao;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;

import java.util.List;

/**
 * Interface domain model Data access object FragmentVersionDag.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.FragmentVersionDag
 */
public interface FragmentVersionDagDao {

    /**
     * Returns a single FragmentVersionDag based on the primary Key.
     * @param fragmentVersionDagId the Fragment Id
     * @return the found FragmentVersionDag
     */
    FragmentVersionDag findFragmentVersionDag(String fragmentVersionDagId);


    /**
     * Returns all the child mappings for the FragmentId.
     * @param fragmentId the fragment id
     * @return the list of child fragments
     */
    List<FragmentVersionDag> getChildMappings(String fragmentId);

    /**
     * the child Fragments from the fragment Version.
     * @param fragmentVersionId  the fragment version id
     * @return the list of child fragments.
     */
    List<FragmentVersion> getChildFragmentsByFragmentVersion(String fragmentVersionId);



    /**
     * Save the FragmentVersionDag.
     * @param fragmentVersionDag the FragmentVersionDag to persist
     */
    void save(FragmentVersionDag fragmentVersionDag);

    /**
     * Update the FragmentVersionDag.
     * @param fragmentVersionDag the FragmentVersionDag to update
     */
    FragmentVersionDag update(FragmentVersionDag fragmentVersionDag);

    /**
     * Remove the FragmentVersionDag.
     * @param fragmentVersionDag the FragmentVersionDag to remove
     */
    void delete(FragmentVersionDag fragmentVersionDag);

}
