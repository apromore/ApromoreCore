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
package org.apromore.logfilter.criteria.model;

/**
 * 
 * @author Bruce Nguyen
 * Modified by Chii Chang (30/12/2019)
 *
 */
public enum Type {
    CONCEPT_NAME, CASE_VARIANT, DIRECT_FOLLOW, EVENTUAL_FOLLOW, LIFECYCLE_TRANSITION,
    ORG_GROUP, ORG_RESOURCE, ORG_ROLE, TIME_TIMESTAMP, DURATION_RANGE,
    TIME_STARTRANGE, TIME_ENDRANGE,
    CASE_ID, CASE_UTILIZATION,
    DURATION_TOTAL_PROCESSING, DURATION_AVERAGE_PROCESSING, DURATION_MAX_PROCESSING,
    DURATION_TOTAL_WAITING, DURATION_AVERAGE_WAITING, DURATION_MAX_WAITING,
    REWORK_REPETITION,
    UNKNOWN
}
