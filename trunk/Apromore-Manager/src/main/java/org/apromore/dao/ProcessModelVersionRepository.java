package org.apromore.dao;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object ProcessModelVersion.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.ProcessModelVersion
 */
@Repository
public interface ProcessModelVersionRepository extends JpaRepository<ProcessModelVersion, Integer>, ProcessModelVersionRepositoryCustom {

    /**
     * Returns the process model version by the branch id.
     * @param branchId the branch Id
     * @param branchName the branch Name
     * @return the found process model version
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv WHERE pmv.processBranch.id = ?1 AND pmv.processBranch.branchName = ?2")
    ProcessModelVersion findProcessModelVersionByBranch(Integer branchId, String branchName);

    /**
     * Gets the list of Used Fragment Models for a fragment version Id.
     * @param uri the fragment version uri if we are searching for used models
     * @return the list of found fragment model versions
     */
    @Query("SELECT pm FROM ProcessModelVersion pm WHERE pm.rootFragmentVersion.uri = ?1")
    List<ProcessModelVersion> getUsedProcessModelVersionsByURI(String uri);

    /**
     * Find the current process model for the process id and name combination.
     * @param processId the process id.
     * @param processName the process name.
     * @return processModelVersion we found or null.
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv, Process p, ProcessBranch pb WHERE p.id = pb.process.id " +
            "AND pb.id = pmv.processBranch.id AND p.id = ?1 AND pmv.versionName = ?2")
    ProcessModelVersion getCurrentVersion(Integer processId, String processName);


    /**
     * find the current process model version for the branch provided.
     * @param processId the branch name
     * @param versionName the version Name
     * @return the process model version.
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv, Process p, ProcessBranch pb WHERE p.id = pb.process.id " +
            "AND pb.id = pmv.processBranch.id AND p.id = ?1 AND pmv.versionName = ?2")
    ProcessModelVersion getCurrentProcessModelVersion(Integer processId, String versionName);

    /**
     * find the current process model version for the processname and branch provided.
     * @param processName the process name
     * @param branchName the branch name
     * @return the process model version.
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv WHERE pmv.processBranch.id = " +
            "   (SELECT pb.id FROM Process p, ProcessBranch pb WHERE p.id = pb.process.id " +
            "       AND p.name = ?1 AND pb.branchName = ?2)" +
            "       AND pmv.versionNumber = " +
            "          (SELECT max(pmv1.versionNumber) from ProcessModelVersion pmv1 " +
            "              WHERE pmv1.processBranch.id = " +
            "                 (SELECT pb1.id FROM Process p1, ProcessBranch pb1 WHERE p1.id = pb1.process.id " +
            "                    AND p1.name = ?1 AND pb1.branchName = ?2))")
    List<ProcessModelVersion> getCurrentProcessModelVersion(String processName, String branchName);


    /**
     * Returns all the ProcessModels for all version or the latest versions.
     * @return returns the list of processModelVersions
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv, ProcessBranch pb " +
            "WHERE pb.id = pmv.processBranch.id AND pb.creationDate in " +
            "  (SELECT max(pb2.creationDate) FROM ProcessBranch pb2 WHERE pb2.id = pmv.processBranch.id GROUP BY pb2.id) " +
            "ORDER by pb.id, pb.creationDate")
    List<ProcessModelVersion> getLatestProcessModelVersions();


    @Query("SELECT pmv FROM ProcessModelVersion pmv WHERE pmv.rootFragmentVersion = ?1")
    List<ProcessModelVersion> getUsedProcessModelVersions(final FragmentVersion originalFragmentVersion);

    /**
     * Get the root fragments.
     * @param minSize the minimum size fragment.
     * @return the list of root fragment ids
     */
    @Query("SELECT f.id FROM FragmentVersion f, ProcessModelVersion pmv " +
            "WHERE f.id = pmv.rootFragmentVersion.id AND f.fragmentSize > ?1")
    List<Integer> getRootFragments(int minSize);


    /**
     * Count the number of times this fragment version has been the root fragment for a process.
     * @param fragmentVersion the fragment version we are checking to see if has been used multiple times.
     * @return the count of times used, 0 or more
     */
    @Query("SELECT count(pmv) from ProcessModelVersion pmv WHERE pmv.rootFragmentVersion = ?1")
    long countFragmentUsesInProcessModels(FragmentVersion fragmentVersion);
}
