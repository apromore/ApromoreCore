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
package org.apromore.apmlog;

import org.apromore.apmlog.exceptions.CaseIdNotFoundException;
import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.calendar.model.CalendarModel;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;

import java.util.List;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (03/02/2020)
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (06/03/2020) - public APMLog(List<ATrace> inputTraceList)
 * Modified: Chii Chang (12/05/2020)
 * Modified: Chii Chang (27/10/2020)
 * Modified: Chii Chang (26/01/2021)
 * Modified: Chii Chang (22/06/2021)
 *
 * An APMLog contains a list of ATrace
 * An ATrace contains a list of events
 * An ActivityInstance is a list of related events
 * An event describes the detail of an ActivityInstance.
 *
 * Threre are two types implementations - ImmutableLog and PLog
 * ImmutableLog is the default log object converted from XLog.
 * ImmutableLog is for caching purpose and it provides the basic stats values of the log.
 * PLog contains indexes. It is the mutable log object for runtime operations.
 */
public interface APMLog {

    String getLogName();

    List<ATrace> getTraces();

    /**
     * Returns all the activity instances of the traces
     * @return
     */
    List<ActivityInstance> getActivityInstances();

    /**
     * APMLog identifies the case variant of a Trace based on the sequence of its activities.
     * In order to reduce memory usage, each activity instance name in APMLog
     * has an indicator (nameIndicator).
     * ActivityNameIndicatorMap is the map that manages such mapping information
     * @return
     */
    HashBiMap<String, Integer> getActivityNameIndicatorMap();

    /**
     * This map is for rapidly retrieve a trace based on caseId.
     * Store Key as 'caseId' and Value as the trace
     *
     * @return
     */
//    Map<String, ATrace> getTracesMap();

    ATrace get(String caseId) throws CaseIdNotFoundException;

    /**
     * Represents the size of traces
     * @return
     */
    int size();

    /**
     * Return a trace based on the index
     * @param index
     * @return
     */
    ATrace get(int index);

    String getTimeZone();

    /**
     * Returns the start time of the log in epoch/Unix timestamp
     * @return
     */
    long getStartTime();

    /**
     * Returns the end time of the log in epoch/Unix timestamp
     * @return
     */
    long getEndTime();

    /**
     * Returns the duration of the log in milliseconds
     * @return
     */
    long getDuration();

    /**
     *
     * @return CalendarModel if exist
     */
    CalendarModel getCalendarModel();

    void setCalendarModel(CalendarModel calendarModel);

    /**
     * When setCalendarModel() being used to assign calendarModel, hasCustomCalendar will return true
     * @return
     */
    boolean hasCustomCalendar();

    APMLog deepClone() throws EmptyInputException;

    /**
     * Convert this APMLog to an OpenXES-XLog
     * @return
     */
    XLog toXLog();
}
