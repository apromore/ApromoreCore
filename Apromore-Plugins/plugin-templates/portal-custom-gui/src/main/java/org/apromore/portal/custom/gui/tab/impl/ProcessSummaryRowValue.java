package org.apromore.portal.custom.gui.tab.impl;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 13/04/2016.
 */
public class ProcessSummaryRowValue extends TabRowValue {

    public ProcessSummaryType getProcessSummaryType() {
        return processSummaryType;
    }

    public VersionSummaryType getVersionSummaryType() {
        return versionSummaryType;
    }

    private ProcessSummaryType processSummaryType;
    private VersionSummaryType versionSummaryType;

    public ProcessSummaryRowValue(ProcessSummaryType processSummaryType, VersionSummaryType versionSummaryType) {
        this.processSummaryType = processSummaryType;
        this.versionSummaryType = versionSummaryType;
    }

}
