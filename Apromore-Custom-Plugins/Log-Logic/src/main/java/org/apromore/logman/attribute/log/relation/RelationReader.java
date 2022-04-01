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

package org.apromore.logman.attribute.log.relation;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.set.SetIterable;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;

public interface RelationReader {
    // Each element in a pair is an element in the trace
    public ListIterable<IntIntPair> read(IntList trace);
    
    // Each element in a pair is an index of an element in the trace
    public ListIterable<IntIntPair> readIndexes(IntList trace);
    
    // Same as read(trace), but returns distinct pairs
    public SetIterable<IntIntPair> readDistinct(IntList trace);
    
    // Same as readIndexes(trace), but returns distinct pairs
    public SetIterable<IntIntPair> readDistinctIndexes(IntList trace);
    
}
