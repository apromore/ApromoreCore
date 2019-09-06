package org.apromore.logman.stats.calculators;

import java.util.HashMap;
import java.util.Map;

import org.apromore.logman.log.Constants;
import org.apromore.logman.log.LogVisitor;
import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilterListener;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;

public class EventAttributeStats extends StatsCollector {
    // attribute name => (attribute value => occurrence count)
    private Map<String, Map<String, Integer>> attributeTotalFreqMap = new HashMap<>();
    
    public Map<String, Integer> getAttributeTotalFrequencies(String attribute) {
        return attributeTotalFreqMap.get(attribute);
    }

    @Override
    public void visitEvent(XEvent event) {
        for (XAttribute attribute : event.getAttributes().values()) {
            if (!(attribute instanceof XAttributeTimestamp)) {
                String key = attribute.getKey();
                if(attributeTotalFreqMap.get(key) == null) attributeTotalFreqMap.put(key, new HashMap<>());
                Integer i = attributeTotalFreqMap.get(key).get(attribute.toString());
                if (i == null) attributeTotalFreqMap.get(key).put(attribute.toString(), 1);
                else attributeTotalFreqMap.get(key).put(attribute.toString(), i + 1);
            }
        }
    }    

    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }

}
