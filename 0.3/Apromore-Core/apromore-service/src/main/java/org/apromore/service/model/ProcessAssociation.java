/**
 *
 */
package org.apromore.service.model;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ProcessAssociation {

    private String processVersionId;
    private String processVersionNumber;
    private String processBranchName;
    private String processId;
    private String processName;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessVersionId() {
        return processVersionId;
    }

    public void setProcessVersionId(String processVersionId) {
        this.processVersionId = processVersionId;
    }

    public String getProcessVersionNumber() {
        return processVersionNumber;
    }

    public void setProcessVersionNumber(String processVersionNumber) {
        this.processVersionNumber = processVersionNumber;
    }

    public String getProcessBranchName() {
        return processBranchName;
    }

    public void setProcessBranchName(String processBranchName) {
        this.processBranchName = processBranchName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}
