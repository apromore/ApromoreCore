package org.apromore.service;

import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentDataObject;

import java.util.List;
import java.util.Map;

/**
 * Analysis Service. Used for the Node Usage Analyser and parts of the Repository Analyser.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface FragmentService {

    /**
     * Gets the Fragment as Epml.
     * @param fragmentId the fragment Id
     * @return the fragment as an EPML.
     * @throws RepositoryException
     */
    String getFragmentAsEPML(Integer fragmentId) throws RepositoryException;

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
     * @param fragmentVersionId the fragment version to return
     * @return the found fragment version.
     */
    FragmentVersion getFragmentVersion(Integer fragmentVersionId);

    /**
     * Used to save a new Fragment to the DB.
     * @param processModel the process Model we are building.
     * @param content the content Id
     * @param childMappings the child mappings
     * @param derivedFrom what model was this derived from
     * @param lockStatus is this model to be locked or not
     * @param lockCount the lock count
     * @param originalSize the original size of this fragment
     * @param fragmentType the type of this fragment
     */
    FragmentVersion addFragmentVersion(ProcessModelVersion processModel, Content content, Map<String, String> childMappings,
        String derivedFrom, int lockStatus, int lockCount, int originalSize, String fragmentType);


    /**
     * Get a Fragment.
     * @param fragmentUri the id of the fragment to get.
     * @param lock do we lock or not.
     * @return the process Model Graph
     * @throws LockFailedException if the lock failed.
     */
    Canonical getFragment(String fragmentUri, boolean lock) throws LockFailedException;

    /**
     * Gets the Matching Fragment Versions.
     * @param contentId  the content Id
     * @param childMappings the child mappings
     * @return the FragmentVersion
     */
    FragmentVersion getMatchingFragmentVersionId(final Integer contentId, final Map<String, String> childMappings);

    /**
     * The unprocessed fragments of the entire repository.
     * @return the list of fragments in objects
     */
    List<FragmentDataObject> getUnprocessedFragments();

    /**
     * Returns all the unprocessed Fragments of a process.
     * @param processIds the process Id's
     * @return the Unprocessed fragments
     */
    List<FragmentDataObject> getUnprocessedFragmentsOfProcesses(final List<Integer> processIds);

    /**
     * Deletes all the child relationships from a fragment Version.
     * @param fvid the fragment Version id.
     */
    void deleteChildRelationships(Integer fvid);

}

