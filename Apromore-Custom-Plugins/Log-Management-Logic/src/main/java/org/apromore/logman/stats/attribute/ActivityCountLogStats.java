package org.apromore.logman.stats.attribute;

import java.util.IntSummaryStatistics;

import org.apromore.logman.AttributeStore;
import org.apromore.logman.Constants;
import org.apromore.logman.LogManager;
import org.apromore.logman.attribute.Attribute;
import org.apromore.logman.attribute.AttributeLevel;
import org.apromore.logman.event.LogFilteredEvent;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.stats.StatsCollector;
import org.eclipse.collections.api.map.primitive.ImmutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;

public class ActivityCountLogStats extends StatsCollector {
	AttributeStore attributeStore;
	Attribute attribute; 
	private MutableIntIntMap actCountMap; //value index => count
	
	public ImmutableIntIntMap getActivitiesWithCounts() {
		return actCountMap.toImmutable();
	}
	
	// can be used to calculate relative frequency
	public long getTotalCountOfValues() {
		return actCountMap.values().sum();
	}
	
	public IntSummaryStatistics getSummaryStatistics() {
		return actCountMap.summaryStatistics();
	}
	
	///////////////////////// Collect statistics the first time //////////////////////////////
	
	@Override
	public void startVisit(LogManager logManager) {
		attributeStore = logManager.getAttributeStore();
		attribute = attributeStore.getAttribute(Constants.ATT_KEY_CONCEPT_NAME, AttributeLevel.EVENT);
		if (actCountMap == null) {
			actCountMap = IntIntMaps.mutable.empty();
		}
		else {
			actCountMap.clear();
		}
	}
	
    @Override
    public void visitActivity(Activity act) {
    	if (attribute != null) {
	    	int valueIndex = attribute.getValueIndex(act.getAttributes().get(Constants.ATT_KEY_CONCEPT_NAME), act);
	    	if (valueIndex >0) actCountMap.addToValue(valueIndex, 1);
    	}
    }
    
    ///////////////////////// Update statistics //////////////////////////////    
    
    @Override
    public void onLogFiltered(LogFilteredEvent event) {
    	if (attribute != null) {
	        for (Activity act : event.getAllDeletedActs()) {
	        	int valueIndex = attribute.getValueIndex(act.getAttributes().get(Constants.ATT_KEY_CONCEPT_NAME), act);
	        	if (valueIndex >0) actCountMap.addToValue(valueIndex, -1);
	        }
    	}
    }
}
