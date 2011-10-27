package org.apromore.dao;

import org.apromore.dao.model.Process;

import java.util.List;

/**
 * Interface domain model Data access object Process.
 *
 * @see org.apromore.dao.model.Process
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface ProcessDao {


    /**
     * Returns all the processes in the system. Also there rankings.
     * @return the list of processes.
     */
    List<Object[]> getAllProcesses();

    /**
     * Returns the distinct list of domains.
     * @return the list of domains.
     */
    List<Object> getAllDomains();



    /**
     * Save the process.
     * @param process the process to persist
     */
    void save(Process process);

    /**
     * Update the process.
     * @param process the process to update
     */
    void update(Process process);

    /**
     * Remove the process.
     * @param process the process to remove
     */
    void delete(Process process);

}
