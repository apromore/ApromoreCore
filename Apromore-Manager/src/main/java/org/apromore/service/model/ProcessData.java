package org.apromore.service.model;

/**
 * @author Chathura Ekanayake
 */
public class ProcessData {

    private Integer id;
    private Double versionNumber;


    public ProcessData() {
    }

    public ProcessData(Integer id, Double versionNumber) {
        this.id = id;
        this.versionNumber = versionNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Double versionNumber) {
        this.versionNumber = versionNumber;
    }
}
