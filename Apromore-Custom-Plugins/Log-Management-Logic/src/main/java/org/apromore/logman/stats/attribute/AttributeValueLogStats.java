package org.apromore.logman.stats.attribute;

import java.util.Set;

import org.apromore.logman.AttributeStore;
import org.apromore.logman.LogManager;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.stats.StatsCollector;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.api.map.primitive.ImmutableIntIntMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;

public class AttributeValueLogStats extends StatsCollector {
	private AttributeStore attributeStore;
    // attribute index in the attribute store => (value index => occurrence count)
    private UnifiedMap<Integer,IntIntHashMap> attValueCountMap = new UnifiedMap<>();
    
    public Set<Integer> getAttributeIndexes() {
    	return attValueCountMap.keySet();
    }
    
    public ImmutableIntIntMap getValueOccurrences(int attIndex) {
        return attValueCountMap.get(attIndex).toImmutable();
    }
    
    ///////////////////////// Collect statistics the first time //////////////////////////////
    
    @Override
    public void startVisit(LogManager logManager) {
    	attributeStore = logManager.getAttributeStore();
    	attValueCountMap.clear();
    }

    @Override
    public void visitEvent(XEvent event) {
        for (XAttribute xatt : event.getAttributes().values()) {
            if (!(xatt instanceof XAttributeTimestamp)) {
            	int attIndex = attributeStore.getAttributeIndex(xatt, event);
            	int valueIndex = attributeStore.getValueIndex(xatt, event);
            	if (attIndex >=0 && valueIndex >= 0) {
            		attValueCountMap.putIfAbsent(attIndex, new IntIntHashMap());
            		attValueCountMap.get(attIndex).addToValue(valueIndex, 1);
            	}
            }
        }
    }    

    ///////////////////////// Update statistics //////////////////////////////
    @Override
    public void onLogFiltered(LogFilteredEvent filterEvent) {
        for (XEvent event : filterEvent.getAllDeletedEvents()) {
        	for (XAttribute xatt : event.getAttributes().values()) {
        		int attIndex = attributeStore.getAttributeIndex(xatt, event);
            	int valueIndex = attributeStore.getValueIndex(xatt, event);
            	if (attIndex >=0 && valueIndex >= 0) {
            		attValueCountMap.get(attIndex).addToValue(valueIndex, -1);
            	}
        	}
        }
    }

}
