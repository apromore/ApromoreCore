package org.apromore.logman.stats.collector;

import java.util.Set;

import org.apromore.logman.AttributeStore;
import org.apromore.logman.LogManager;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

public class AttributeValueTraceStats extends StatsCollector {
	private AttributeStore attributeStore;
	private XLog originalLog;
	// attribute index => (value index => count in each trace)
	private UnifiedMap<Integer,UnifiedMap<Integer,LongArrayList>> attValueCountMap = new UnifiedMap<>();
	private int traceIndex = -1;
	
	public Set<Integer> getAttributeIndexes() {
    	return attValueCountMap.keySet();
    }
	
	public Set<Integer> getValueIndexes(int attIndex) {
		return attValueCountMap.get(attIndex).keySet();
	}
	
	public ImmutableLongList getValueTraceOccurrences(int attIndex, int valueIndex) {
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
    public void visitEvent(XEvent event) {
        for (XAttribute xatt : event.getAttributes().values()) {
            if (!(xatt instanceof XAttributeTimestamp)) {
            	int attIndex = attributeStore.getAttributeIndex(xatt, event);
            	int valueIndex = attributeStore.getValueIndex(xatt, event);
            	if (attIndex >=0 && valueIndex >= 0) {
            		attValueCountMap.putIfAbsent(attIndex, new UnifiedMap<>());
            		attValueCountMap.get(attIndex).putIfAbsent(valueIndex, new LongArrayList(originalLog.get(traceIndex).size()));
            		attValueCountMap.get(attIndex).get(valueIndex).addAtIndex(traceIndex, 1);
            	}
            }
        }
    }
    
    ///////////////////////// Update statistics //////////////////////////////
    
    @Override
    public void onLogFiltered(LogFilteredEvent filterEvent) {
        for (XTrace trace : filterEvent.getDeletedTraces()) {
        	int traceIndex = originalLog.indexOf(trace);
        	for (UnifiedMap<Integer,LongArrayList> valueMap: attValueCountMap.values()) {
        		for (LongArrayList countTrace : valueMap.values()) {
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
                    	int attIndex = attributeStore.getAttributeIndex(xatt, event);
                    	int valueIndex = attributeStore.getValueIndex(xatt, event);
                    	if (attIndex >=0 && valueIndex >= 0) {
                    		attValueCountMap.putIfAbsent(attIndex, new UnifiedMap<>());
                    		attValueCountMap.get(attIndex).putIfAbsent(valueIndex, new LongArrayList());
                    		attValueCountMap.get(attIndex).get(valueIndex).addAtIndex(traceIndex, -1);
                    	}    				
        			}
        		}

        	}
        }
    }
}
