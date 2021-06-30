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
package org.apromore.apmlog.filter;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.AbstractLogImpl;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.logobjects.ImmutableLog;
import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.stats.CaseAttributeValue;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.eclipse.collections.impl.map.immutable.ImmutableUnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PLog does not update case variant, event attribute values and case attribute values.
 * The three collections need to be either handled by the Plugins
 * or generated based on filtering the corresponding values from the immutableLog
 */
public class PLog extends AbstractLogImpl implements Serializable {

    private BitSet validTraceIndexBS;
    private APMLog immutableLog;
    private List<PTrace> originalPTraces;
    private final List<PTrace> pTraces = new ArrayList<>();
    private Map<String, PTrace> pTracesMap;

    public PLog(APMLog immutableLog) {
        this.immutableLog = immutableLog;
        int traceSize = immutableLog.getTraces().size();
        validTraceIndexBS = new BitSet(traceSize);
        validTraceIndexBS.set(0, traceSize);
        setLogName(immutableLog.getLogName());
        setActivityNameIndicatorMap(immutableLog.getActivityNameIndicatorMap());
        initStats();
    }

    // ===============================================================================================================
    // GET methods
    // ===============================================================================================================


    public BitSet getValidTraceIndexBS() {
        return validTraceIndexBS;
    }

    public List<PTrace> getPTraces() {
        return pTraces;
    }

    public PTrace get(String caseId) {
        return LogStatsAnalyzer.findCaseById(caseId, this);
    }

    public long size() {
        return pTraces.size();
    }

    public PTrace get(int index) {
        return pTraces.get(index);
    }

    public List<PTrace> getOriginalPTraces() {
        return originalPTraces;
    }

    public APMLog getImmutableLog() {
        return immutableLog;
    }

    public long getDuration() {
        return TimeStatsProcessor.getPLogDuration(this);
    }

    public Map<String, PTrace> getPTracesMap() {
        return pTracesMap;
    }

    public ImmutableUnifiedMap<String, UnifiedSet<CaseAttributeValue>> getImmutableCaseAttributeValues() {
        return immutableLog.getImmutableCaseAttributeValues();
    }

    public ImmutableUnifiedMap<String, UnifiedSet<EventAttributeValue>> getImmutableEventAttributeValues() {
        return immutableLog.getImmutableEventAttributeValues();
    }

    // ===============================================================================================================
    // SET methods
    // ===============================================================================================================


    public void setValidTraceIndexBS(BitSet validTraceIndexesBS) {
        this.validTraceIndexBS = validTraceIndexesBS;
    }

    public void setPTraces(List<PTrace> traces) {
        this.pTraces.clear();
        if (traces != null && !traces.isEmpty()) {
            this.pTraces.addAll(traces);
            updateStats();
        }
    }

    public void resetIndex() {
        validTraceIndexBS.set(0, immutableLog.size());
        setPTraces(originalPTraces);
        for (PTrace pTrace : pTraces) {
            pTrace.resetIndex();
        }
    }

    // ===============================================================================================================
    // Operation methods
    // ===============================================================================================================

    private void initStats() {
        int index = 0;
        originalPTraces = immutableLog.getTraces().stream()
                .map(x -> createPTraceAndUpdateIndex(x, index))
                .collect(Collectors.toList());

        this.pTraces.clear();
        this.pTraces.addAll(originalPTraces);

        setActivityInstances(immutableLog.getActivityInstances());
        pTracesMap = pTraces.stream().collect(Collectors.toMap( PTrace::getCaseId, x -> x));
    }

    private PTrace createPTraceAndUpdateIndex(ATrace aTrace, int index) {
        PTrace pTrace = new PTrace(index, aTrace);
        index += 1;
        return pTrace;
    }

    private void updateStats() {
        pTracesMap = pTraces.stream().collect(Collectors.toMap( PTrace::getCaseId, x -> x));
        List<ATrace> traceList = pTraces.stream().collect(Collectors.toList());

        List<ActivityInstance> instances = traceList.stream()
                .flatMap(x -> x.getActivityInstances().stream())
                .collect(Collectors.toList());

        setActivityInstances(instances);
    }

    public ImmutableLog toImmutableLog() throws EmptyInputException {
        setPTraces(LogStatsAnalyzer.getValidTraces(this));
        return new ImmutableLog(this.immutableLog.getLogName(),
                pTraces.stream().collect(Collectors.toList()),
                this.immutableLog.getActivityNameIndicatorMap());
    }

    public void reset() {
        validTraceIndexBS.set(0, originalPTraces.size());
        this.pTraces.clear();
        this.pTraces.addAll(originalPTraces);
        for (PTrace pTrace : pTraces) {
            pTrace.reset();
        }
        activityInstances.clear();
        activityInstances.addAll(immutableLog.getActivityInstances());
    }



    // ===============================================================================================================
    // Special methods
    // ===============================================================================================================
    /**
     * A custom PTrace list that maintains the original PTrace list
     * while each PTrace contains up-to-date event BitSet.
     * The event BitSet of the invalid PTrace of this list
     * contains 'false' for all the elements of the event BitSet
     * @return List<PTrace>
     */
    public List<PTrace> getCustomPTraceList() {
        BitSet currentBS = this.validTraceIndexBS;

        Map<String, List<PTrace>> idPTraces = pTraces.stream()
                .collect(Collectors.groupingBy(PTrace::getCaseId));

        List<PTrace> theCusPTraceList = originalPTraces;

        for (PTrace pTrace : theCusPTraceList) {
            if (currentBS.get(pTrace.getImmutableIndex())) {
                pTrace.setValidEventIndexBS(idPTraces.get(pTrace.getCaseId()).get(0).getValidEventIndexBS());
            } else {
                pTrace.setValidEventIndexBS(new BitSet(pTrace.getImmutableEvents().size()));
            }
        }

        return theCusPTraceList;

    }

    private PTrace getPTraceByImmutableIndex(int index) {
        return pTraces.stream()
                .filter(x -> x.getImmutableIndex() == index)
                .findFirst()
                .orElse(null);
    }
}
