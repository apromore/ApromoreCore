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

package org.apromore.logman.attribute.log.relation;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

public class EventuallyFollowReader implements RelationReader {
    @Override
    public ListIterable<IntIntPair> read(IntList trace) {
        MutableList<IntIntPair> pairs = Lists.mutable.empty();
        for (int i=0; i<(trace.size()-1); i++) {
            for (int j=i+1; j<trace.size(); j++) {
                pairs.add(PrimitiveTuples.pair(trace.get(i), trace.get(j)));
            }
        }
        return pairs;
    }
    
    @Override
    public ListIterable<IntIntPair> readIndexes(IntList trace) {
        MutableList<IntIntPair> pairs = Lists.mutable.empty();
        for (int i=0; i<(trace.size()-1); i++) {
            for (int j=i+1; j<trace.size(); j++) {
                pairs.add(PrimitiveTuples.pair(i, j));
            }
        }
        return pairs;
    }
    
    @Override
    public SetIterable<IntIntPair> readDistinct(IntList trace) {
        MutableSet<IntIntPair> pairs = Sets.mutable.empty();
        for (int i=0; i<(trace.size()-1); i++) {
            for (int j=i+1; j<trace.size(); j++) {
                pairs.add(PrimitiveTuples.pair(trace.get(i), trace.get(j)));
            }
        }
        return pairs;
    }
    
    @Override
    public SetIterable<IntIntPair> readDistinctIndexes(IntList trace) {
        MutableSet<IntIntPair> pairs = Sets.mutable.empty();
        for (int i=0; i<(trace.size()-1); i++) {
            for (int j=i+1; j<trace.size(); j++) {
                pairs.add(PrimitiveTuples.pair(i, j));
            }
        }
        return pairs;
    }

}
