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

    public AAttributeGraph(APMLog apmLog) {
        this.apmLog = apmLog;
    }

    // =========================================
    // Used by PD
    // =========================================
    public static DurSubGraph getValueDurations(String key, APMLog apmLog) {
        UnifiedMap<String, UnifiedSet<EventAttributeValue>> eavMap = apmLog.getEventAttributeValues();
        if (!eavMap.containsKey(key)) return null;

        DurSubGraph durSubGraph = new DurSubGraph();

        for (EventAttributeValue eav : eavMap.get(key)) {
            UnifiedSet<AActivity> activities = eav.getOccurActivities();
            for (AActivity activity : activities) {
                double actDur = activity.getDuration();
                int traceIndex = activity.getMutableTraceIndex();
                durSubGraph.addDuration(eav.getValue(), actDur, traceIndex);
            }
        }

        return durSubGraph;
    }

    public static DurSubGraph getNextValueDurations(String key, String baseValue, APMLog log) {
        return getArcValueDurations(key, baseValue, "next", log);
    }

    public static DurSubGraph getArcValueDurations(String key, String baseValue, String direction, APMLog log) {
        UnifiedMap<String, UnifiedSet<EventAttributeValue>> eavMap = log.getEventAttributeValues();

        if (!eavMap.containsKey(key)) return null;

        EventAttributeValue eav = eavMap.get(key).stream()
                .filter(x -> x.getValue().equals(baseValue))
                .findFirst()
                .orElse(null);

        if (eav == null) return null;

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
    public static UnifiedSet<Double> getDurations(String attributeKey, String indegree, String outdegree, APMLog theLog) {

        DurSubGraph subGraph = getNextValueDurations(attributeKey, indegree, theLog);

        if (subGraph == null) return null;

        UnifiedMap<String, UnifiedMap<Double, UnifiedSet<Integer>>> valDurCaseMap = subGraph.getValDurCaseIndexMap();

        UnifiedSet<Double> set = new UnifiedSet<>();

        if (valDurCaseMap.containsKey(outdegree)) {
            UnifiedMap<Double, UnifiedSet<Integer>> durCaseMap = valDurCaseMap.get(outdegree);
            set = new UnifiedSet<>(durCaseMap.keySet());
        }
        return set;
    }

}
