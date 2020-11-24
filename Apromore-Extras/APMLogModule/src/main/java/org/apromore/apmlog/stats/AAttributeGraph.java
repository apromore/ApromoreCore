/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.apmlog.stats;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.PLog;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.List;
import java.util.Set;

public class AAttributeGraph {

    private APMLog apmLog;
    private UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> eventAttributeOccurMap;

    public AAttributeGraph(APMLog apmLog) {
        this.apmLog = apmLog;
        this.eventAttributeOccurMap = apmLog.getEventAttributeOccurMap();
    }

    public boolean hasNextValue(String key) {
        UnifiedMap<String, UnifiedSet<AActivity>> valOccurMap = eventAttributeOccurMap.get(key);
        for (String val : valOccurMap.keySet()) {
            DurSubGraph dsg = getNextValueDurations(key, val);
            if (dsg.getValCaseOccurMap().size() > 0) return true;
        }
        return false;
    }

    public boolean hasPreviousValue(String key) {
        UnifiedMap<String, UnifiedSet<AActivity>> valOccurMap = eventAttributeOccurMap.get(key);
        for (String val : valOccurMap.keySet()) {
            DurSubGraph dsg = getPreviousValueDurations(key, val);
            if (dsg.getValCaseOccurMap().size() > 0) return true;
        }
        return false;
    }

    public boolean contains(String key) {
        return eventAttributeOccurMap.containsKey(key);
    }

    public boolean contains(String key, String value) {
        if (!eventAttributeOccurMap.containsKey(key)) return false;

        return eventAttributeOccurMap.get(key).contains(value);
    }

    public Set<String> getKeys() {
        return eventAttributeOccurMap.keySet();
    }

    public DurSubGraph getValueDurations(String key) {
        if (!eventAttributeOccurMap.containsKey(key)) return null;

        UnifiedMap<String, UnifiedSet<AActivity>> valActsMap = eventAttributeOccurMap.get(key);

        DurSubGraph durSubGraph = new DurSubGraph();

        for (String val : valActsMap.keySet()) {
            UnifiedSet<AActivity> activities = valActsMap.get(val);
            for (AActivity activity : activities) {
                double actDur = activity.getDuration();
                int traceIndex = activity.getMutableTraceIndex();
                durSubGraph.addDuration(val, actDur, traceIndex);

            }
        }


        return durSubGraph;

    }


    public DurSubGraph getNextValueDurations(String key, String value) {
        if (!eventAttributeOccurMap.containsKey(key)) return null;
        if (!eventAttributeOccurMap.get(key).containsKey(value)) return null;

        UnifiedSet<AActivity> activities = eventAttributeOccurMap.get(key).get(value);

        DurSubGraph subGraph = new DurSubGraph();


        for (AActivity act : activities) {
            int actIndex = act.getMutableIndex();
            ATrace parentTrace = ((PLog) apmLog).getPTraceList().get(act.getMutableTraceIndex());
            int traceIndex = parentTrace.getMutableIndex();

            List<AActivity> activityList = parentTrace.getActivityList();

            if (actIndex + 1 < activityList.size()) {
                AActivity nextAct = activityList.get(actIndex+1);

                UnifiedMap<String, String> nAttr = nextAct.getAllAttributes();
                if (nAttr.containsKey(key)) {
                    String nVal = nAttr.get(key);

                    long aET = act.getEndTimeMilli();
                    long nST = nextAct.getStartTimeMilli();

                    if (nST > aET) {
                        long dur = nST - aET;
                        subGraph.addDuration(nVal, dur, traceIndex);
                    }
                }
            }
        }

        return subGraph;
    }

    public DurSubGraph getPreviousValueDurations(String key, String value) {
        if (!eventAttributeOccurMap.containsKey(key)) return null;
        if (!eventAttributeOccurMap.get(key).containsKey(value)) return null;

        UnifiedSet<AActivity> activities = eventAttributeOccurMap.get(key).get(value);

        DurSubGraph subGraph = new DurSubGraph();

        for (AActivity act : activities) {
            int actIndex = act.getMutableIndex();
            ATrace parentTrace = apmLog.get(act.getMutableTraceIndex());
            if (actIndex < parentTrace.getActivityList().size()-1 && actIndex > 0) {
                AActivity prevAct = parentTrace.getActivityList().get(actIndex-1);
                int traceIndex = prevAct.getMutableTraceIndex();

                UnifiedMap<String, String> pAttr = prevAct.getAllAttributes();
                if (pAttr.containsKey(key)) {
                    String pVal = pAttr.get(key);

                    long aST = act.getStartTimeMilli();
                    long pET = prevAct.getEndTimeMilli();
                    long dur = aST > pET ? aST - pET : 0;

                    subGraph.addDuration(pVal, dur, traceIndex);
                }
            }
        }

        return subGraph;
    }


        /**
         * Used by PD
         * @param attributeKey
         * @param indegree
         * @param outdegree
         * @param theLog
         * @return
         */
    public UnifiedSet<Double> getDurations(String attributeKey, String indegree, String outdegree, APMLog theLog) {

        DurSubGraph subGraph = getNextValueDurations(attributeKey, indegree);

        UnifiedMap<String, UnifiedMap<Double, UnifiedSet<Integer>>> valDurCaseMap = subGraph.getValDurCaseIndexMap();

        UnifiedSet<Double> set = new UnifiedSet<>();

        if (valDurCaseMap.containsKey(outdegree)) {

            UnifiedMap<Double, UnifiedSet<Integer>> durCaseMap = valDurCaseMap.get(outdegree);

            set = new UnifiedSet<>(durCaseMap.keySet());


        }
        return set;


    }




}
