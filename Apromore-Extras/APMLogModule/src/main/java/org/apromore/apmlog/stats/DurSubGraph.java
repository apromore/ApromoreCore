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

import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

public class DurSubGraph {
    private UnifiedMap<String, UnifiedMap<Double, UnifiedSet<Integer>>> valDurCaseIndexMap;
    private UnifiedMap<String, UnifiedSet<Integer>> valCaseOccurMap;
    private UnifiedMap<String, Integer> valTtlFreqMap;
    private UnifiedMap<String, IntIntPair> valFrequenciesMap;

    public DurSubGraph() {
        valDurCaseIndexMap = new UnifiedMap<>();
        valFrequenciesMap = new UnifiedMap<>();
        valCaseOccurMap = new UnifiedMap<>();
        valTtlFreqMap = new UnifiedMap<>();
    }

    public void addDuration(String value, double duration, int traceIndex) {
        if (valDurCaseIndexMap.containsKey(value)) {
            UnifiedMap<Double, UnifiedSet<Integer>> durCaseMap = valDurCaseIndexMap.get(value);
            if (durCaseMap.containsKey(duration)) {
                UnifiedSet<Integer> caseIdxSet = durCaseMap.get(duration);
                if (!caseIdxSet.contains(traceIndex)) caseIdxSet.add(traceIndex);
            } else {
                UnifiedSet<Integer> caseIdxSet = new UnifiedSet<>();
                caseIdxSet.add(traceIndex);
                durCaseMap.put(duration, caseIdxSet);
            }
        } else {
            UnifiedSet<Integer> caseIdxSet = new UnifiedSet<>();
            caseIdxSet.add(traceIndex);
            UnifiedMap<Double, UnifiedSet<Integer>> durCaseMap = new UnifiedMap<>();
            durCaseMap.put(duration, caseIdxSet);
            valDurCaseIndexMap.put(value, durCaseMap);
        }
        if (valTtlFreqMap.containsKey(value)) {
            int freq = valTtlFreqMap.get(value) + 1;
            valTtlFreqMap.put(value, freq);
        } else valTtlFreqMap.put(value, 1);

        if (valCaseOccurMap.containsKey(value)) {
            if (!valCaseOccurMap.get(value).contains(traceIndex)) valCaseOccurMap.get(value).add(traceIndex);
        } else {
            UnifiedSet<Integer> traceIdxSet = new UnifiedSet<>();
            traceIdxSet.add(traceIndex);
            valCaseOccurMap.put(value, traceIdxSet);
        }
        int caseSize = valCaseOccurMap.get(value).size();
        int ttlSize = valTtlFreqMap.get(value);

        IntIntPair iip = PrimitiveTuples.pair(caseSize,ttlSize);

        valFrequenciesMap.put(value, iip);
    }

    public UnifiedMap<String, IntIntPair> getValFrequenciesMap() {
        return valFrequenciesMap;
    }

    public UnifiedMap<String, UnifiedMap<Double, UnifiedSet<Integer>>> getValDurCaseIndexMap() {
        return valDurCaseIndexMap;
    }

    public UnifiedMap<String, Integer> getValTtlFreqMap() {
        return valTtlFreqMap;
    }

    public UnifiedMap<String, UnifiedSet<Integer>> getValCaseOccurMap() {
        return valCaseOccurMap;
    }
}
