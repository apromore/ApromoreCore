package org.apromore.service.csvimporter.impl;

import java.sql.Timestamp;
import java.util.HashMap;

public class LogModel {

	private String caseID;
	private String concept;
	private Timestamp timestamp;
	private Timestamp startTimestamp;
	private String resource;

	private HashMap<String, Timestamp> otherTimestamps;
	private HashMap<String, String> others;



	public LogModel(String caseID, String concept, Timestamp timestamp,Timestamp startTimestamp,  HashMap<String, Timestamp> otherTimestamps, String resource, HashMap<String, String> others) {
		setCaseID(caseID);
		setConcept(concept);
		setTimestamp(timestamp);
		setStartTimestamp(startTimestamp);
		setResource(resource);
		setOtherTimestamps(otherTimestamps);
		setOthers(others);
	}



	public void setCaseID(String ID) {
		this.caseID = ID;
	}

	public String getCaseID() {
		return caseID;
	}

	public void setConcept(String con) {
		this.concept = con;
	}

	public String getConcept() {
		return concept;
	}


	public void setTimestamp(Timestamp timeSt) {
		this.timestamp = timeSt;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public Timestamp getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Timestamp startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setOtherTimestamps(HashMap<String, Timestamp> otherTimestamps) {
		this.otherTimestamps = otherTimestamps;
	}

	public HashMap<String, Timestamp> getOtherTimestamps() {
		return otherTimestamps;
	}

	public void setOthers(HashMap<String, String> oth)
	{
		this.others= oth;
	}

	public HashMap<String, String> getOthers()
	{
		return others;
	}

}