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


    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
