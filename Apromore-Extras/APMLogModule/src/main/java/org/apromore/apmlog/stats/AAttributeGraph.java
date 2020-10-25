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
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Set;

public class AAttributeGraph {

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
}
