package org.apromore.service;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionMergeProcess;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessVersionIdsType;

/**
 * Interface for the Merge Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface MergeService {

    /**
     * Merge multiple Processes and Save the Result.
     *
     * @param processName the new process name
     * @param version     the version name
     * @param domain      the domain
     * @param username    the username that did this merge
     * @param algo        the algorithm used
     * @param folderId    The folder we are going to store the new model in.
     * @param parameters  the algorithm photos
     * @param ids         ID'd of the processes to merge.
     * @param makePublic  do we make this new model public?
     * @return the new process summary of the newly merged process
     * @throws ExceptionMergeProcess if the merge failed
     */
    ProcessModelVersion mergeProcesses(String processName, String version, String domain, String username, String algo, Integer folderId,
        ParametersType parameters, ProcessVersionIdsType ids, boolean makePublic) throws ExceptionMergeProcess;


}
