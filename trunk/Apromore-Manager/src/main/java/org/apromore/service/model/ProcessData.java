package org.apromore.service.model;

import org.apromore.dao.dataObject.Version;

/**
 * @author Chathura Ekanayake
 */
public class ProcessData {

    private Integer id;
    private Version versionNumber;


    public ProcessData() {
    }

    public ProcessData(Integer id, Version versionNumber) {
        this.id = id;
        this.versionNumber = versionNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Version getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Version versionNumber) {
        this.versionNumber = versionNumber;
    }
}
