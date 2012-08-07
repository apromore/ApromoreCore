package org.apromore.dao;

import java.util.List;
import java.util.Map;

import org.apromore.dao.model.FragmentVersion;

/**
 * Define the Fragment Version DAO.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface FragmentVersionDao {

    /**
     * Get a single Fragment Version record.
     * @param fragmentId the fragment version id
     * @return the found fragmentVersion
     */
    FragmentVersion findFragmentVersion(String fragmentId);

    /**
     * Get all the Fragment Version records.
     * @return the list of records
     */
    List<FragmentVersion> getAllFragmentVersion();

    /**
     * find the matching fragment versions for the content and mapping code.
     * @param contentId the content id.
     * @param childMappingCode the child mapping code.
     * @return the fragment version that corresponds to the content and mapping code.
     */
    FragmentVersion getMatchingFragmentVersionId(String contentId, String childMappingCode);

    /**
     * get the used process models for a fragment.
     * @param fvid the fragment id.
     * @return the process model that uses fragment.
     */
    Integer getUsedProcessModels(String fvid);

    /**
     * Return the parent fragment for a fragment.
     * @param fvid the child fragment id.
     * @return the list of parent fragments.
     */
    List<FragmentVersion> getParentFragments(String fvid);

    /**
     * find all the parent fragments that are locked.
     * @param fvid the fragment we are searching for parents
     * @return the list of fragments Id's we are looking for.
     */
    List<String> getLockedParentFragmentIds(String fvid);

    /**
     * find all the child fragments with their size.
     * @param fvid the fragment id.
     * @return the list of fragments
     */
    Map<String, Integer> getChildFragmentsWithSize(String fvid);

    /**
     * find all the child fragments with their type.
     * @param fvid the fragment id.
     * @return the list of fragments
     */
    Map<Integer, String> getChildFragmentsWithType(int fvid);

    /**
     * Returns all the Fragments with a certain size.
     * @return the collection of Fragments
     */
    Map<String, Integer> getAllFragmentIdsWithSize();

    /**
     * Return the content id from a fragment.
     * @param fvid the fragment id
     * @return the content id.
     */
    String getContentId(String fvid);

    /**
     * return all the fragments for a particular process model.
     * @param pmvid the process model version id
     * @return the list of fragment data.
     */
    List<FragmentVersion> getFragmentDataOfProcessModel(String pmvid);

    /**
     * Return a fragment by it's id.
     * @param fragmentId the fragment Id we want
     * @return the fragment id or null.
     */
    FragmentVersion getFragmentData(String fragmentId);

    /**
     * Find all the process models that are used in a list of nodes.
     * @param nodes the nodes we are using to find the fragments
     * @return the list of fragment id's
     */
    List<String> getContainingFragments(List<String> nodes);

    /**
     * Find all the process models that use a particular fragment.
     * @param fragmentId the fragment Id
     * @return the list of process model id's
     */
    List<Integer> getContainedProcessModels(int fragmentId);

    /**
     * Find all the fragments that have been used by a particular fragment.
     * @param matchingContentId the fragment id we are searching for.
     * @return the list of fragments.
     */
    List<String> getUsedFragmentIds(String matchingContentId);

    /**
     * Find all the similar fragments byt their size.
     * @param minSize the min size to search for
     * @param maxSize the max size to search for.
     * @return the list of found fragments.
     */
    List<FragmentVersion> getSimilarFragmentsBySize(int minSize, int maxSize);

    /**
     * Find all the similar fragments byt their size and type.
     * @param minSize the min size to search for
     * @param maxSize the max size to search for.
     * @param type the fragment type.
     * @return the list of found fragments.
     */
    List<FragmentVersion> getSimilarFragmentsBySizeAndType(int minSize, int maxSize, String type);


    /**
     * Save the FragmentVersion.
     * @param fragVersion the FragmentVersion to persist
     */
    void save(FragmentVersion fragVersion);

    /**
     * Update the FragmentVersion.
     * @param fragVersion the FragmentVersion to update
     * @return the updated object.
     */
    FragmentVersion update(FragmentVersion fragVersion);

    /**
     * Remove the FragmentVersion.
     * @param fragVersion the FragmentVersion to remove
     */
    void delete(FragmentVersion fragVersion);

}
