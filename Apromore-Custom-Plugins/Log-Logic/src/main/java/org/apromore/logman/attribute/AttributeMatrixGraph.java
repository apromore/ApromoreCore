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

package org.apromore.logman.attribute;

import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.primitive.IntInterval;

// The nodes are the value indexes of the attribute. In addition, there are also two artificial nodes representing
// the source and the sink. These nodes are the next values of the index of the attribute. The source
// is max_index+1 and the sink is max_index+2.
// The arc is represented by the cell on a 2-dimensional matrix whose each dimension is the list of nodes from low
// to high values. The dimension size is equal to the total number of nodes.
// Each arc is an integer converted from its source and target.
public class AttributeMatrixGraph {
    private int numberOfNodes;
    private int startIndex;
    private int endIndex;

    public AttributeMatrixGraph (IndexableAttribute attribute) {
        numberOfNodes = attribute.getValueSize()+2;
        startIndex = attribute.getArtificialStartIndex();
        endIndex = attribute.getArtificialEndIndex();
    }
    
    public IntList getNodes() {
        return IntInterval.fromTo(0, numberOfNodes-1).toImmutable();
    }
    
    public int getDimension() {
        return numberOfNodes;
    }
    
    public int getSource() {
        return numberOfNodes-2;
    }
    
    public int getSink() {
        return numberOfNodes-1;
    }
    
    public boolean isSource(int valueIndex) {
        return (valueIndex == startIndex);
    }
    
    public boolean isSink(int valueIndex) {
        return (valueIndex == endIndex);
    }
    
    public int getNodeFromValueIndex(int valueIndex) {
//        if (isSource(valueIndex)) {
//            return getSource();
//        }
//        else if (isSink(valueIndex)) {
//            return getSink();
//        }
//        else {
//            return valueIndex;
//        }
        return valueIndex;
    }
    
    public int getTarget(int arc) {
        return arc%numberOfNodes;
    }
    
    public int getSource(int arc) {
//        return (arc - arc%numberOfNodes)/numberOfNodes;
        return (arc/numberOfNodes);
    }    
    
    public int getArc(int source, int target) {
        return source*numberOfNodes + target;
    }
}
