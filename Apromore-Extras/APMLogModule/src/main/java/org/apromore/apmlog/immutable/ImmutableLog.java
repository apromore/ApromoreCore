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
package org.apromore.apmlog.immutable;


import org.apromore.apmlog.*;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;
import java.util.List;

public class ImmutableLog extends LaLog {



    public ImmutableLog() {
        traceList = new ArrayList<>();
        immutableTraces = new ArrayList<>();
        eventAttributeOccurMap = new UnifiedMap<>();
        activityNameBiMap = new HashBiMap<>();
        caseAttributeValueFreqMap = new UnifiedMap<>();
        eventAttributeValueCasesFreqMap = new UnifiedMap<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        variantIdFreqMap = new UnifiedMap<>();
    }

    public ImmutableLog(List<ATrace> traceList) {
        this.traceList = traceList;
        this.immutableTraces = traceList;
        eventAttributeOccurMap = new UnifiedMap<>();
        activityNameBiMap = new HashBiMap<>();
        caseAttributeValueFreqMap = new UnifiedMap<>();
        eventAttributeValueCasesFreqMap = new UnifiedMap<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        variantIdFreqMap = new UnifiedMap<>();
    }


    @Override
    public APMLog clone() {

        List<ATrace> traceListForClone = new ArrayList<>();

        for (int i = 0; i < this.traceList.size(); i++) {
            ATrace aTrace = this.traceList.get(i).clone();
            traceListForClone.add(aTrace);
        }

        UnifiedMap<Integer, Integer> variIdFreqMapForClone = new UnifiedMap<>();

        for (int key : this.variantIdFreqMap.keySet()) {
            variIdFreqMapForClone.put(key, this.variantIdFreqMap.get(key));
        }


        UnifiedMap<String, UnifiedMap<String, Integer>> eventAttrValCasesFreqMapForClone = new UnifiedMap<>();

        for (String key : this.eventAttributeValueCasesFreqMap.keySet()) {
            UnifiedMap<String, Integer> valFreqMapForClone = new UnifiedMap<>();
            UnifiedMap<String, Integer> valFreqMap = this.eventAttributeValueCasesFreqMap.get(key);

            for (String val : valFreqMap.keySet()) {
                valFreqMapForClone.put(val, valFreqMap.get(val));
            }

            eventAttrValCasesFreqMapForClone.put(key, valFreqMapForClone);
        }

        UnifiedMap<String, UnifiedMap<String, Integer>> eventAttrValFreqMapForClone = new UnifiedMap<>();

        for (String key : this.eventAttributeValueFreqMap.keySet()) {
            UnifiedMap<String, Integer> valFreqMapForClone = new UnifiedMap<>();
            UnifiedMap<String, Integer> valFreqMap = this.eventAttributeValueFreqMap.get(key);

            for (String val : valFreqMap.keySet()) {
                valFreqMapForClone.put(val, valFreqMap.get(val));
            }

            eventAttrValFreqMapForClone.put(key, valFreqMapForClone);
        }

        UnifiedMap<String, UnifiedMap<String, Integer>> caseAttrValFreqMapForClone = new UnifiedMap<>();

        for (String key : this.caseAttributeValueFreqMap.keySet()) {
            UnifiedMap<String, Integer> valFreqMapForClone = new UnifiedMap<>();
            UnifiedMap<String, Integer> valFreqMap = this.caseAttributeValueFreqMap.get(key);

            for (String val : valFreqMap.keySet()) {
                valFreqMapForClone.put(val, valFreqMap.get(val));
            }

            caseAttrValFreqMapForClone.put(key, valFreqMapForClone);
        }

        UnifiedMap<String, Integer> activityMaxOccurMapForClone = new UnifiedMap<>();

        for (String key : this.activityMaxOccurMap.keySet()) {
            activityMaxOccurMapForClone.put(key, this.activityMaxOccurMap.get(key));
        }

        DoubleArrayList caseDurationListClone = new DoubleArrayList(caseDurationList.toArray());


        ImmutableLog logClone = new ImmutableLog(traceListForClone,
                variIdFreqMapForClone,
                eventAttrValCasesFreqMapForClone,
                eventAttrValFreqMapForClone,
                caseAttrValFreqMapForClone,
                caseDurationListClone,
                this.timeZone,
                this.startTime,
                this.endTime,
                this.eventSize,
                this.activityNameMapper,
                activityMaxOccurMapForClone);

        return logClone;
    }

    public ImmutableLog(List<ATrace> traceList,
                      UnifiedMap<Integer, Integer> variantIdFreqMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueCasesFreqMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> caseAttributeValueFreqMap,
                        DoubleArrayList caseDurationList,
                      String timeZone,
                      long startTime,
                      long endTime,
                      long eventSize,
                      ActivityNameMapper activityNameMapper,
                      UnifiedMap<String, Integer> activityMaxOccurMap) {
        this.immutableTraces = traceList;
        this.traceList = traceList;
        this.variantIdFreqMap = variantIdFreqMap;
        this.actIdNameMap = actIdNameMap;
        this.eventAttributeValueCasesFreqMap = eventAttributeValueCasesFreqMap;
        this.eventAttributeValueFreqMap = eventAttributeValueFreqMap;
        this.caseAttributeValueFreqMap = caseAttributeValueFreqMap;
        this.caseDurationList = caseDurationList;
        this.timeZone = timeZone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventSize = eventSize;
        this.activityNameMapper = activityNameMapper;
        this.activityMaxOccurMap = activityMaxOccurMap;
        if (traceList.size() > 0) {
            if (traceList.get(0).getDuration() > 0) {
                defaultChartDataCollection = new DefaultChartDataCollection(this);
            }
        }
    }
}
