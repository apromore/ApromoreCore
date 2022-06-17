/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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
package org.apromore.apmlog.filter.types;

/**
 * @author Chii Chang
 * modified: 2022-06-16 by Chii Chang
 */
public enum FilterType {

    // =====================================================================
    // Do not change the string values of the enums.
    // The String values have been used in the usermetadata already.
    // =====================================================================
    // attributes
    EVENT_EVENT_ATTRIBUTE("EVENT_EVENT_ATTRIBUTE"),
    CASE_EVENT_ATTRIBUTE("CASE_EVENT_ATTRIBUTE"),
    CASE_CASE_ATTRIBUTE("CASE_CASE_ATTRIBUTE"),
    CASE_VARIANT("CASE_VARIANT"),
    CASE_ID("CASE_ID"),

    // rework-based
    REWORK_REPETITION("REWORK_REPETITION"),

    // time
    CASE_TIME("CASE_TIME"),  // time:timestamp (filter in case section)
    EVENT_TIME("EVENT_TIME"),  // time:timestamp (filter in event section)
    STARTTIME("STARTTIME"),
    ENDTIME("ENDTIME"),

    // range-based
    DURATION("DURATION"),
    CASE_UTILISATION("CASE_UTILISATION"),
    TOTAL_PROCESSING_TIME("TOTAL_PROCESSING_TIME"),
    AVERAGE_PROCESSING_TIME("AVERAGE_PROCESSING_TIME"),
    MAX_PROCESSING_TIME("MAX_PROCESSING_TIME"),
    TOTAL_WAITING_TIME("TOTAL_WAITING_TIME"),
    AVERAGE_WAITING_TIME("AVERAGE_WAITING_TIME"),
    MAX_WAITING_TIME("MAX_WAITING_TIME"),
    CASE_SECTION_ATTRIBUTE_COMBINATION("CASE_SECTION_ATTRIBUTE_COMBINATION"),
    EVENT_ATTRIBUTE_DURATION("EVENT_ATTRIBUTE_DURATION"),
    ATTRIBUTE_ARC_DURATION("ATTRIBUTE_ARC_DURATION"),
    CASE_LENGTH("CASE_LENGTH"),
    CASE_COST("CASE_COST"),
    NODE_COST("NODE_COST"),

    // path
    DIRECT_FOLLOW("DIRECT_FOLLOW"),
    EVENTUAL_FOLLOW("EVENTUAL_FOLLOW"),

    // between
    BETWEEN("BETWEEN"),

    // others
    UNKNOWN("UNKNOWN");

    private final String stringValue;

    FilterType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String toDisplay() {
        switch (this) {
            case EVENT_EVENT_ATTRIBUTE: return "Event attribute (Inner cases)";
            case CASE_EVENT_ATTRIBUTE: return "Event attribute (Inter cases)";
            case CASE_CASE_ATTRIBUTE: return "Case attribute";
            case CASE_ID: return "Case ID";
            case REWORK_REPETITION: return "Rework";
            case CASE_TIME:
            case STARTTIME:
            case ENDTIME:
                return "Case timeframe";
            case EVENT_TIME: return "Event timeframe";
            case DURATION: return "Duration";
            case CASE_UTILISATION: return "Case utilization";
            case TOTAL_PROCESSING_TIME: return "Total processing time";
            case AVERAGE_PROCESSING_TIME: return "Average processing time";
            case MAX_PROCESSING_TIME: return "Max processing time";
            case TOTAL_WAITING_TIME: return "Total waiting time";
            case AVERAGE_WAITING_TIME: return "Average waiting time";
            case MAX_WAITING_TIME: return "Max waiting time";
            case CASE_SECTION_ATTRIBUTE_COMBINATION: return "Attribute combination (Inter cases)";
            case EVENT_ATTRIBUTE_DURATION: return "Node duration";
            case ATTRIBUTE_ARC_DURATION: return "Arc duration";
            case CASE_LENGTH: return "Case length";
            case CASE_VARIANT: return "Case variant";
            case BETWEEN: return "Between";
            case DIRECT_FOLLOW: return "Directly-follows";
            case EVENTUAL_FOLLOW: return "Eventually-follows";
            case CASE_COST: return "Case cost";
            case NODE_COST: return "Node cost";
            default: return this.stringValue;
        }
    }

    public static boolean isCaseFilter(FilterType filterType) {
        switch (filterType) {
            case EVENT_EVENT_ATTRIBUTE:
            case EVENT_TIME:
            case BETWEEN:
                return false;
            default:
                return true;
        }
    }
}
