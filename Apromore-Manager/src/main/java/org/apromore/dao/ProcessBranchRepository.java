package org.apromore.dao;

import org.apromore.dao.model.ProcessBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Branch.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.ProcessBranch
 */
@Repository
public interface ProcessBranchRepository extends JpaRepository<ProcessBranch, Integer> {

    /**
     * Returns a single Process Branch based on the primary Key.
     * @param processId the process Id
     * @param branchName the branch name
     * @return the found processBranch
     */
    @Query("SELECT b FROM ProcessBranch b WHERE b.process.id = ?1 AND b.branchName = ?2")
    ProcessBranch getProcessBranchByProcessBranchName(Integer processId, String branchName);

}
