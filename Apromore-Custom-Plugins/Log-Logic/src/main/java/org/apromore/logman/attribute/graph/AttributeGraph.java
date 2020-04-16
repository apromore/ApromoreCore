/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.logman.attribute.graph;

import java.util.BitSet;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.set.primitive.IntSet;

public interface AttributeGraph {
    public AttributeLogGraph getOriginalGraph();
    public BitSet cloneNodeBitMask();
    public BitSet cloneArcBitMask();
    public void markRemoveNode(int node);
    public void markRemoveArc(int node);    
    public void markAddArc(int node);
    public IntSet getNodes();
    public boolean containNode(int node);    
    public int getSourceNode();    
    public int getSinkNode();
    public String getNodeName(int node);
    public IntSet getIncomingArcs(int node);    
    public IntSet getOutgoingArcs(int node);    
    public IntSet getIncomingArcsWithoutSelfLoops(int node);
    public IntSet getOutgoingArcsWithoutSelfLoops(int node);
    public IntList getSortedArcs();
    public IntList getSortedArcsWithoutSelfLoops();
//    public IntList getIncomingSortedArcsWithoutSelfLoops(int node);
//    public IntList getOutgoingSortedArcsWithoutSelfLoops(int node);    
    public int getArc(int source, int target) throws InvalidArcException;    
    public int getSource(int arc);    
    public int getTarget(int arc);    
    public IntSet getArcs();    
    public double getNodeWeight(int node, MeasureType type, MeasureAggregation aggregation);    
    public double getArcWeight(int arc, MeasureType type, MeasureAggregation aggregation);
    public boolean isPerfectSequence();
    public ListIterable<AttributeGraph> getSubGraphs();
}
