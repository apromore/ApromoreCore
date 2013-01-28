package org.apromore.service.helper;

import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionsType;

/**
 * UI Helper Interface. Kinda a hack, need to re-look at this.
 * @author <a href="mailto:cam.james@gmail.com>Cameron James</a>
 */
public interface UserInterfaceHelper {

    /**
     * Create a Process Summary record for the Front UI display.
     * @param name          the process Name
     * @param processId     the process Id
     * @param branchName    the version number of this model
     * @param versionNumber the process version number.
     * @param nativeType    the native type of this model
     * @param domain        The domain of this model
     * @param created       the Date create
     * @param lastUpdate    the Date Last Updated
     * @param username      the user who updated the
     * @return the created Process Summary
     */
    ProcessSummaryType createProcessSummary(String name, Integer processId, String branchName, Double versionNumber, String nativeType,
        String domain, String created, String lastUpdate, String username);


    /**
     * Builds the list of process Summaries and kicks off the versions and annotations.
     * @param conditions the search conditions
     * @param similarProcesses something
     * @return the list of process Summaries
     */
    ProcessSummariesType buildProcessSummaryList(String conditions, ProcessVersionsType similarProcesses);

    /**
     * Builds the list of process Summaries and kicks off the versions and annotations.
     * @param userId the search conditions
     * @param folderId the search conditions
     * @param similarProcesses something
     * @return the list of process Summaries
     */
    ProcessSummariesType buildProcessSummaryList(String userId, Integer folderId, ProcessVersionsType similarProcesses);
}
