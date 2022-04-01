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
package org.apromore.apmlog.logobjects;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.exceptions.CaseIdNotFoundException;
import org.apromore.calendar.model.CalendarModel;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XLogImpl;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chii Chang
 */
public class EmptyImmutableLog extends AbstractLogImpl implements APMLog {

    private final List<ATrace> traces = new ArrayList<>();

    public EmptyImmutableLog(String logName, HashBiMap<String, Integer> activityNameIndicatorMap) {
        setLogName(logName);
        setActivityNameIndicatorMap(activityNameIndicatorMap);
        setEmptyTraces();
    }

    private void setEmptyTraces() {
        this.traces.clear();
        this.activityInstances.clear();
        startTime = 0;
        endTime = 0;
    }

    @Override
    public List<ATrace> getTraces() {
        return traces;
    }

    @Override
    public ATrace get(String caseId) throws CaseIdNotFoundException {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public ATrace get(int index) {
        return null;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public APMLog deepClone() {
        return new EmptyImmutableLog(logName, activityNameIndicatorMap);
    }

    @Override
    public XLog toXLog() {
        return new XLogImpl();
    }

    @Override
    public CalendarModel getCalendarModel() {
        return null;
    }
}
