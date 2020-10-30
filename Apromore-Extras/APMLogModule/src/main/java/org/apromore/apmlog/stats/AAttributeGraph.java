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
<<<<<<< HEAD
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.List;
=======
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

>>>>>>> development
import java.util.Set;

public class AAttributeGraph {

<<<<<<< HEAD
//    private UnifiedMap<String, AAttribute> map = new UnifiedMap<>();
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
                long actDur = activity.getDuration();
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
            int actIndex = act.getImmutableIndex();
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
                    } else {
                        //System.out.println("");
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




=======
    private UnifiedMap<String, AAttribute> map = new UnifiedMap<>();

    public AAttributeGraph() {}

    public void setMap(UnifiedMap<String, AAttribute> map) {
        this.map = map;
    }

    public void put(String key, AAttribute aAttribute) {
        map.put(key, aAttribute);
    }

    public void put(String key, String value) {
        AAttribute aAttribute = new AAttribute(key, value);
        map.put(key, aAttribute);
    }


    public void add(String key, String value, String index, double duration) {
        if (map.containsKey(key)) {
            AAttribute aAttribute = map.get(key);
            if (aAttribute.contains(value)) {
                aAttribute.get(value).addOccurrence(index, duration);
            } else {
                AAttributeValue aAttributeValue = new AAttributeValue(value);
                aAttributeValue.addOccurrence(index, duration);
                aAttribute.add(aAttributeValue);
            }
        } else {
            AAttribute aAttribute = new AAttribute(key);
            AAttributeValue aAttributeValue = new AAttributeValue(value);
            aAttributeValue.addOccurrence(index, duration);
            aAttribute.add(aAttributeValue);
            map.put(key, aAttribute);
        }
    }

    public boolean addNext(String key, String value, String nextValue,
                           String indexPairOfNext) {
        if (map.containsKey(key)) {
            AAttribute aAttribute = map.get(key);
            if (aAttribute.contains(value)) {
                AAttributeValue aAttributeValue = aAttribute.get(value);
                aAttributeValue.addToNext(nextValue, indexPairOfNext);

                return true;
            }
        }
        return false;
    }

    public boolean addPrevious(String key, String value, String previousValue,
                               String indexPairOfPrevious) {
        if (map.containsKey(key)) {
            AAttribute aAttribute = map.get(key);
            if (aAttribute.contains(value)) {
                AAttributeValue aAttributeValue = aAttribute.get(value);
                aAttributeValue.addToPrevious(previousValue, indexPairOfPrevious);

                return true;
            }
        }
        return false;
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public boolean contains(String key, String value) {
        if (!map.containsKey(key)) return false;

        return map.get(key).contains(value);
    }

    public Set<String> getKeys() {
        return map.keySet();
    }

    public AAttribute getAttribute(String key) {
        return map.get(key);
    }

    public AAttributeValue getAttributeValue(String key, String value) {
        return map.get(key).get(value);
    }

    public UnifiedSet<Double> getDurations(String attributeKey, String indegree, String outdegree, APMLog apmLog) {

        UnifiedSet<Double> durSet = new UnifiedSet<>();

        AAttribute aAttribute = map.get(attributeKey);

        AAttributeValue aAttributeValue = aAttribute.get(indegree);

        UnifiedSet<String> indexPairSet = aAttributeValue.getNextValues().get(outdegree);

        for (String taip : indexPairSet) {
            String index1 = taip.substring(0, taip.indexOf(">"));
            String index2 = taip.substring(taip.indexOf(">") + 1);
            String traceIndexStr = index1.substring(0, index1.indexOf(":"));
            String actIndex1Str = index1.substring(index1.indexOf(":") + 1);
            String actIndex2Str = index2.substring(index2.indexOf(":") + 1);

            int traceIndex = Integer.valueOf(traceIndexStr);
            int actIndex1 = Integer.valueOf(actIndex1Str);
            int actIndex2 = Integer.valueOf(actIndex2Str);
            AActivity activity1 = apmLog.getTraceList().get(traceIndex).getActivityList().get(actIndex1);
            AActivity activity2 = apmLog.getTraceList().get(traceIndex).getActivityList().get(actIndex2);
            long act1ET = activity1.getEndTimeMilli();
            long act2ST = activity2.getStartTimeMilli();
            double duration = act2ST > act1ET ? act2ST - act1ET : 0;

            if (!durSet.contains(duration)) durSet.put(duration);
        }
        return durSet;
    }

    public AAttributeGraph clone() {
        AAttributeGraph aag = new AAttributeGraph();
        if (map.size() > 0) {
            UnifiedMap<String, AAttribute> mapClone = new UnifiedMap<>();
            for (String key : map.keySet()) {
                mapClone.put(key, map.get(key).clone());
            }
            aag.setMap(mapClone);
        }
        return aag;
    }
>>>>>>> development
}
