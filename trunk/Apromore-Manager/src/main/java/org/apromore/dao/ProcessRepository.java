package org.apromore.dao;

import org.apromore.dao.model.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object Process.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Process
 */
@Repository
public interface ProcessRepository extends JpaRepository<Process, Integer>, ProcessRepositoryCustom {

    /**
     * Returns the distinct list of domains.
     * @return the list of domains.
     */
    @Query("SELECT DISTINCT p.domain FROM Process p ORDER by p.domain")
    List<String> getAllDomains();

    /**
     * Returns the process object the has this name and joins to a branch with a certain name.
     * @param processName the processName;
     * @param branchName the branchName;
     * @return the process if found or null if nothing found
     */
    @Query("SELECT p FROM ProcessBranch pb JOIN pb.process p WHERE p.name = ?1 AND pb.branchName = ?2")
    Process getProcessByNameAndBranchName(String processName, String branchName);

    /**
     * Finds if a process with a particular name exists.
     * @param processName the process name
     * @return the process if one exists, null otherwise.
     */
    @Query("SELECT p FROM Process p WHERE p.name = ?1 AND p.folder is null")
    Process findUniqueByName(String processName);

    /**
     * Finds if a process with a particular name in a particular folder exists.
     * @param processName the process name
     * @param folderId the folder id
     * @return the process if one exists, null otherwise.
     */
    Process findByNameAndFolderId(String processName, Integer folderId);
}
