package org.apromore.service;

import org.apromore.dao.model.User;
import org.apromore.model.ProcessSummariesType;

import java.util.List;

/**
 * Interface for the Process Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ProcessService {

    /**
     * Loads all the process Summaries. It will either get all or use the keywords parameter
     * to load a subset of the processes.
     * @param searchExpression the search expression to limit the search.
     * @return The ProcessSummariesType used for Webservices.
     */
    ProcessSummariesType readProcessSummaries(String searchExpression);

}
