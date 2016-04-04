package org.apromore.plugin.merge;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.DefaultPlugin;

@Component("plugin")
public class MergePlugin extends DefaultPlugin implements MergeService {

    // Method implementing MergeService

    public ProcessSummaryType mergeProcesses(Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions, String mergedProcessName,
            String mergedVersionName, String mergedDomain, String mergedUsername, Integer folderId, boolean makePublic, String method,
            boolean removeEntanglements, double mergeThreshold, double labelThreshold, double contextThreshold, double skipnWeight,
            double subnWeight, double skipeWeight) {

        throw new RuntimeException("Not implemented");
    }
}
