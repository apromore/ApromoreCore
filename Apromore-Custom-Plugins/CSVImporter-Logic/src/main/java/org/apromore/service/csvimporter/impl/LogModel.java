/*
 * Copyright © 2009-2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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