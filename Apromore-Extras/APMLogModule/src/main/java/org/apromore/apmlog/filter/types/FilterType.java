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
