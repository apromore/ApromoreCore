/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.csvimporter.impl;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
class LogEventModel {

    private String caseID;
    private String activity;
    private Timestamp endTimestamp;
    private Timestamp startTimestamp;
    private String resource;
    private Map<String, Timestamp> otherTimestamps;
    private Map<String, String> eventAttributes;
    private Map<String, String> caseAttributes;


    LogEventModel(String caseID, String activity, Timestamp endTimestamp, Timestamp startTimestamp, Map<String, Timestamp> otherTimestamps, String resource, Map<String, String> eventAttributes, Map<String, String> caseAttributes) {
        this.caseID = caseID;
        this.activity = activity;
        this.endTimestamp = endTimestamp;
        this.startTimestamp = startTimestamp;
        this.otherTimestamps = otherTimestamps;
        this.resource = resource;
        this.eventAttributes = eventAttributes;
        this.caseAttributes = caseAttributes;
    }
}
