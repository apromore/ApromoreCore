package org.apromore.manager.client.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionIdentifierType;
import org.apromore.model.VersionSummaryType;

/**
 * Helper class to help construct the client's message to the WebService.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public final class DeleteProcessVersionHelper {

    /* Private Constructor */
    private DeleteProcessVersionHelper() {
    }


    /**
     * Sets up the process models that are to be merged.
     * @param selectedProVers the list of models
     * @return the object to be sent to the Service
     */
    public static Collection<ProcessVersionIdentifierType> setProcessModels(final Map<ProcessSummaryType, List<VersionSummaryType>> selectedProVers) {
        ProcessVersionIdentifierType processVersionId;
        List<VersionSummaryType> versionSummaries;
        Set<ProcessSummaryType> keys = selectedProVers.keySet();

        Collection<ProcessVersionIdentifierType> payload = new ArrayList<>();

        for (ProcessSummaryType processSummary : keys) {
            versionSummaries = selectedProVers.get(processSummary);
            processVersionId = new ProcessVersionIdentifierType();
            processVersionId.setProcessName(processSummary.getName());
            for (VersionSummaryType versionSummary : versionSummaries) {
                processVersionId.setBranchName(versionSummary.getName());
            }
            payload.add(processVersionId);
        }

        return payload;
    }

}
