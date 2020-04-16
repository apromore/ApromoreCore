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

import java.util.Map;
import java.util.HashMap;

public interface Constants {

    final String caseid    = "caseid";
    final String activity  = "activity";
    final String timestamp = "timestamp";
    final String tsStart   = "startTimestamp";
    final String tsValue   = "otherTimestamp";
    final String resource  = "resource";

    final Map<String, String> fieldMap = new HashMap<String, String>() {
        {
            put("caseid", "Case ID");
            put("activity", "Activity");
            put("timestamp", "Timestamp");
        }
    };

    final String[] caseIdValues    = {"case", "case id", "case-id", "service id", "event id", "caseid", "serviceid"};
    final String[] activityValues  = {"activity", "activity id", "activity-id", "operation", "event"};
    final String[] resourceValues  = {"resource", "agent", "employee", "group"};
    final String[] timestampValues = {"timestamp", "end date", "complete timestamp", "time:timestamp",
            "completion time", "end timestamp", "end time", "timestamp2"};
    final String[] StartTsValues   = {"start date", "start timestamp", "start time", "timestamp1"};
}
