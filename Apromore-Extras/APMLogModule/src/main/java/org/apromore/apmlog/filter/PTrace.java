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

import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.AbstractTraceImpl;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.logobjects.ImmutableTrace;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class PTrace extends AbstractTraceImpl implements Comparable<PTrace>, Serializable {

    private BitSet validEventIndexBS;
    private ATrace immutableTrace;
    private int mutableIndex;
    private List<ActivityInstance> originalActivityInstances;

    public PTrace(int mutableIndex, ATrace immutableTrace) {
        super(immutableTrace.getImmutableIndex(),
                immutableTrace.getCaseId(),
                immutableTrace.getImmutableEvents(),
                immutableTrace.getActivityInstances(),
                immutableTrace.getAttributes());

        originalActivityInstances = new ArrayList<>(immutableTrace.getActivityInstances());

        this.mutableIndex = mutableIndex;
        this.immutableTrace = immutableTrace;
        int eventSize = immutableTrace.getImmutableEvents().size();
        validEventIndexBS = new BitSet(eventSize);
        validEventIndexBS.set(0, eventSize);
        this.caseVariantId = immutableTrace.getCaseVariantId();
        updateStats();
    }

    // ===================================
    // For clone
    // ===================================
    public PTrace(int mutableIndex,
                  ATrace immutableTrace,
                  List<ActivityInstance> activityInstances) {
        super(immutableTrace.getImmutableIndex(),
                immutableTrace.getCaseId(),
                immutableTrace.getImmutableEvents(),
                immutableTrace.getActivityInstances(),
                immutableTrace.getAttributes());

        originalActivityInstances = new ArrayList<>(immutableTrace.getActivityInstances());

        this.mutableIndex = mutableIndex;
        int eventSize = immutableTrace.getImmutableEvents().size();
        validEventIndexBS = new BitSet(eventSize);
        validEventIndexBS.set(0, eventSize);

        setActivityInstances(activityInstances);
        updateStats();
    }

    // ========================================================
    // GET methods
    // ========================================================

    public long size() {
        return activityInstances.size();
    }

    public long getSize() { return activityInstances.size(); }

    public int getMutableIndex() {
        return mutableIndex;
    }

    public BitSet getValidEventIndexBS() {
        return validEventIndexBS;
    }

    public Number getCaseIdDigit() {
        return Util.isNumeric(caseId) ? Double.valueOf(caseId) : -1;
    }

    public BitSet getOriginalValidEventIndexBS() {
        int xEventsSize = immutableTrace.getImmutableEvents().size();
        BitSet bs = new BitSet(xEventsSize);
        bs.set(0, xEventsSize);
        return bs;
    }

    public List<ActivityInstance> getOriginalActivityInstances() {
        return originalActivityInstances;
    }

    public void setMutableIndex(int mutableIndex) {
        this.mutableIndex = mutableIndex;
    }

    public void setValidEventIndexBS(BitSet validEventIndexBS) {
        this.validEventIndexBS = validEventIndexBS;
        updateStats();
    }

    public void resetIndex() {
        validEventIndexBS.set(0, immutableTrace.getImmutableEvents().size());
    }

    public void reset() {
        resetIndex();
        setActivityInstances(originalActivityInstances);
    }

    public void updateActivities() {
        List<ActivityInstance> validActs = LogStatsAnalyzer.getValidActivitiesOf(this);
        this.setActivityInstances(validActs);
    }

    // ===============================================================================================================
    // Operation methods
    // ===============================================================================================================

    private void updateStats() {
        List<ActivityInstance> refinedAI = immutableTrace.getActivityInstances().stream()
                .filter(x -> validEventIndexBS.get(x.getFirstEventIndex()))
                .collect(Collectors.toList());

        setActivityInstances(refinedAI);
        updateTimeStats();
    }

    @Override
    public ATrace deepClone() {
        List<ActivityInstance> activityInstanceClone = activityInstances.stream()
                .map(ActivityInstance::clone)
                .collect(Collectors.toList());

        return new PTrace(mutableIndex,
                immutableTrace,
                activityInstanceClone);
    }

    public ImmutableTrace toImmutableTrace() {
        return new ImmutableTrace(immutableIndex, caseId, immutableEvents, activityInstances, attributes);
    }

    @Override
    public int compareTo(PTrace o) {
        if (Util.isNumeric(getCaseId()) && Util.isNumeric(o.getCaseId())) {
            if (getCaseIdDigit().doubleValue() > o.getCaseIdDigit().doubleValue()) return 1;
            else if (getCaseIdDigit().doubleValue() < o.getCaseIdDigit().doubleValue()) return -1;
            else return 0;
        } else {
            return getCaseId().compareTo(o.getCaseId());
        }
    }
}
