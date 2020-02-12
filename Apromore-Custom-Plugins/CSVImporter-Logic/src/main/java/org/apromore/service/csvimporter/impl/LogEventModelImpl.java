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

import org.apromore.service.csvimporter.LogEventModel;

import java.sql.Timestamp;
import java.util.Map;

class LogEventModelImpl implements LogEventModel {

    // Instance variable

    private String caseID;
    private String concept;
    private Timestamp timestamp;
    private Timestamp startTimestamp;
    private String resource;
    private Map<String, Timestamp> otherTimestamps;
    private Map<String, String> others;
    private Map<String, String> caseAttributes;


    // Constructor

    LogEventModelImpl(String caseID, String concept, Timestamp timestamp, Timestamp startTimestamp, Map<String, Timestamp> otherTimestamps, String resource, Map<String, String> others, Map<String, String> caseAttributes) {
        this.caseID = caseID;
        this.concept = concept;
        this.timestamp = timestamp;
        this.startTimestamp = startTimestamp;
        this.otherTimestamps = otherTimestamps;
        this.resource = resource;
        this.others = others;
        this.caseAttributes = caseAttributes;
    }


        // Accessors

    @Override
    public String getCaseID() {
        return caseID;
    }

    @Override
    public String getConcept() {
        return concept;
    }

    @Override
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public void setOtherTimestamps(Map<String, Timestamp> otherTimestamps) {
        this.otherTimestamps = otherTimestamps;
    }

    @Override
    public Map<String, Timestamp> getOtherTimestamps() {
        return otherTimestamps;
    }

    @Override
    public Map<String, String> getOthers() {
        return others;
    }

    @Override
    public Map<String, String> getCaseAttributes() {
        return caseAttributes;
    }
}
