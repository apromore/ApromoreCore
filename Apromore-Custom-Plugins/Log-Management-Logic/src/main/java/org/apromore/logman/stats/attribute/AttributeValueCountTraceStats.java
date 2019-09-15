package org.apromore.logman.stats.attribute;

import org.apromore.logman.AttributeStore;
import org.apromore.logman.LogManager;
import org.apromore.logman.attribute.Attribute;
import org.apromore.logman.event.LogFilteredEvent;
import org.apromore.logman.stats.StatsCollector;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.LongLists;

public class AttributeValueCountTraceStats extends StatsCollector {
	private AttributeStore attributeStore;
	private XLog originalLog;
	// attribute index => (value index => occurrence count of the value in each trace index in the log)
	private MutableIntObjectMap<MutableIntObjectMap<MutableLongList>> attValueCountMap = IntObjectMaps.mutable.empty();
	private int traceIndex = -1;
	
	public ImmutableIntSet getAttributeIndexes() {
    	return attValueCountMap.keySet().toImmutable();
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
    	Attribute attribute = attributeStore.getAttribute(xatt, event);
    	int attIndex = attributeStore.getAttributeIndex(attribute);
    	int valueSize = attribute.getValueSize();
    	int valueIndex = attribute.getValueIndex(xatt, event);
    	if (attIndex >=0 && valueIndex >= 0) {
    		attValueCountMap.getIfAbsentPut(attIndex, IntObjectMaps.mutable.empty())
    			.getIfAbsentPut(valueIndex, LongLists.mutable.with(new long[originalLog.get(traceIndex).size()]));
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
