/*
 * Copyright © 2019 The University of Melbourne.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.processdiscoverer.impl.util;

import com.raffaeleconforti.statistics.StatisticsSelector;

import org.apromore.plugin.processdiscoverer.impl.Arc;
import org.apromore.plugin.processdiscoverer.impl.VisualizationAggregation;
import org.eclipse.collections.impl.block.factory.primitive.DoublePredicates;
import org.eclipse.collections.impl.block.factory.primitive.LongPredicates;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import static org.apromore.plugin.processdiscoverer.impl.VisualizationAggregation.*;

import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class FrequencySetPopulator {

    public static double getAggregateInformation(LongArrayList list, VisualizationAggregation aggregation) {
        Double result = -Double.MAX_VALUE;
        if(aggregation == TOTAL) result = (double) list.sum();
        else if(aggregation == CASES) result = (double) list.count(LongPredicates.greaterThan(0));
        else if(aggregation == MAX) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MAX, null, list.toArray());
        else if(aggregation == MIN) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MIN, null, list.toArray());
        else if(aggregation == MEAN) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MEAN, null, list.toArray());
        else if(aggregation == MEDIAN) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MEDIAN, null, list.toArray());
        else if(aggregation == MODE) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MODE, null, list.toArray());
        return result;
    }

    public static LongArrayList retreiveEntryLong(Map<Arc, LongArrayList> set, Arc key, int length) {
        LongArrayList list;
        if((list = set.get(key)) == null) {
            list = createEntryLong(length);
            set.put(key, list);
        }
        return list;
    }

    public static DoubleArrayList retreiveEntryDouble(Map<Arc, DoubleArrayList> set, Arc key, int length) {
        DoubleArrayList list;
        if((list = set.get(key)) == null) {
            list = createEntryDouble(length);
            set.put(key, list);
        }
        return list;
    }

    // Retrieve the value list associated with <key> in the collection <set>
    // Create an empty value list of length <length> if no value list found in <set> associated with <key> 
    public static LongArrayList retreiveEntry(IntObjectHashMap<LongArrayList> set, int key, int length) {
        LongArrayList list;
        if((list = set.get(key)) == null) {
            list = createEntryLong(length);
            set.put(key, list);
        }
        return list;
    }

    private static LongArrayList createEntryLong(int length) {
        LongArrayList list = new LongArrayList(length);
        for(int i = 0; i < length; i++) list.add(0);
        return list;
    }

    private static DoubleArrayList createEntryDouble(int length) {
        DoubleArrayList list = new DoubleArrayList(length);
        for(int i = 0; i < length; i++) list.add(0);
        return list;
    }

}
