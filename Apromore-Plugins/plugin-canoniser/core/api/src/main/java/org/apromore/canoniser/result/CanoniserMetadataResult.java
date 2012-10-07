package org.apromore.canoniser.result;

import java.util.Date;

import org.apromore.plugin.impl.DefaultPluginResult;

public class CanoniserMetadataResult extends DefaultPluginResult {

    private String processAuthor;
    private String processName;
    private String processVersion;
    private String processDocumentation;
    private Date processCreated;
    private Date processLastUpdate;

    public String getProcessAuthor() {
        return processAuthor;
    }

    public void setProcessAuthor(String processAuthor) {
        this.processAuthor = processAuthor;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    public String getProcessDocumentation() {
        return processDocumentation;
    }

    public void setProcessDocumentation(String processDocumentation) {
        this.processDocumentation = processDocumentation;
    }

    public Date getProcessCreated() {
        return processCreated;
    }

    public void setProcessCreated(Date processCreated) {
        this.processCreated = processCreated;
    }

    public Date getProcessLastUpdate() {
        return processLastUpdate;
    }

    public void setProcessLastUpdate(Date processLastUpdate) {
        this.processLastUpdate = processLastUpdate;
    }

}
