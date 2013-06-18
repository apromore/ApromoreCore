package org.apromore.manager.client.helper;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.VersionSummaryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class to help construct the client's message to the WebService.
 */
public class DeleteProcessVersionHelper {

    public static final String GREEDY_ALGORITHM = "Greedy";

    /**
     * Sets up the process models that are to be merged.
     *
     * @param selectedProcessVersions the list of models
     * @return the object to be sent to the Service
     */
    public static Collection<ProcessVersionIdentifierType> setProcessModels(Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions) {
        ProcessVersionIdentifierType processVersionId;
        List<VersionSummaryType> versionSummaries;
        Set<ProcessSummaryType> keys = selectedProcessVersions.keySet();

        Collection<ProcessVersionIdentifierType> payload = new ArrayList<>();

        for (ProcessSummaryType processSummary : keys) {
            versionSummaries = selectedProcessVersions.get(processSummary);

            processVersionId = new ProcessVersionIdentifierType();
            processVersionId.setProcessName(processSummary.getName());
            for (VersionSummaryType versionSummary : versionSummaries) {
                processVersionId.setBranchName(versionSummary.getName());
                processVersionId.setVersionNumber(versionSummary.getVersionNumber());
            }

            payload.add(processVersionId);
        }

        return payload;
    }

}
