package org.apromore.logman.stats.attribute;

import java.util.Set;

import org.apromore.logman.AttributeStore;
import org.apromore.logman.LogManager;
import org.apromore.logman.event.LogFilteredEvent;
import org.apromore.logman.stats.StatsCollector;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

public class AttributeDurationTraceStats extends StatsCollector {
	private AttributeStore attributeStore;
	private XLog originalLog;
	// attribute index => (value index => count in each trace)
	private MutableMap<Integer,MutableIntObjectMap<MutableLongList>> attValueCountMap = Maps.mutable.empty();
	private int traceIndex = -1;
	
	public Set<Integer> getAttributeIndexes() {
    	return attValueCountMap.keySet();
    }
	
	public ImmutableIntSet getValueIndexes(int attIndex) {
		return attValueCountMap.get(attIndex).keySet().toImmutable();
	}
	
	public ImmutableLongList getValueTraceCounts(int attIndex, int valueIndex) {
		return attValueCountMap.get(attIndex).get(valueIndex).toImmutable().select(a -> a != -1);
	}
	
	
	///////////////////////// Collect statistics the first time //////////////////////////////
    @Override
    public void startVisit(LogManager logManager) {
    	originalLog = logManager.getLog();
    	attributeStore = logManager.getAttributeStore();
    	attValueCountMap.clear();
    	traceIndex = -1;
    }
    
    @Override
    public void visitTrace(XTrace trace) {
    	traceIndex++;
    }

    @Override
    // If visitActivity() is not used, this visitEvent can be used 
    // for both activities and events as Activity is also XEvent
    public void visitEvent(XEvent event) {
        for (XAttribute xatt : event.getAttributes().values()) {
            if (!(xatt instanceof XAttributeTimestamp)) {
            	addToData(xatt, event, traceIndex, true);
            }
        }
    }
    
    private void addToData(XAttribute xatt, XEvent event, int traceIndex, boolean increase) {
    	int attIndex = attributeStore.getAttributeIndex(xatt, event);
    	int valueSize = attributeStore.getValueRangeSize(xatt, event);
    	int valueIndex = attributeStore.getValueIndex(xatt, event);
    	if (attIndex >=0 && valueIndex >= 0) {
    		attValueCountMap.putIfAbsent(attIndex, new IntObjectHashMap<>(valueSize));
    		attValueCountMap.get(attIndex).getIfAbsentPut(valueIndex, LongLists.mutable.with(new long[originalLog.get(traceIndex).size()]));
    		attValueCountMap.get(attIndex).get(valueIndex).addAtIndex(traceIndex, (increase ? 1 : -1));
    	}
    }
    
    ///////////////////////// Update statistics //////////////////////////////
    
    @Override
    public void onLogFiltered(LogFilteredEvent filterEvent) {
        for (XTrace trace : filterEvent.getDeletedTraces()) {
        	int traceIndex = originalLog.indexOf(trace);
        	for (MutableIntObjectMap<MutableLongList> valueMap: attValueCountMap.values()) {
        		for (MutableLongList countTrace : valueMap.values()) {
        			countTrace.set(traceIndex,-1); //mark that this trace has been deleted
        		}
        	}
        }
        
        for (Pair<XTrace,XTrace> pair : filterEvent.getUpdatedTraces()) {
        	XTrace old = pair.getOne();
        	int traceIndex = originalLog.indexOf(old);
        	XTrace ne = pair.getTwo();
        	for (XEvent event : old) {
        		if (!ne.contains(event)) {
        			for (XAttribute xatt : event.getAttributes().values()) {
        				addToData(xatt, event, traceIndex, false);  				
        			}
        		}

        	}
        }
    }
}
