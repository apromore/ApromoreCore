/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

public enum FilterType {

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

    // path
    DIRECT_FOLLOW("DIRECT_FOLLOW"),
    EVENTUAL_FOLLOW("EVENTUAL_FOLLOW"),

    // others
    UNKNOWN("UNKNOWN");

    private String stringValue;

    FilterType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String toString() {
        return stringValue;
    }

}
