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

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public class AAttributeValue {
    private String stringValue;

    /**
     * String format = traceId:activityId. for example: 1:2
     */
    UnifiedSet<String> occurrences = new UnifiedSet<>();

    /**
     * Set's String format = traceId:activityId>traceId:activityId. example: 0:1>0:2
     */
    UnifiedMap<String, UnifiedSet<String>> nextValues = new UnifiedMap<>();
    UnifiedMap<String, UnifiedSet<String>> previousValues = new UnifiedMap<>();

    /**
     * Map < duration, Set < traceId:activityId > >.
     *
     */
    UnifiedMap<Double, UnifiedSet<String>> durationsIndexMap = new UnifiedMap<>();

    public AAttributeValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void setOccurrences(UnifiedSet<String> occurrences) {
        this.occurrences = occurrences;
    }

    public void setNextValues(UnifiedMap<String, UnifiedSet<String>> nextValues) {
        this.nextValues = nextValues;
    }

    public void setPreviousValues(UnifiedMap<String, UnifiedSet<String>> previousValues) {
        this.previousValues = previousValues;
    }

    public UnifiedMap<Double, UnifiedSet<String>> getDurationsIndexMap() {
        return durationsIndexMap;
    }

    public String getStringValue() {
        return stringValue;
    }

    public UnifiedSet<String> getOccurrences() {
        return occurrences;
    }

    public UnifiedMap<String, UnifiedSet<String>> getNextValues() {
        return nextValues;
    }

    public UnifiedMap<String, UnifiedSet<String>> getPreviousValues() {
        return previousValues;
    }

    public void addOccurrence(String indexPair, double duration) {
        this.occurrences.put(indexPair);
        if (durationsIndexMap.containsKey(duration)) {
            durationsIndexMap.get(duration).put(indexPair);
        } else {
            UnifiedSet<String> indexPairSet = new UnifiedSet<>();
            indexPairSet.put(indexPair);
            durationsIndexMap.put(duration, indexPairSet);
        }
    }

    public boolean containsNextValue(String nextValue) {
        return nextValues.containsKey(nextValue);
    }

    public boolean containsPreviousValue(String previousValue) {
        return previousValues.containsKey(previousValue);
    }

    public void addToNext(String value, String indexPair) {
        if (nextValues.containsKey(value)) nextValues.get(value).put(indexPair);
        else {
            UnifiedSet<String> indexPairs = new UnifiedSet<>();
            indexPairs.add(indexPair);
            nextValues.put(value, indexPairs);
        }
    }

    public void addToPrevious(String value, String indexPair) {
        if (previousValues.containsKey(value)) previousValues.get(value).put(indexPair);
        else {
            UnifiedSet<String> indexPairs = new UnifiedSet<>();
            indexPairs.add(indexPair);
            previousValues.put(value, indexPairs);
        }
    }

    public AAttributeValue clone() {
        AAttributeValue aav = new AAttributeValue(stringValue);
//        UnifiedSet<TraceActivityIndex> occurrences = new UnifiedSet<>();
//        UnifiedMap<String, UnifiedSet<TraceActivityIndexPair>> nextValues = new UnifiedMap<>();
//        UnifiedMap<String, UnifiedSet<TraceActivityIndexPair>> previousValues = new UnifiedMap<>();

        if (occurrences.size() > 0) {
            UnifiedSet<String> occurrencesClone = new UnifiedSet<>();
            for (String tai : occurrences) {
                occurrencesClone.add(tai);
            }
            aav.setOccurrences(occurrencesClone);
        }


        if (nextValues.size() > 0) {
            UnifiedMap<String, UnifiedSet<String>> nextValuesClone = new UnifiedMap<>();
            for (String val : nextValues.keySet()) {
                UnifiedSet<String> taipSetClone = new UnifiedSet<>();
                UnifiedSet<String> taipSet = nextValues.get(val);
                for (String taip : taipSet) {
                    taipSetClone.add(taip);
                }
                nextValuesClone.put(val, taipSetClone);
            }
            aav.setNextValues(nextValuesClone);
        }

        if (previousValues.size() > 0) {
            UnifiedMap<String, UnifiedSet<String>> previousValuesClone = new UnifiedMap<>();
            for (String val : previousValues.keySet()) {
                UnifiedSet<String> taipSetClone = new UnifiedSet<>();
                UnifiedSet<String> taipSet = previousValues.get(val);
                for (String taip : taipSet) {
                    taipSetClone.add(taip);
                }
                previousValuesClone.put(val, taipSetClone);
            }
            aav.setNextValues(previousValuesClone);
        }

        return aav;

    }
}
