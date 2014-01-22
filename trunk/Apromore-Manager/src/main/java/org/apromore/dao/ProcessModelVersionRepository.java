package org.apromore.dao;

import javax.persistence.QueryHint;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
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
    @Query("SELECT pmv FROM ProcessModelVersion pmv JOIN pmv.processBranch pb JOIN pb.process p " +
            "WHERE p.id = ?1 AND pb.branchName = ?2")
    ProcessModelVersion findProcessModelVersionByBranch(Integer branchId, String branchName);

    /**
     * Find the process model version for the process id branch and version.
     * @param processId the process id.
     * @param branchName the branch name.
     * @param versionNumber the pmv version number.
     * @return processModelVersion we found or null.
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv JOIN pmv.processBranch pb JOIN pb.process p " +
            "WHERE p.id = ?1 AND pb.branchName = ?2 AND pmv.versionNumber = ?3")
    ProcessModelVersion getProcessModelVersion(Integer processId, String branchName, String versionNumber);

    /**
     * find the current process model version for the process and version details provided.
     * @param processId the branch name
     * @param versionNumber the version Number
     * @return the process model version.
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv JOIN pmv.processBranch pb " +
            "JOIN pb.process p " +
            "WHERE p.id = ?1 AND pmv.versionNumber = ?2")
    ProcessModelVersion getCurrentProcessModelVersion(Integer processId, String versionNumber);

    /**
     * find the current process model version for the process name and branch provided.
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
    ProcessModelVersion getCurrentProcessModelVersion(String processName, String branchName);

    /**
     * Returns all the ProcessModels for all version or the latest versions.
     * @return returns the list of processModelVersions
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv, ProcessBranch pb " +
            "WHERE pb.id = pmv.processBranch.id AND pb.createDate in " +
            "  (SELECT max(pb2.createDate) FROM ProcessBranch pb2 WHERE pb2.id = pmv.processBranch.id GROUP BY pb2.id) " +
            "ORDER by pb.id, pb.createDate")
    @QueryHints(value = {
            @QueryHint(name = "eclipselink.query-results-cache", value = "true"),
            @QueryHint(name = "eclipselink.query-results-cache.size", value = "1000")
        },
    forCounting = false)
    List<ProcessModelVersion> getLatestProcessModelVersions();

    /**
     * Find all process model version that use the fragment version.
     * @param originalFragmentVersion the fragment version we are looking for that has been used.
     * @return the found list of process model versions.
     */
    @Query("SELECT pmv FROM ProcessModelVersion pmv WHERE pmv.rootFragmentVersion = ?1")
    List<ProcessModelVersion> getUsedProcessModelVersions(final FragmentVersion originalFragmentVersion);

    /**
     * Get the root fragments.
     * @param minSize the minimum size fragment.
     * @return the list of root fragment ids
     */
    @Query("SELECT f.id FROM FragmentVersion f JOIN f.rootProcessModelVersions pmv WHERE f.fragmentSize > ?1")
    List<Integer> getRootFragments(int minSize);

    /**
     * Count the number of times this fragment version has been the root fragment for a process.
     * @param fragmentVersion the fragment version we are checking to see if has been used multiple times.
     * @return the count of times used, 0 or more
     */
    @Query("SELECT count(pmv) from ProcessModelVersion pmv WHERE pmv.rootFragmentVersion = ?1")
    long countFragmentUsesInProcessModels(FragmentVersion fragmentVersion);

}
