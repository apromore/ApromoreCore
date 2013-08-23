package org.apromore.service.model;

/**
 * @author Chathura Ekanayake
 */
public class ProcessData {

    private Integer id;
    private Double versionNumber;


    public ProcessData(Integer id, Double versionNumber) {
        this.id = id;
        this.versionNumber = versionNumber;
    }

    public Integer getProcessId() {
        return id;
    }

    public void setProcessId(Integer id) {
        this.id = id;
    }

    public Double getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Double versionNumber) {
        this.versionNumber = versionNumber;
    }
}
