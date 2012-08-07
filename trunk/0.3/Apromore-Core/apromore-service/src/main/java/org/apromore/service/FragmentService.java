package org.apromore.service;

import java.util.List;
import java.util.Map;

import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.RepositoryException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.helper.RPSTNodeCopy;

/**
 * Analysis Service. Used for the Node Usage Analyser and parts of the Repository Analyser.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface FragmentService {

    /**
     * Add a process Fragment Mapping record to the DB.
     * @param pmvid                the process Model Version
     * @param composingFragmentIds the composing fragment id's
     * @throws ExceptionDao if the DB throws an exception
     */
    void addProcessFragmentMappings(Integer pmvid, List<String> composingFragmentIds) throws ExceptionDao;

    /**
     * Get the Fragment id.
     * @param pmvid the process fragment Version id
     * @param g     the process model graph
     * @param nodes the list of nodes
     * @return the fragment Id
     */
    String getFragmentId(Integer pmvid, CPF g, List<String> nodes);

    /**
     * Gets the Fragment as Epml.
     * @param fragmentId
     * @return
     * @throws RepositoryException
     */
    String getFragmentAsEPML(String fragmentId) throws RepositoryException;

    /**
     * Get a particular Fragment based on fragmentId.
     * @param fragmentId the fragment to return
     * @param lock       do we lock the table or not.
     * @return the found processModel.
     * @throws LockFailedException if we failed to obtain a lock on the table
     */
    CPF getFragment(String fragmentId, boolean lock) throws LockFailedException;

    /**
     * Get a particular Fragment version based on fragmentVersionId.
     * @param fragmentVersionId the fragment version to return
     * @return the found fragment version.
     */
    FragmentVersion getFragmentVersion(String fragmentVersionId);

    /**
     * Used to save a new Fragment to the DB.
     * @param cid the content Id
     * @param childMappings the child mappings
     * @param derivedFrom what model was this derived from
     * @param lockStatus is this model to be locked or not
     * @param lockCount the lock count
     * @param originalSize the original size of this fragment
     * @param fragmentType the type of this fragment
     */
    FragmentVersion addFragmentVersion(Content cid, Map<String, String> childMappings, String derivedFrom,
        int lockStatus, int lockCount, int originalSize, String fragmentType);

    /**
     * Used to Save a child mapping to the DB.
     * @param fragVer the fragment version
     * @param childMappings the child mappings
     * @throws ExceptionDao if communications to the DB fails
     */
    void addChildMappings(FragmentVersion fragVer, Map<String, String> childMappings);

    /**
     * Stores a fragment in the DB.
     * @param fragmentCode the fragment code
     * @param fCopy the RPSTNode
     * @param g the CPF graph
     * @return the new fragment version
     */
    FragmentVersion storeFragment(String fragmentCode, RPSTNodeCopy fCopy, CPF g);

    /**
     * Gets the Matching Fragment Versions.
     * @param contentId  the content Id
     * @param childMappings the child mappings
     * @return the FragmentVersion
     */
    FragmentVersion getMatchingFragmentVersionId(final String contentId, final Map<String, String> childMappings);


    /**
     * Deletes the Fragment Version.
     * @param fvid the fragment Version id.
     */
    void deleteFragmentVersion(String fvid);

    /**
     * Deletes all the child relationships from a fragment Version.
     * @param fvid the fragment Version id.
     */
    void deleteChildRelationships(String fvid);

    /**
     * Update to have the new Derived fragments.
     * @param fvid                  the fragment Version Id
     * @param derivedFromFragmentId the id it was derived from.
     */
    void setDerivation(String fvid, String derivedFromFragmentId);

}

