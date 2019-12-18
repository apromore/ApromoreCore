package org.apromore.service.csvimporter.impl;

public interface Constants {

    final String caseid    = "caseid";
    final String activity  = "activity";
    final String timestamp = "timestamp";
    final String tsStart   = "startTimestamp";
    final String tsValue   = "otherTimestamp";
    final String resource  = "resource";

    final String[] caseIdValues    = {"case", "case id", "case-id", "service id", "event id", "caseid", "serviceid"};
    final String[] activityValues  = {"activity", "activity id", "activity-id", "operation", "event"};
    final String[] resourceValues  = {"resource", "agent", "employee", "group"};
    final String[] timestampValues = {"timestamp", "end date", "complete timestamp", "time:timestamp",
            "completion time", "end timestamp"};
    final String[] StartTsValues   = {"start date", "start timestamp", "start time"};
}
