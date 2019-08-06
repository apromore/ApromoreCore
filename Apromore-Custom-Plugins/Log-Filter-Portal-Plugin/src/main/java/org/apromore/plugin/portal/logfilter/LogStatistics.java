package org.apromore.plugin.portal.logfilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class LogStatistics {
	private Map<String, Map<String, Integer>> options_frequency;
	private long min;
	private long max;
	private XLog log;
	private String eventClassifier;
	
	public LogStatistics(XLog log) {
		this.log = log;
		this.eventClassifier = "concept:name";
		options_frequency = this.generateStatistics(log, true);
	}
	
	public Map<String, Map<String, Integer>> getStatistics() {
		return Collections.unmodifiableMap(options_frequency);
	}
	
	public long getMinTimestamp() {
		return min;
	}
	
	public long getMaxTimetamp() {
		return max;
	}
	
	public XLog getLog() {
		return log;
	}
	
    /**
     * Collect frequency statistics for all event attributes except the timestamp
     * options_frequency is updated
     * Key: attribute key
     * Value: map (key: attribute value, value: frequency count of the value)
     * @param log
     */
    private Map<String, Map<String, Integer>> generateStatistics(XLog log, boolean attributeStat) {
        //boolean firstTime = (options_frequency.keySet().size() == 0);
        Multimap<String, String> tmp_options = HashMultimap.create(); //map from attribute key to attribute values
        
        //key: type of attribute (see LogFilterTypeSelector), value: map (key: attribute value, value: frequency count)
        Map<String, Map<String, Integer>> tmp_options_frequency = new HashMap<>();

        for (XTrace trace : log) {
            if (attributeStat) {
	            for (XEvent event : trace) {
	                for (XAttribute attribute : event.getAttributes().values()) {
	                    String key = attribute.getKey();
	                    if (!(key.equals("lifecycle:model") || key.equals("time:timestamp"))) {
	                        tmp_options.put(key, attribute.toString());
	                        if(tmp_options_frequency.get(key) == null) tmp_options_frequency.put(key, new HashMap<>());
	
	                        Integer i = tmp_options_frequency.get(key).get(attribute.toString());
	                        if (i == null) tmp_options_frequency.get(key).put(attribute.toString(), 1);
	                        else tmp_options_frequency.get(key).put(attribute.toString(), i + 1);
	                    }
	                    if (key.equals("time:timestamp")) {
	                        min = Math.min(min, ((XAttributeTimestamp) attribute).getValueMillis());
	                        max = Math.max(max, ((XAttributeTimestamp) attribute).getValueMillis());
	                    }
	                }
	            }
            }

            for (int i = -1; i < trace.size(); i++) {
                String event1;
                if (i == -1) event1 = "|>";
                else event1 = trace.get(i).getAttributes().get(getLabel()).toString();

                for (int j = i + 1; j < trace.size() + 1; j++) {
                    String event2;
                    if (j == trace.size()) event2 = "[]";
                    else {
                        XAttribute attribute = trace.get(j).getAttributes().get(getLabel());
                        if (attribute != null) event2 = attribute.toString();
                        else event2 = "";
                    }

                    if(j == i + 1) {
                        String df = (event1 + " => " + event2);
                        tmp_options.put("direct:follow", df);
                        if (tmp_options_frequency.get("direct:follow") == null)
                            tmp_options_frequency.put("direct:follow", new HashMap<>());
                        Integer k = tmp_options_frequency.get("direct:follow").get(df);
                        if (k == null) tmp_options_frequency.get("direct:follow").put(df, 1);
                        else tmp_options_frequency.get("direct:follow").put(df, k + 1);
                    }
                    if(i != -1 && j != trace.size()) {
                        String ef = (event1 + " => " + event2);
                        tmp_options.put("eventually:follow", ef);
                        if (tmp_options_frequency.get("eventually:follow") == null)
                            tmp_options_frequency.put("eventually:follow", new HashMap<>());
                        Integer k = tmp_options_frequency.get("eventually:follow").get(ef);
                        if (k == null) tmp_options_frequency.get("eventually:follow").put(ef, 1);
                        else tmp_options_frequency.get("eventually:follow").put(ef, k + 1);
                    }
                }
            }
        }
        
        tmp_options_frequency.put("time:timestamp", new HashMap<>());
        tmp_options_frequency.put("time:duration", new HashMap<>());

        return tmp_options_frequency;
    }
    
    private String getLabel() {
    	return this.eventClassifier;
    }
}	
