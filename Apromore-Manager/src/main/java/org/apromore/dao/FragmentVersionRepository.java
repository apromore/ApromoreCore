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
    @Query("SELECT f FROM FragmentVersion f JOIN f.processModelVersions pmv JOIN pmv.processBranch b JOIN b.process p WHERE p.id IN (?1)")
    List<FragmentVersion> getFragmentsByProcessIds(List<Integer> clusterId);

    /**
     * Count the number of times this fragment version is used by other Fragment Versions.
     * @param fragmentVersion the fragment version we are checking to see if has been used multiple times.
     * @return the count of times used, 0 or more
     */
    @Query("SELECT count(fvd) from FragmentVersionDag fvd WHERE fvd.childFragmentVersion = ?1")
    long countFragmentUsesInFragmentVersions(FragmentVersion fragmentVersion);
}
