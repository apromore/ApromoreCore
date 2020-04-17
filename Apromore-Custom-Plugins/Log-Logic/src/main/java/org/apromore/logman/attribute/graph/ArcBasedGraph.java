/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.logman.attribute.graph;

import java.util.BitSet;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.factory.Lists;

public class ArcBasedGraph extends AbstractAttributeGraph {
    public ArcBasedGraph(AttributeLogGraph originalGraph, BitSet nodeBitMask, BitSet arcBitMask) {
        super(originalGraph, nodeBitMask, arcBitMask);
    }
    
    @Override
    public ListIterable<AttributeGraph> getSubGraphs() {
        return Lists.immutable.empty();
    }
    
    
    public boolean isOutgoingConnected(int node) {
        return !getOutgoingArcsWithoutSelfLoops(node).isEmpty();
    }
    
    public boolean isIncomingConnected(int node) {
        return !getIncomingArcsWithoutSelfLoops(node).isEmpty();
    }
    
}
