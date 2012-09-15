package org.apromore.dao;

import java.util.List;
import java.util.Map;

import org.apromore.dao.model.Process;

/**
 * Interface domain model Data access object Process.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Process
 */
public interface ProcessDao {

    /**
     * Returns a process.
     * @param processId the process id
     * @return the Process
     */
    Process findProcess(Integer processId);


    /**
     * Returns all the processes in the system.
     * @return the list of processes.
     */
    List<Process> getProcesses();

    /**
     * Returns all the processes in the system. Also there rankings.
     * @param conditions the conditions that might need to be added to the query.
     * @return the list of processes.
     */
    List<Process> getAllProcesses(String conditions);

    /**
     * Returns the distinct list of domains.
     * @return the list of domains.
     */
    List<String> getAllDomains();

    /**
     * Returns the process Name.
     * @param processId the identifier of the process
     * @return the name of the process
     */
    Process getProcess(Integer processId);

    /**
     * Returns the process object for the record that contains the passed in process name.
     * @param processName the identifier of the process
     * @return the process
     */
    Process getProcess(String processName);

    /**
     * Returns the root process model fragment.
     * @param processModelVersionId the model version id
     * @return the fragment version id
     */
    Integer getRootFragmentVersionId(Integer processModelVersionId);

    /**
     * Returns the root process model fragment.
     * @param processModelVersionId the model version id
     * @return the fragment version id
     */
    String getRootFragmentVersionURI(Integer pmvid);

    /**
     * Returns a map of the current process models and the branch version.
     * @return the map of current Models
     */
    Map<Integer, int[]> getCurrentProcessModels();


    /**
     * Save the process.
     * @param process the process to persist
     */
    void save(Process process);

    /**
     * Update the process.
     * @param process the process to update
     * @return the merged object.
     */
    Process update(Process process);

    /**
     * Remove the process.
     * @param process the process to remove
     */
    void delete(Process process);

}
