package org.apromore.logman.stats.attribute;

import org.apromore.logman.AttributeStore;
import org.apromore.logman.LogManager;
import org.apromore.logman.attribute.Attribute;
import org.apromore.logman.event.LogFilteredEvent;
import org.apromore.logman.stats.StatsCollector;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.api.map.primitive.ImmutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;

public class AttributeValueCountLogStats extends StatsCollector {
	private AttributeStore attributeStore;
    // attribute index in the attribute store => (value index => occurrence count)
    private MutableIntObjectMap<MutableIntIntMap> attValueCountMap = IntObjectMaps.mutable.empty();
    
    // get map: value => count for one attribute
    // the using program should know the type of the attribute being used
    // and then usign the returning value (Object type) properly
    public ImmutableObjectIntMap<Object> getValueLogCounts(Attribute attribute) {
    	int attIndex = attributeStore.getAttributeIndex(attribute);
    	MutableObjectIntMap<Object> valueCounts = ObjectIntMaps.mutable.empty();
    	valueCounts.each(valueIndex -> {
    		Object value = attribute.getObjectValue(valueIndex);
    		if (value != null) {
    			valueCounts.put(value, attValueCountMap.get(attIndex).get(valueIndex));
    		}
    	});
    	return valueCounts.toImmutable();
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
            	Attribute att = attributeStore.getAttribute(xatt, event);
            	int attIndex = attributeStore.getAttributeIndex(att);
            	int valueIndex = att.getValueIndex(xatt, event);
            	if (attIndex >=0 && valueIndex >= 0) {
            		attValueCountMap.getIfAbsentPut(attIndex, IntIntMaps.mutable.empty());
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
        		Attribute att = attributeStore.getAttribute(xatt, event);
        		int attIndex = attributeStore.getAttributeIndex(xatt, event);
            	int valueIndex = att.getValueIndex(xatt, event);
            	if (attIndex >=0 && valueIndex >= 0) {
            		attValueCountMap.get(attIndex).addToValue(valueIndex, -1);
            	}
        	}
        }
    }

}
