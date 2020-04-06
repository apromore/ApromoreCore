/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

public interface Constants {

    String caseIdLabel = "caseId";
    String activityLabel = "activity";
    String timestampLabel = "timestamp";
    String startTimestampLabel = "startTimestamp";
    String otherTimestampLabel = "otherTimestamp";
    String resourceLabel = "resource";

    String[] caseIdValues    = {"case", "case id", "case-id", "service id", "event id", "caseid", "serviceid"};
    String[] activityValues  = {"activity", "activity id", "activity-id", "operation", "event"};
    String[] resourceValues  = {"resource", "agent", "employee", "group"};
    String[] timestampValues = {"timestamp", "end date", "complete timestamp", "time:timestamp",
            "completion time", "end timestamp"};
    String[] StartTsValues   = {"start date", "start timestamp", "start time"};
}
