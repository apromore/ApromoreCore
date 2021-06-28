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

import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseAttributeValue implements AttributeValue {
    private String value;

    // the percentage of cases
    private String frequency;
    private double percent;

    // ratio is: the cases of this value / the max cases among all attribute values
    private double ratio;

    // for default sorting
    private double oppCases;

    private IntArrayList occurCaseIndexes;
    private UnifiedSet<Integer> occurCaseIndexSet;

    private long totalCases;

    public CaseAttributeValue(String value, IntArrayList occurCaseIndexes, long totalCases) {
        this.value = value.intern();
        this.occurCaseIndexes = occurCaseIndexes;
        this.percent = 100 * ((double) occurCaseIndexes.size() / totalCases);
        this.frequency = String.format("%.2f",  percent );
        this.totalCases = totalCases;

        this.oppCases = totalCases - occurCaseIndexes.size();

        occurCaseIndexSet = Arrays.stream(occurCaseIndexes.toArray()).boxed().collect(Collectors.toCollection(UnifiedSet::new));
    }

    public CaseAttributeValue(String value, UnifiedSet<Integer> occurCaseIndexSet, long totalCases) {
        this.value = value.intern();
        this.occurCaseIndexSet = occurCaseIndexSet;
        this.percent = 100 * ((double) occurCaseIndexSet.size() / totalCases);
        this.frequency = String.format("%.2f",  percent );
        this.totalCases = totalCases;

        this.oppCases = totalCases - occurCaseIndexSet.size();
    }

    public Set<Integer> getOccurCasesIndexSet() {
        return occurCaseIndexSet;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public String getValue() {
        return value;
    }

    public long getCases() {
        return getOccurCasesIndexSet().size();
    }

    public String getFrequency() {
        return String.format("%.2f",  getPercent() );
    }

    @Override
    public long getTotal() {
        return getCases();
    }

    public double getRatio() {
        return ratio;
    }

    public double getOppCases() {
        return oppCases;
    }

    public double getPercent() {
        return 100 * ((double) getOccurCasesIndexSet().size() / totalCases);
    }

    @Override
    public double getValueInDouble() {
        if (!Util.isNumeric(value)) return -1;
        else return Double.valueOf(value);
    }

    @Override
    public IntArrayList getOccurCaseIndexes() {
        return new IntArrayList(getOccurCasesIndexSet().stream().mapToInt(x->x).toArray());
    }

    public CaseAttributeValue clone() {
        IntArrayList occurCaseIndexesClone = new IntArrayList(occurCaseIndexes.size());
        for (int i = 0; i < occurCaseIndexes.size(); i++) {
            occurCaseIndexesClone.add(occurCaseIndexes.get(i));
        }
        return new CaseAttributeValue(value, occurCaseIndexesClone, totalCases);
    }
}
