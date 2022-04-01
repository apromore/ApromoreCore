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

package org.apromore.logman;

import java.util.BitSet;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntLists;

/**
 * This class is used to unify the update of all bitset-based trace and event status for a log.
 * The event bitset is used to indicate the status of each "event" in a trace which can be an activity
 * or raw event depending on the context of use (e.g. it is events for ALog/ATrace, but activity for AttributeLog/AttributeTrace).
 * 
 * Note that JDK's BitSet does not store the actual number of bits used (either true or false). Any non-negative index
 * always has either true or false bit. It is therefore impossible to check the validity of a bitset
 * against a log or trace. A different implementation of BitSet must be used if this check is important.
 * 
 * When using, trace bitset must be set first, then add event bitset for each trace.
 * 
 * Note that the trace bitSet takes precendence over the event bitset. This means if the bit of trace ith is false
 * then all event bitset of trace ith is clear (false). If the bit of trace ith is true, then each bit in the 
 * event bitset of trace ith is checked. This is to ensure the integrity of the LogBitMap object.
 * 
 * @author Bruce Nguyen
 *
 */
public class LogBitMap {
    private BitSet traceBitSet; 
    private int size; // this is also the size of traceBitSet 
    private MutableList<BitSet> traceEventBitSets; // event bitset of each trace
    private MutableIntList traceEventBitSetSizes = IntLists.mutable.empty(); // the size of each of traceEventBitSets
    
    public LogBitMap(int numberOfTraces) throws InvalidLogBitMapException {
        if (numberOfTraces <= 0) {
            throw new InvalidLogBitMapException("Invalid number of traces: must be positive values.");
        }
        else {
            this.size = numberOfTraces;
            traceEventBitSets = Lists.mutable.empty();
        }
    }
    
    public int size() {
        return this.size;
    }
    
    // Always clone for the later difference check between this bitset and the source bitset 
    public void setTraceBitSet(BitSet traceBitSet, int traceBitSetSize) throws InvalidLogBitMapException {
        if (traceBitSetSize != size) {
            throw new InvalidLogBitMapException("Invalid trace bitset: different size from the number of traces!");
        }
        else {
            this.traceBitSet = (BitSet)traceBitSet.clone();
        }
    }
    
    public BitSet getTraceBitSet() {
        return this.traceBitSet;
    }
    
    public int getTraceBitSetSize() {
        return this.size;
    }
    
    // The bitSetSize param is used to check the validity of eventBitSet.
    // This is because JDK's BitSet does not keep the real number of bits in use
    // Always clone for the later difference check between this bitset and the source bitset
    // The trace bitset takes precendence to ensure the integrity of this LogBitMap
    // If a trace bit is not set, all its event bits must be also clear.
    public void addEventBitSet(BitSet eventBitSet, int bitSetSize) throws InvalidLogBitMapException {
        if (traceBitSet == null) {
            throw new InvalidLogBitMapException("trace bitset has not been set. It must be set before any event bitsets.");
        }
        if (traceEventBitSets.size() >= size) {
            throw new InvalidLogBitMapException("Cannot add any further event bitset to the list of event bitsets for traces");
        }
        else {
            if (!traceBitSet.get(traceEventBitSets.size())) {
                traceEventBitSets.add(new BitSet(bitSetSize)); // clear all event bits as trace bitset takes precedence
            }
            else {
                traceEventBitSets.add((BitSet)eventBitSet.clone());
            }
            traceEventBitSetSizes.add(bitSetSize);
        }
    }
    
    public BitSet getEventBitSetAtIndex(int index) {
        if (index < 0 || index >= traceEventBitSets.size()) {
            return null;
        }
        else {
            return traceEventBitSets.get(index);
        }
    }
    
    public int getEventBitSetSizeAtIndex(int index) {
        if (index < 0 || index >= traceEventBitSets.size()) {
            return -1;
        }
        else {
            return traceEventBitSetSizes.get(index);
        }
    }
    
    // Always clone for the later difference check between this bitset and the source bitset
    public void setEventBitSetAtIndex(int index, BitSet eventBitSet, int eventBitSetSize) throws InvalidLogBitMapException {
        if (index < 0 || index >= traceEventBitSets.size()) {
            throw new InvalidLogBitMapException("Add event bitset: invalid trace index = " + index);
        }
        else if (eventBitSet == null) {
            throw new InvalidLogBitMapException("Add event bitset: invalid bitset, bitset = null");
        }
        else if (traceBitSet.get(index)) {
            traceEventBitSets.set(index, (BitSet)eventBitSet.clone());
            traceEventBitSetSizes.set(index, eventBitSetSize);
        }
    }
    
    public void clear() {
        traceBitSet.clear();
        traceEventBitSets.clear();
        traceEventBitSetSizes.clear();
    }
    
    // To create test data, all bits are set to true after creation
    public static BitSet newBitSet(int size, int setIndexFrom, int setIndexTo) {
        BitSet bitset = new BitSet(size);
        bitset.set(setIndexFrom, setIndexTo);
        return bitset;
    }
    
    public static BitSet newBitSet(int size) {
        BitSet bitset = new BitSet(size);
        bitset.set(0, size);
        return bitset;
    }
}
