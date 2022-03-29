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

package org.apromore.logman;

import java.time.Instant;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.joda.time.DateTime;

public class Constants {
    public static final String LIFECYCLE_START = "start";
    public static final String LIFECYCLE_COMPLETE = "complete";
    public static final String PLUS_COMPLETE_CODE = "+complete";
    public static final String PLUS_START_CODE = "+start";
    public static final String CONCEPT_NAME = "concept:name";
    public static final String TIMESTAMP_KEY = "time:timestamp";
    
    public static final String MISSING_STRING_VALUE = "";
    public static final long MISSING_TIMESTAMP = Instant.EPOCH.toEpochMilli();
    public static final DateTime MISSING_DATETIME = new DateTime(MISSING_TIMESTAMP);
    public static final long MISSING_LONG_VALUE = Long.MIN_VALUE;
    public static final double MISSING_DOUBLE_VALUE = Double.MIN_VALUE;
    
	public final static String START_NAME = "|>"; //marker for the start event in a trace in simplifiedNameMap
    public final static String END_NAME = "[]"; //marker for the end event in a trace in simplifiedNameMap
    
    public final static String ATT_KEY_CONCEPT_NAME = XConceptExtension.KEY_NAME;
    public final static String ATT_KEY_RESOURCE = XOrganizationalExtension.KEY_RESOURCE;
    public final static String ATT_KEY_GROUP = XOrganizationalExtension.KEY_GROUP;
    public final static String ATT_KEY_ROLE = XOrganizationalExtension.KEY_ROLE;
    public final static String ATT_KEY_LIFECYCLE_TRANSITION = XLifecycleExtension.KEY_TRANSITION;
    public final static String ATT_KEY_TIMESTAMP = XTimeExtension.KEY_TIMESTAMP;
    public final static String ATT_KEY_START_TIME = "time:start_timestamp";
    public final static String ATT_KEY_END_TIME = "time:end_timestamp";
}
