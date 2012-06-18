package org.apromore.service;

import org.apromore.exception.ExceptionSearchForSimilar;
import org.apromore.model.ParametersType;
import org.apromore.model.ProcessSummariesType;

/**
 * Interface for the Similarity Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface SimilarityService {

    /**
     * Search for similar processes.
     * @param branchId the branchId
     * @param versionName the name of the version we are looking for
     * @param latestVersions are we looking at the latest version or all processes
     * @param method the search algorithm
     * @param params the params used for the
     * @return the processSummaryTypes for the found models
     * @throws ExceptionSearchForSimilar
     */
    ProcessSummariesType SearchForSimilarProcesses(Integer processId, String versionName, Boolean latestVersions, String method,
            ParametersType params) throws ExceptionSearchForSimilar;


}
