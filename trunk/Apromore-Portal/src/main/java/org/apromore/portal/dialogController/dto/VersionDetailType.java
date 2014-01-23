package org.apromore.portal.dialogController.dto;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

/**
 * Created by cameron on 23/01/2014.
 */
public class VersionDetailType {

    private ProcessSummaryType process;
    private VersionSummaryType version;

    public VersionDetailType(final ProcessSummaryType process, final VersionSummaryType version) {
        this.process = process;
        this.version = version;
    }

    public ProcessSummaryType getProcess() {
        return process;
    }

    public void setProcess(final ProcessSummaryType process) {
        this.process = process;
    }

    public VersionSummaryType getVersion() {
        return version;
    }

    public void setVersion(final VersionSummaryType version) {
        this.version = version;
    }
}
