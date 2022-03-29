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

package org.apromore.logman.attribute;

import org.apromore.logman.Constants;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.primitive.IntInterval;

/**
 * AttributeMatrixGraph is the matrix-based graph based on an attribute.
 * The nodes are the value indexes of the attribute. In addition, there are also two artificial nodes representing
 * the source and the sink. These nodes are the next values of the index of the attribute. The source
 * is max_index+1 and the sink is max_index+2.
 * The arc is represented by the cell on a 2-dimensional matrix whose each dimension is the list of nodes from low
 * to high values. The dimension size is equal to the total number of nodes.
 * Each arc is an integer converted from its source and target.
 * 
 * @author Bruce Nguyen
 *
 */
public class AttributeMatrixGraph {
    private IndexableAttribute attribute;
    private int numberOfNodes;
    private int startIndex;
    private int endIndex;

    public AttributeMatrixGraph (IndexableAttribute attribute) {
        this.attribute = attribute;
        numberOfNodes = attribute.getValueSize()+2;
        startIndex = attribute.getArtificialStartIndex();
        endIndex = attribute.getArtificialEndIndex();
    }
    
    public IndexableAttribute getAttribute() {
        return this.attribute;
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
        return valueIndex;
    }
    
    public int getTarget(int arc) {
        return arc%numberOfNodes;
    }
    
    public int getSource(int arc) {
        return (arc/numberOfNodes);
    }    
    
    public int getArc(int source, int target) {
        return source*numberOfNodes + target;
    }
    
    public String getNodeName(int node) {
        if (isSource(node)) {
            return Constants.START_NAME;
        }
        else if (isSink(node)) {
            return Constants.END_NAME;
        }
        else {
            Object value = attribute.getValue(node);
            return (value == null ? "" : value.toString());
        }
    }
    
    public int getNodeFromName(String name) {
        if (name.equals(Constants.START_NAME)) {
            return getSource();
        }
        else if (name.equals(Constants.END_NAME)) {
            return getSink();
        }
        else {
            return attribute.getValueIndex(name);
        }
    }
}
