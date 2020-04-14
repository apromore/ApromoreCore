package org.apromore.apmlog.filter.types;

public enum FilterType {
//    // standard event attributes            XES representation
//    CASE_EVENT_ACTIVITY,                    // concept:name (filter in case section)
//    CASE_EVENT_RESOURCE,                    // org:resource (filter in case section)
//    CASE_EVENT_GROUP,                       // org:group (filter in case section)
//    CASE_EVENT_ROLE,                        // org:role (filter in case section)
//    CASE_EVENT_LIFECYCLE,                   // lifecycle:transition (filter in case section)
//
//    EVENT_EVENT_ACTIVITY,                   // concept:name (filter in event section)
//    EVENT_EVENT_RESOURCE,                   // org:resource (filter in event section)
//    EVENT_EVENT_GROUP,                      // org:group (filter in event section)
//    EVENT_EVENT_ROLE,                       // org:role (filter in event section)
//    EVENT_EVENT_LIFECYCLE,                  // lifecycle:transition (filter in event section)

    // other attributes
    EVENT_EVENT_ATTRIBUTE,
    CASE_EVENT_ATTRIBUTE,
    CASE_CASE_ATTRIBUTE,
    CASE_VARIANT,
    CASE_ID,

    // activity-based
    REWORK_REPETITION,

    // range-based
    CASE_TIME,                              // time:timestamp (filter in case section)
    EVENT_TIME,                             // time:timestamp (filter in event section)
    STARTTIME,
    ENDTIME,
    DURATION,
    CASE_UTILISATION,
    TOTAL_PROCESSING_TIME,
    AVERAGE_PROCESSING_TIME,
    MAX_PROCESSING_TIME,
    TOTAL_WAITING_TIME,
    AVERAGE_WAITING_TIME,
    MAX_WAITING_TIME,

    // path
    DIRECT_FOLLOW,
    EVENTUAL_FOLLOW,

    // others
    UNKNOWN
}
