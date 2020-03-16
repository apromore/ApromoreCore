package org.apromore.service.csvimporter.impl;

import java.util.List;

public class CaseAttributesDiscovery {

    private String caseId;
    private int position;
    private String value;



    public CaseAttributesDiscovery() {
    }

    public CaseAttributesDiscovery(String caseId,int position, String value) {
        this.caseId = caseId;
        this.position = position;
        this.value = value;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
