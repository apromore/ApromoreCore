/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne. All Rights Reserved.
 *
 */

package org.apromore.apmlog.filter.criterion.types;

/**
 * @author Chii Chang (Last modified: 28/01/2020)
 */
public enum CodeType {
    CONCEPT_NAME, VARIANT, D_FOLLOW, E_FOLLOW, LIFECYCLE,
    ORG_GROUP, ORG_RESOURCE, ORG_ROLE, TIMEFRAME, DURATION_RANGE,
    START_TIME_RANGE, END_TIME_RANGE, CASE_NAME, CASE_UTIL,
    TOTAL_PROCESSING_TIME, AVERAGE_PROCESSING_TIME, MAX_PROCESSING_TIME,
    TOTAL_WAITING_TIME, AVERAGE_WAITING_TIME, MAX_WAITING_TIME,
    CASE_CASE_ATTR, CASE_EVENT_ATTR, EVENT_ATTR,
    REWORK_REPETITION,
    UNKNOWN
}
