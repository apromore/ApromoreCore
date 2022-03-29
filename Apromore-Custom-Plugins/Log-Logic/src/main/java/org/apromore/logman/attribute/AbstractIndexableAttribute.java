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

import org.apromore.logman.ATrace;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;
import org.eclipse.collections.impl.list.primitive.IntInterval;

/**
 * Provides common code for indexable attributes.
 * 
 * IndexableAttribute also provides a matrix-based graph whose nodes are its values
 * and arcs are the possible relation between nodes. It is a matrix with each row and colum
 * as a node (number of rows = number of columns), and cells representing the arc between
 * the corresponding row and column.
 * 
 * This matrix-based graph will be used by graphs created from a log based on this attribute.
 * For example, these graphs are created in ProcessDiscoverer. In this way, the graphs created
 * from AttributeTrace/AttributeLog are only subgraphs of this graph.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class AbstractIndexableAttribute extends AbstractAttribute implements IndexableAttribute {
	private AttributeMatrixGraph attGraph;
	protected MutableIntIntMap originalValueIndexCountMap = IntIntMaps.mutable.empty(); // index => occurrence count
	protected MutableIntIntMap activeValueIndexCountMap = IntIntMaps.mutable.empty(); // index => occurrence count
    
	public AbstractIndexableAttribute(String key, AttributeLevel level, AttributeType type) {
		super(key, level, type);
	}

    @Override
    public int[] getValueIndexes() {
        if (this.getValueSize() > 0) {
            return IntInterval.fromTo(0, this.getValueSize()-1).toArray();
        }
        else {
            return new int[] {};
        }
    }
    
    @Override
    public int getValueIndex(XAttribute xatt) {
        if (xatt instanceof XAttributeLiteral) {
            return ((IndexableAttribute)this).getValueIndex(((XAttributeLiteral)xatt).getValue());
        }
        if (xatt instanceof XAttributeDiscrete) {
            return ((IndexableAttribute)this).getValueIndex(((XAttributeDiscrete)xatt).getValue());
        }
        if (xatt instanceof XAttributeBoolean) {
            return ((IndexableAttribute)this).getValueIndex(((XAttributeBoolean)xatt).getValue());
        }
        else {
            return -1;
        }
    }
    
    @Override
    public int getValueIndex(XAttributable ele) {
        if ((this.getLevel() == AttributeLevel.EVENT || this.getLevel() == AttributeLevel.ACTIVITY) && !(ele instanceof XEvent)) {
            return -1;
        }
        else if (this.getLevel() == AttributeLevel.TRACE && !(ele instanceof XTrace)) {
            return -1;
        }
        else if (this.getLevel() == AttributeLevel.LOG && !(ele instanceof XLog)) {
            return -1;
        }
        
        XAttribute att = ele.getAttributes().get(this.getKey());
        if (att != null) {
            return this.getValueIndex(att);
        }
        else {
            return -1;
        }
    }
    
    @Override
    // On create lazily when matrix graph is used
    public AttributeMatrixGraph getMatrixGraph() {
        if (attGraph == null) {
            attGraph = new AttributeMatrixGraph(this);
        }
        return attGraph;
        
    }
    
    @Override
    public int getArtificialStartIndex() {
        return this.getValueSize();
    }
    
    @Override
    public int getArtificialEndIndex() {
        return this.getValueSize()+1;
    }
     
    @Override
    public void updateValueCount(XEvent event, boolean increase) {
        int valueIndex = getValueIndex(event);
        if (valueIndex >= 0) {
            activeValueIndexCountMap.addToValue(valueIndex, increase ? +1 : -1);
        }
    }
     
    @Override
    public void updateValueCount(ATrace trace, boolean increase) {
        for (XEvent event : trace.getOriginalEvents()) {
            updateValueCount(event, increase);
        }
    }
     
    @Override
    public double getValueRelativeFrequency(int valueIndex) {
        long totalCount = activeValueIndexCountMap.sum();
        return totalCount==0 ? 0 : 1.0*activeValueIndexCountMap.getIfAbsent(valueIndex, 0)/totalCount;
    }
    
}
