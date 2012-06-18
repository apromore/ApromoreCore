package org.apromore.service;

import org.apromore.exception.ExceptionMergeProcess;
import org.apromore.exception.ExceptionSearchForSimilar;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
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
     * @param processName the new process name
     * @param version the version name
     * @param domain the domain
     * @param username the username that did this merge
     * @param algo the algorithm used
     * @param parameters the algorithm photos
     * @param ids ID'd of the processes to merge.
     * @return the new process summary of the newly merged process
     * @throws ExceptionMergeProcess if the merge failed
     */
    ProcessSummaryType mergeProcesses(String processName,String version, String domain, String username, String algo,
            ParametersType parameters, ProcessVersionIdsType ids) throws ExceptionMergeProcess;


}
