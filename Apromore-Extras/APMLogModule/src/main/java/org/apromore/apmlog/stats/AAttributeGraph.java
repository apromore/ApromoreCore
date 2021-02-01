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
package org.apromore.apmlog.stats;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class AAttributeGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(AAttributeGraph.class);

    private APMLog apmLog;
    private UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> eventAttributeOccurMap;

    public AAttributeGraph(APMLog apmLog) {
        this.apmLog = apmLog;
        this.eventAttributeOccurMap = apmLog.getEventAttributeOccurMap();

    }

    public boolean hasNextValue(String key) {
        if (!eventAttributeOccurMap.containsKey(key)) return false;

        UnifiedMap<String, UnifiedSet<AActivity>> valOccurMap = eventAttributeOccurMap.get(key);
        for (String value : valOccurMap.keySet()) {
            UnifiedSet<AActivity> activities = valOccurMap.get(value);
            for (AActivity act : activities) {
                int actIndex = act.getMutableIndex();
                ATrace parentTrace = apmLog.getTraceList().get(act.getMutableTraceIndex());

                List<AActivity> activityList = parentTrace.getActivityList();

                if (actIndex + 1 < activityList.size()) {
                    AActivity nextAct = activityList.get(actIndex+1);

                    UnifiedMap<String, String> nAttr = nextAct.getAllAttributes();
                    if (nAttr.containsKey(key)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasPreviousValue(String key) {
        if (!eventAttributeOccurMap.containsKey(key)) return false;

        UnifiedMap<String, UnifiedSet<AActivity>> valOccurMap = eventAttributeOccurMap.get(key);
        for (String value : valOccurMap.keySet()) {
            UnifiedSet<AActivity> activities = eventAttributeOccurMap.get(key).get(value);
            for (AActivity act : activities) {
                int actIndex = act.getMutableIndex();
                ATrace parentTrace = apmLog.get(act.getMutableTraceIndex());
                if (actIndex < parentTrace.getActivityList().size()-1 && actIndex > 0) {
                    AActivity prevAct = parentTrace.getActivityList().get(actIndex-1);

                    UnifiedMap<String, String> pAttr = prevAct.getAllAttributes();
                    if (pAttr.containsKey(key)) {
                        return true;
                    }
                }
            }
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

    public UnifiedMap<String, DurSubGraph> getNextValueDurSubGraphs(String key, Set<String> values, PLog pLog) {
        if (!eventAttributeOccurMap.containsKey(key)) return null;

        UnifiedMap<String, DurSubGraph> durSubGraphs = new UnifiedMap<>();
        for (String v : values) {
            durSubGraphs.put(v, new DurSubGraph());
        }

//        int count = 1;

        for (PTrace pTrace : pLog.getPTraceList()) {
            int pTraceIndex = pTrace.getMutableIndex();



            List<AActivity> activityList = pTrace.getActivityList();
            for (AActivity iAct : activityList) {
                int iActIndex = iAct.getMutableIndex();
                int nActIndex = iActIndex + 1;
                if (nActIndex < activityList.size()) {
                    if (nActIndex < activityList.size()) {
                        AActivity nAct = activityList.get(nActIndex);
                        UnifiedMap<String, String> iAttr = iAct.getAllAttributes();
                        UnifiedMap<String, String> nAttr = nAct.getAllAttributes();

                        if (iAttr.containsKey(key) && nAttr.containsKey(key)) {
                            String iVal = iAttr.get(key);
                            String nVal = nAttr.get(key);
                            long aET = iAct.getEndTimeMilli();
                            long nST = nAct.getStartTimeMilli();
                            long dur = nST > aET ? nST - aET : 0;
                            durSubGraphs.get(iVal).addDuration(nVal, dur, pTraceIndex);
                        }
                    }
                }
            }
        }

        return durSubGraphs;

    }

    public UnifiedMap<String, DurSubGraph> getPrevValueDurSubGraphs(String key, Set<String> values, PLog pLog) {
        if (!eventAttributeOccurMap.containsKey(key)) return null;

        UnifiedMap<String, DurSubGraph> durSubGraphs = new UnifiedMap<>();
        for (String v : values) {
            durSubGraphs.put(v, new DurSubGraph());
        }

        for (PTrace pTrace : pLog.getPTraceList()) {
            int pTraceIndex = pTrace.getMutableIndex();
            List<AActivity> activityList = pTrace.getActivityList();
            for (AActivity iAct : activityList) {
                UnifiedMap<String, String> iAttr = iAct.getAllAttributes();
                if (iAttr.containsKey(key)) {
                    int iActIndex = iAct.getMutableIndex();
                    int pActIndex = iActIndex - 1;
                    if (pActIndex >= 0) {
                        AActivity pAct = activityList.get(pActIndex);
                        UnifiedMap<String, String> pAttr = pAct.getAllAttributes();
                        if (pAttr.containsKey(key)) {
                            String iVal = iAttr.get(key);
                            String pVal = pAttr.get(key);
                            if (durSubGraphs.containsKey(pVal)) {
                                DurSubGraph subGraph = durSubGraphs.get(iVal);
                                long aST = iAct.getStartTimeMilli();
                                long pET = pAct.getEndTimeMilli();
                                long dur = aST > pET ? aST - pET : 0;
                                subGraph.addDuration(pVal, dur, pTraceIndex);
                            }
                        }
                    }
                }
            }
        }

        return durSubGraphs;

    }


    public DurSubGraph getNextValueDurations(String key, String baseValue, APMLog log) {
        return getArcValueDurations(key, baseValue, "next", log);
    }

    public DurSubGraph getPreviousValueDurations(String key, String baseValue, PLog pLog) {
        return getArcValueDurations(key, baseValue, "previous", pLog);
    }

    public DurSubGraph getArcValueDurations(String key, String baseValue, String direction, APMLog log) {
        if (!eventAttributeOccurMap.containsKey(key)) return null;
        if (!eventAttributeOccurMap.get(key).containsKey(baseValue)) return null;

        DurSubGraph durSubGraph = new DurSubGraph();

        for (ATrace trace : log.getTraceList()) {
            int pTraceIndex = trace.getMutableIndex();

            List<AActivity> activityList = trace.getActivityList();
            for (AActivity iAct : activityList) {
                UnifiedMap<String, String> iAttr = iAct.getAllAttributes();
                if (iAttr.containsKey(key) && iAttr.get(key).equals(baseValue)) {
                    int iActIndex = iAct.getMutableIndex();

                    int dActIndex = direction.equals("next") ? iActIndex + 1 : iActIndex - 1;

                    if (dActIndex >= 0 && dActIndex < activityList.size()) {
                        AActivity dAct = activityList.get(dActIndex);
                        UnifiedMap<String, String> dAttr = dAct.getAllAttributes();

                        if (iAttr.containsKey(key) && dAttr.containsKey(key)) {
                            String dVal = dAttr.get(key);

                            long et = direction.equals("next") ? iAct.getEndTimeMilli() : dAct.getEndTimeMilli();
                            long st = direction.equals("next") ? dAct.getStartTimeMilli() : iAct.getStartTimeMilli();
                            long dur = st > et ? st - et : 0;
                            durSubGraph.addDuration(dVal, dur, pTraceIndex);
                        }
                    }
                }
            }
        }

        return durSubGraph;
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

        DurSubGraph subGraph = getNextValueDurations(attributeKey, indegree, theLog);

        UnifiedMap<String, UnifiedMap<Double, UnifiedSet<Integer>>> valDurCaseMap = subGraph.getValDurCaseIndexMap();

        UnifiedSet<Double> set = new UnifiedSet<>();

        if (valDurCaseMap.containsKey(outdegree)) {
            UnifiedMap<Double, UnifiedSet<Integer>> durCaseMap = valDurCaseMap.get(outdegree);
            set = new UnifiedSet<>(durCaseMap.keySet());
        }
        return set;
    }

}
