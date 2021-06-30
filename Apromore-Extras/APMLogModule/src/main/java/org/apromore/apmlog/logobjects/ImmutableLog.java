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
package org.apromore.apmlog.logobjects;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.exceptions.CaseIdNotFoundException;
import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.stats.CaseAttributeValue;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.apromore.apmlog.xes.APMLogToXLog;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.immutable.ImmutableUnifiedMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImmutableLog extends AbstractLogImpl implements APMLog, Serializable {

    private final List<ATrace> traces = new ArrayList<>();
    private Map<String, ATrace> tracesMap;
    protected UnifiedMap<String, UnifiedSet<CaseAttributeValue>> immutableCaseAttributeValues;
    protected UnifiedMap<String, UnifiedSet<EventAttributeValue>> immutableEventAttributeValues;

    // ------------------------------------------------------------
    // Config caseVariantGroupMap after the traces establishment
    // ------------------------------------------------------------
    protected Map<Integer, List<ATrace>> caseVariantGroupMap;

    public ImmutableLog(String logName,
                        List<ATrace> traces,
                        HashBiMap<String, Integer> activityNameIndicatorMap) throws EmptyInputException {

        setLogName(logName);
        setActivityNameIndicatorMap(activityNameIndicatorMap);
        setTraces(traces);
    }

    // ===============================================================================================================
    // GET methods
    // ===============================================================================================================
    @Override
    public List<ATrace> getTraces() {
        return traces;
    }

    @Override
    public int size() {
        return traces.size();
    }

    @Override
    public ATrace get(int index) {
        return traces.get(index);
    }

    @Override
    public ATrace get(String caseId) throws CaseIdNotFoundException {
        if (!tracesMap.containsKey(caseId)) throw new CaseIdNotFoundException(caseId);
        else return tracesMap.get(caseId);
    }

    @Override
    public long getDuration() {
        return TimeStatsProcessor.getAPMLogDuration(this);
    }

    @Override
    public Map<Integer, List<ATrace>> getCaseVariantGroupMap() {
        return caseVariantGroupMap;
    }

    @Override
    public ImmutableUnifiedMap<String, UnifiedSet<CaseAttributeValue>> getImmutableCaseAttributeValues() {
        return new ImmutableUnifiedMap(immutableCaseAttributeValues);
    }

    @Override
    public ImmutableUnifiedMap<String, UnifiedSet<EventAttributeValue>> getImmutableEventAttributeValues() {
        return new ImmutableUnifiedMap(immutableEventAttributeValues);
    }

    // ===============================================================================================================
    // SET methods
    // ===============================================================================================================

    private void setTraces(List<ATrace> traces) throws EmptyInputException {
        if (traces == null || traces.isEmpty()) throw new EmptyInputException(this);

        this.traces.clear();
        if (traces != null && !traces.isEmpty()) {
            this.traces.addAll(traces);
            updateStats();
        }
    }

    // ===============================================================================================================
    // Operation methods
    // ===============================================================================================================

    protected void updateStats() {
        tracesMap = traces.stream().collect(Collectors.toMap( ATrace::getCaseId, x -> x ));
        updateStats(traces);
    }


    protected void updateStats(List<ATrace> traces) {

        List<ActivityInstance> instances = traces.stream()
                .flatMap(x -> x.getActivityInstances().stream())
                .collect(Collectors.toList());

        setActivityInstances(instances);

        immutableCaseAttributeValues = LogStatsAnalyzer.getCaseAttributeValues(traces);
        immutableEventAttributeValues = LogStatsAnalyzer.getEventAttributeValues(activityInstances, traces.size());
        caseVariantGroupMap = LogStatsAnalyzer.getCaseVariantGroupMap(traces);
    }

    // ===============================================================================================================
    // Clone methods
    // ===============================================================================================================

    public APMLog deepClone() throws EmptyInputException {
        List<ATrace> tracesClone = traces.stream()
                .map(ATrace::deepClone)
                .collect(Collectors.toList());

        return new ImmutableLog(logName, tracesClone, activityNameIndicatorMap);
    }

    // ===============================================================================================================
    // XES methods
    // ===============================================================================================================

    @Override
    public XLog toXLog() {
        return APMLogToXLog.getXLog(this);
    }
}
