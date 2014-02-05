package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Process;

/**
 * Interface domain model Data access object Process.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Process
 */
public interface ProcessRepositoryCustom  {

    /* ************************** JPA Methods here ******************************* */

    /**
     * Find all the Processes based on the following Condition. This could be done Using Specifications?
     * @param conditions the conditions
     * @return the list of processes.
     */
    List<Process> findAllProcesses(final String conditions);

    /**
     * Find all the Processes based on the Conditions and folder. This could be done Using Specifications?
     * @param folderId the folder to search in.
     * @param conditions the conditions
     * @return the list of processes.
     */
    List<Process> findAllProcessesByFolder(final Integer folderId, final String conditions);

    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
