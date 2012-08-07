package org.apromore.dao;

import org.apromore.dao.model.ProcessBranch;

/**
 * Interface domain model Data access object Branch.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.ProcessBranch
 */
public interface ProcessBranchDao {

    /**
     * Returns a single Process Branch based on the primary Key.
     * @param branchId the processBranch Id
     * @return the found processBranch
     */
    ProcessBranch findProcessBranch(String branchId);

    /**
     * Returns a single Process Branch based on the primary Key.
     * @param processId the process Id
     * @param branchName the branch name
     * @return the found processBranch
     */
    ProcessBranch getProcessBranchByProcessBranchName(String processId, String branchName);


    /**
     * Save the branch.
     * @param branch the branch to persist
     */
    void save(ProcessBranch branch);

    /**
     * Update the branch.
     * @param branch the branch to update
     * @return the merged object.
     */
    ProcessBranch update(ProcessBranch branch);

    /**
     * Remove the branch.
     * @param branch the branch to remove
     */
    void delete(ProcessBranch branch);

}
