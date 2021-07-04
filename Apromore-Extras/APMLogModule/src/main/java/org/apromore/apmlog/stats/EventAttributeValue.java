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

import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EventAttributeValue implements AttributeValue, Serializable {
    private String value;
    private long total;
    private long totalCases;
    private UnifiedSet<ActivityInstance> occurActivities;

    // ====================================================================
    // Avoid accessing this value directly. Use getOccurCasesIndexSet()
    // ====================================================================
    private Set<Integer> occurCasesIndexSet;

    public EventAttributeValue(String value, UnifiedSet<ActivityInstance> occurActivities, long totalCasesOfLog) {
        this.value = value;
        this.occurActivities = occurActivities;
        this.totalCases = totalCasesOfLog;

        // =========================================================================
        // Doing operations here will cause performance problem. Avoid it.
        // =========================================================================
    }


    public Set<Integer> getOccurCasesIndexSet() {
        // ==================
        // Late binding.
        // ==================
        if (occurCasesIndexSet == null) {
            occurCasesIndexSet = occurActivities.stream()
                    .map(ActivityInstance::getImmutableTraceIndex)
                    .collect(Collectors.toSet());
        }
        return occurCasesIndexSet;
    }

    public String getValue() {
        return value;
    }

    public long getCases() {
        return getOccurCasesIndexSet().size();
    }

    public long getCases(BitSet validCaseIndexes) {
        return getOccurCasesIndexSet().stream().filter(x -> validCaseIndexes.get(x)).collect(Collectors.toSet()).size();
    }

    public String getFrequency() {
        return String.format("%.2f", getPercent() );
    }

    public long getTotal() {
        if (total < 1) {
            total = occurActivities.stream().collect(Collectors.summingLong(ActivityInstance::getEventSize));
        }
        return total;

    }

    public double getRatio() {
        return getOccurCasesIndexSet().size() / (double) totalCases;
    }

    public double getOppCases() {
        return totalCases - getOccurCasesIndexSet().size();
    }

    public double getPercent() {
        return 100 * ((double) getOccurCasesIndexSet().size() / totalCases);
    }

    public UnifiedSet<ActivityInstance> getOccurActivities(UnifiedSet<ActivityInstance> validActivities) {
        return occurActivities.stream().filter(x -> validActivities.contains(x))
                .collect(Collectors.toCollection(UnifiedSet::new));
    }

    public int getActivitySize(UnifiedSet<ActivityInstance> validActivities) {
        return occurActivities.stream().filter(x -> validActivities.contains(x)).collect(Collectors.toList()).size();
    }

    public double getTotalDuration(UnifiedSet<ActivityInstance> validActivities) {
        return getAllDurations(validActivities).sum();
    }

    public double getMinDuration(UnifiedSet<ActivityInstance> validActivities) {
        return getAllDurations(validActivities).min();
    }

    public double getMedianDuration(UnifiedSet<ActivityInstance> validActivities) {
        return getAllDurations(validActivities).median();
    }

    public double getAverageDuration(UnifiedSet<ActivityInstance> validActivities) {
        return getAllDurations(validActivities).average();
    }

    public double getMaxDuration(UnifiedSet<ActivityInstance> validActivities) {
        return getAllDurations(validActivities).max();
    }

    public Set<Double> getUniqueDurations(UnifiedSet<ActivityInstance> validActivities) {
        return occurActivities.stream()
                .filter(x -> validActivities.contains(x))
                .map(s -> s.getDuration()).collect(Collectors.toSet());
    }

    public DoubleArrayList getAllDurations(UnifiedSet<ActivityInstance> validActivities) {
        double[] array = occurActivities.stream()
                .filter(x -> validActivities.contains(x))
                .mapToDouble(s -> s.getDuration()).toArray();
        return new DoubleArrayList(array);
    }

    public double getInterCasesDoubleValue(BitSet validCaseIndexes) {
        return getValueInDouble() * getCases(validCaseIndexes);
    }

    public double getInterCasesDoubleValue() {
        return getValueInDouble() * getCases();
    }

    @Override
    public double getValueInDouble() {
        if (!Util.isNumeric(value)) return -1;
        else return Double.valueOf(value);
    }

    public EventAttributeValue clone() {
        return new EventAttributeValue(
                value, new UnifiedSet<>(occurActivities), totalCases );
    }
}
