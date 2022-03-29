/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.logman.attribute.log.variants;

import java.util.Arrays;
import java.util.Comparator;

import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;

/**
 * AttributeTraceVariants contain a collection of unique value traces, each is a variant.
 * Each variant has a frequency indicating the number of AttributeTraces having that value trace in the AttributeLog.
 * Each variant also contains a set of indexes of its corresponding AttributeTrace.
 * Variants can be sorted by their frequency. Their position after sorting is called a rank.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeTraceVariants {
    private AttributeLog attLog;
    private boolean isOriginal = false; // true if this variant set is for original data
    
    private MutableList<IntList> variants = Lists.mutable.empty(); //list of variants, element at an index i is a variant
    private MutableObjectIntMap<IntList> variantIndexMap = ObjectIntMaps.mutable.empty(); // boost the lookup of variant index
    
    // attributes of variants using variant indexes
    private MutableList<MutableList<AttributeTrace>> traces = Lists.mutable.empty(); // set of traces of the variant at index i
    private MutableIntList ranks = IntLists.mutable.empty(); // rank of the variant at an index i (high frequency first)
    private MutableIntIntMap indexToRank = IntIntMaps.mutable.empty(); // variant index mapped to its rank
    private MutableIntList frequencies = IntLists.mutable.empty(); // frequency (count) of the variant at index i
    
    public AttributeTraceVariants(AttributeLog attLog, boolean isOriginal) {
        this.attLog = attLog;
        this.isOriginal = isOriginal;
    }
    
    public void reset() {
        variants.clear();
        variantIndexMap.clear();
        traces.clear();
        frequencies.clear();
        indexToRank.clear();
        ranks.clear();
    }    
    
    public int size() {
        return variants.size();
    }
    
    public ListIterable<AttributeTrace> getTraces(IntList variant) {
        int index = getIndexOf(variant);
        return (index < 0) ? Lists.immutable.empty() : traces.get(index);
    }
    
    public int getIndexOf(IntList variant) {
        return variantIndexMap.getIfAbsent(variant, -1);
    }
    
    public int getRankOf(IntList variant) {
        return indexToRank.get(getIndexOf(variant));
    }    
    
    public int add(AttributeTrace trace) {
        trace.setVariants(this);
        int variantIndex;
        IntList valueTrace = isOriginal ? trace.getOriginalValueTrace() : trace.getValueTrace();
        if (!variantIndexMap.containsKey(valueTrace)) {
            variants.add(valueTrace);
            traces.add(Lists.mutable.empty());
            variantIndex = variants.size()-1;
            variantIndexMap.put(valueTrace, variantIndex);
            frequencies.add(1);
        }
        else {
            variantIndex = variantIndexMap.get(valueTrace);
            frequencies.set(variantIndex, frequencies.get(variantIndex) + 1);
        }
        traces.get(variantIndex).add(trace);
        
        return variantIndex;
    }
    
    public void sortVariantsByFrequency() {
        Integer[] variantIndexes = new Integer[variants.size()];
        for (int i=0;i<variantIndexes.length;i++) {
            variantIndexes[i] = i;
        }
        Arrays.sort(variantIndexes, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(frequencies.get(o1), frequencies.get(o2));
            };
        });
        
        int[] indexes = Arrays.stream(variantIndexes).mapToInt(Integer::intValue).toArray();
        ranks = IntLists.mutable.of(indexes).reverseThis();
        for (int i=0;i<ranks.size();i++) {
            indexToRank.put(ranks.get(i), i);
        }
    }
    
    // Note that the variants are formed based on start and complete events only
    // if the log has both start and complete events.
    public ListIterable<IntList> getVariants() {
        return variants;
    } 
    
    public ListIterable<IntList> getVariantsByRanks() {
        MutableList<IntList> orderedVariants = Lists.mutable.empty();
        for (int i: ranks.toArray()) {
            orderedVariants.add(variants.get(i));
        }
        return orderedVariants;
    }
    
    public IntList getVariantAtIndex(int variantIndex) {
        return variants.get(variantIndex).toImmutable();
    }
    
    public IntList getVariantAtRank(int rank) {
        return this.getVariantAtIndex(ranks.get(rank));
    }
    
    // Number of traces of the variant
    public int getFrequency(IntList variant) {
        return frequencies.get(getIndexOf(variant));
    }
    
    public double getVariantRelativeFrequency(IntList variant) {
        if (attLog.getTraces().size() == 0) return 0;
        return 1.0*this.getFrequency(variant)/attLog.getTraces().size();
    }

}
