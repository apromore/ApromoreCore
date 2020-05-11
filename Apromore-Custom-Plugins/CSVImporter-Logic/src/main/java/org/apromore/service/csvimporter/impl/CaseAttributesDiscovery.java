package org.apromore.service.csvimporter.impl;

import lombok.Data;

@Data
class CaseAttributesDiscovery {

    private String caseId;
    private int position;
    private String value;

    public CaseAttributesDiscovery(String caseId,int position, String value) {
        this.caseId = caseId;
        this.position = position;
        this.value = value;
    }

}
