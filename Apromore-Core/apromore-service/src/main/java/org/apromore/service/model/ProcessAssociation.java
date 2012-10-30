/**
 *
 */
package org.apromore.service.model;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ProcessAssociation {

    private Integer processVersionId;
    private Double processVersionNumber;
    private String processBranchName;
    private Integer processId;
    private String processName;


    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(final Integer newProcessId) {
        this.processId = newProcessId;
    }

    public Integer getProcessVersionId() {
        return processVersionId;
    }

    public void setProcessVersionId(final Integer newProcessVersionId) {
        this.processVersionId = newProcessVersionId;
    }

    public Double getProcessVersionNumber() {
        return processVersionNumber;
    }

    public void setProcessVersionNumber(final Double newProcessVersionNumber) {
        this.processVersionNumber = newProcessVersionNumber;
    }

    public String getProcessBranchName() {
        return processBranchName;
    }

    public void setProcessBranchName(final String newProcessBranchName) {
        this.processBranchName = newProcessBranchName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(final String newProcessName) {
        this.processName = newProcessName;
    }
}
