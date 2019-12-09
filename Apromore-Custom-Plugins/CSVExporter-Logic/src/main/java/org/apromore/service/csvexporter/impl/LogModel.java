package org.apromore.service.csvexporter.impl;

import java.util.HashMap;

public class LogModel {

	private HashMap<String, String> attributeList;

	public LogModel(HashMap<String, String> attributeList) {
		setAttributeList(attributeList);
	}

    public void setAttributeList(HashMap<String, String> oth)
    {
    	this.attributeList = oth;
    }

    public HashMap<String, String> getAttributeList()
    {
        return attributeList;
    }

}
