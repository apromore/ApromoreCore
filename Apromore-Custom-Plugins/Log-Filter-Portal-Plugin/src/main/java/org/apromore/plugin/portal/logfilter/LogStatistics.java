package org.apromore.plugin.portal.logfilter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;

import static java.util.Map.Entry.comparingByValue;

public class LogStatistics {
	public static final String DEFAULT_CLASSIFIER_KEY = "concept:name";
	public static final String CASE_VARIANT_KEY = "case:variant";
	public static final String TIMESTAMP_KEY = "time:timestamp";
	public static final String LIFECYCLE_KEY = "lifecycle:transition";
	public static final String TIME_DURATION_KEY = "time:duration";
	public static final String DIRECTLY_FOLLOW_KEY = "direct:follow";
	public static final String EVENTUALLY_FOLLOW_KEY = "eventually:follow";
	
	private Map<String, Map<String, Integer>> options_frequency;
    private long min = Long.MAX_VALUE; //the earliest timestamp of the log
    private long max = 0; //the latest timestamp of the log
	private XLog log;
	private String eventClassifier = DEFAULT_CLASSIFIER_KEY;
	private Map<String, Set<String>> directFollowMap = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> eventualFollowMap = new HashMap<String, Set<String>>();
    private Map<Integer, List<String>> variantEventsMap = new HashMap<Integer, List<String>>();

	public LogStatistics(XLog log) {
		this.log = log;
		this.eventClassifier = DEFAULT_CLASSIFIER_KEY;
		options_frequency = this.generateStatistics(log, true);
	}
	
	public LogStatistics(XLog log, String eventClassifier) {
		this.log = log;
		this.eventClassifier = eventClassifier;
		options_frequency = this.generateStatistics(log, true);
	}
	
	public LogStatistics(Map<String, Map<String, Integer>> stats, long min, long max) {
		this.eventClassifier = DEFAULT_CLASSIFIER_KEY;
		this.min = min;
		this.max = max;
		this.options_frequency = stats;
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

        Map<List<String>, Integer> variantFrequencyMap = variantFrequencyMapOf(log);
        Map<List<String>, Integer> variantIdMap = variantIdMapOf(variantFrequencyMap);
//        System.out.println(variantIdMap);

        Map<String, Integer> variIdFreqMap = new HashMap<String, Integer>();
        for(List<String> list : variantIdMap.keySet()) {
            String variantIdString = variantIdMap.get(list).toString();
            int variantFreq = variantFrequencyMap.get(list);
            variIdFreqMap.put(variantIdString, variantFreq);
        }
        tmp_options_frequency.put(CASE_VARIANT_KEY, variIdFreqMap);
        this.log = logWithVariant(this.log, variantIdMap);

//        XAttributeMap xm = this.log.get(0).getAttributes();
//        System.out.println(xm);

        for (XTrace trace : log) {

            if (attributeStat) {
	            for (XEvent event : trace) {
	                for (XAttribute attribute : event.getAttributes().values()) {
	                    String key = attribute.getKey();
	                    if (!(key.equals("lifecycle:model") || key.equals(TIMESTAMP_KEY))) {
	                        tmp_options.put(key, attribute.toString());
	                        if(tmp_options_frequency.get(key) == null) tmp_options_frequency.put(key, new HashMap<>());
	
	                        Integer i = tmp_options_frequency.get(key).get(attribute.toString());
	                        if (i == null) tmp_options_frequency.get(key).put(attribute.toString(), 1);
	                        else tmp_options_frequency.get(key).put(attribute.toString(), i + 1);
	                    }
	                    if (key.equals(TIMESTAMP_KEY)) {
	                        min = Math.min(min, ((XAttributeTimestamp) attribute).getValueMillis());
	                        max = Math.max(max, ((XAttributeTimestamp) attribute).getValueMillis());
	                    }
	                }
	            }
            }

            /**
             * Calculating Sequent // removed at 2019-08-19
             */
//            for (int i = -1; i < trace.size(); i++) {
//                String event1;
////                if (i == -1) event1 = "|>";
//                if (i == -1) event1 = "[Start]";
//                else event1 = trace.get(i).getAttributes().get(getLabel()).toString();
//
//                for (int j = i + 1; j < trace.size() + 1; j++) {
//                    String event2;
////                    if (j == trace.size()) event2 = "[]";
//                    if (j == trace.size()) event2 = "[End]";
//                    else {
//                        XAttribute attribute = trace.get(j).getAttributes().get(getLabel());
//                        if (attribute != null) event2 = attribute.toString();
//                        else event2 = "";
//                    }
//
//                    if(j == i + 1) {
//                        String df = (event1 + " => " + event2);
//                        tmp_options.put(DIRECTLY_FOLLOW_KEY, df);
//                        if (tmp_options_frequency.get(DIRECTLY_FOLLOW_KEY) == null)
//                            tmp_options_frequency.put(DIRECTLY_FOLLOW_KEY, new HashMap<>());
//                        Integer k = tmp_options_frequency.get(DIRECTLY_FOLLOW_KEY).get(df);
//                        if (k == null) tmp_options_frequency.get(DIRECTLY_FOLLOW_KEY).put(df, 1);
//                        else tmp_options_frequency.get(DIRECTLY_FOLLOW_KEY).put(df, k + 1);
//                    }
//                    if(i != -1 && j != trace.size()) {
//                        String ef = (event1 + " => " + event2);
//                        tmp_options.put(EVENTUALLY_FOLLOW_KEY, ef);
//                        if (tmp_options_frequency.get(EVENTUALLY_FOLLOW_KEY) == null)
//                            tmp_options_frequency.put(EVENTUALLY_FOLLOW_KEY, new HashMap<>());
//                        Integer k = tmp_options_frequency.get(EVENTUALLY_FOLLOW_KEY).get(ef);
//                        if (k == null) tmp_options_frequency.get(EVENTUALLY_FOLLOW_KEY).put(ef, 1);
//                        else tmp_options_frequency.get(EVENTUALLY_FOLLOW_KEY).put(ef, k + 1);
//                    }
//                }
//            }
        }
        tmp_options_frequency.put(DIRECTLY_FOLLOW_KEY, new HashMap<>());
        tmp_options_frequency.put(EVENTUALLY_FOLLOW_KEY, new HashMap<>());
        tmp_options_frequency.put(TIMESTAMP_KEY, new HashMap<>());
        tmp_options_frequency.put(TIME_DURATION_KEY, new HashMap<>());

        return tmp_options_frequency;
    }

    private XLog logWithVariant(XLog log, Map<List<String>, Integer> variantIdMap) {
        for(int i=0; i<log.size(); i++) {
            XTrace trace = log.get(i);
            List<String> actList = activitySequenceOf(trace);
            if(actList.size() > 0) {
                int variantId = variantIdMap.get(actList);
                variantEventsMap.put(variantId, actList);
                XAttribute attribute = new XAttributeLiteralImpl(CASE_VARIANT_KEY, Integer.toString(variantId));
                log.get(i).getAttributes().put(CASE_VARIANT_KEY, attribute);
            }
        }
        return log;
    }

    private Map<List<String>, Integer> variantIdMapOf(Map<List<String>, Integer> variantFrequencyMap) {
        Map<List<String>, Integer> variIdMap = new HashMap<List<String>, Integer>();

        List<Map.Entry<List<String>, Integer>> list = new ArrayList<Map.Entry<List<String>, Integer>>(variantFrequencyMap.entrySet());
        list.sort(comparingByValue());

        int idNum = 1;

        for (int i = (list.size() - 1); i >= 0; i--) { //from big to small
            List<String> eventNames = list.get(i).getKey();
            variIdMap.put(eventNames, idNum);
            idNum += 1;
        }
        return variIdMap;
    }

    private Map<List<String>, Integer> variantFrequencyMapOf(XLog theLog) {
        Map<List<String>, Integer> variFreqMap = new HashMap<List<String>, Integer>();
        for(int i=0; i<theLog.size(); i++) {
            XTrace trace = theLog.get(i);
            List<String> actSeq = activitySequenceOf(trace);
            if(actSeq.size() > 0) {
                if(variFreqMap.containsKey(actSeq)) {
                    int freq = variFreqMap.get(actSeq) + 1;
                    variFreqMap.put(actSeq, freq);
                }else{
                    variFreqMap.put(actSeq, 1);
                }
            }
        }
        return variFreqMap;
    }

    private List<String> activitySequenceOf(XTrace trace) {
        List<String> activitySequence = new ArrayList<String>();

        // key: timestamp, value: not used
        HashMap<ZonedDateTime, Integer> markedMap = new HashMap<ZonedDateTime, Integer>();

        for(int i=0; i < trace.size(); i++) {
            XEvent iEvent = trace.get(i);
            boolean hasStart = true;
            String iActName = "";
            if(iEvent.getAttributes().containsKey(DEFAULT_CLASSIFIER_KEY)) {
                iActName = iEvent.getAttributes().get(DEFAULT_CLASSIFIER_KEY).toString();
            }

            /**
             * Directly followed
             */
            if(i < (trace.size()-1)) {
                XEvent dfEvent = trace.get(i+1);
                if(dfEvent.getAttributes().containsKey(DEFAULT_CLASSIFIER_KEY)) {
                    String dfActName = dfEvent.getAttributes().get(DEFAULT_CLASSIFIER_KEY).toString();
                    if(directFollowMap.containsKey(iActName)) {
                        Set followSet = directFollowMap.get(iActName);
                        followSet.add(dfActName);
                        directFollowMap.put(iActName, followSet);
                    }else{
                        Set followSet = new HashSet();
                        followSet.add(dfActName);
                        directFollowMap.put(iActName, followSet);
                    }
                }
            }

            /**
             * Eventually followed
             */
            for(int j= (i+1); j<trace.size(); j++) {
                XEvent fEvent = trace.get(j);
                if(fEvent.getAttributes().containsKey(DEFAULT_CLASSIFIER_KEY)) {
                    String fActName = fEvent.getAttributes().get(DEFAULT_CLASSIFIER_KEY).toString();
                    if(eventualFollowMap.containsKey(iActName)) {
                        Set followSet = eventualFollowMap.get(iActName);
                        followSet.add(fActName);
                        eventualFollowMap.put(iActName, followSet);
                    }else{
                        Set followSet = new HashSet();
                        followSet.add(fActName);
                        eventualFollowMap.put(iActName, followSet);
                    }
                }
            }

            String iLifecycle = "";
            if(iEvent.getAttributes().containsKey(LIFECYCLE_KEY)) {
                iLifecycle = iEvent.getAttributes().get(LIFECYCLE_KEY).toString().toLowerCase();
            }
            ZonedDateTime iZdt = zonedDateTimeOf(iEvent);
            if(iLifecycle.equals("start")) {
                for(int j=(i+1); j < trace.size(); j++) {
                    XEvent jEvent = trace.get(j);
                    String jActName = "";
                    if(jEvent.getAttributes().containsKey(DEFAULT_CLASSIFIER_KEY)) {
                        jActName = jEvent.getAttributes().get(DEFAULT_CLASSIFIER_KEY).toString();
                    }
                    String jLifecycle = "";
                    if(jEvent.getAttributes().containsKey(LIFECYCLE_KEY)) {
                        jLifecycle = iEvent.getAttributes().get(LIFECYCLE_KEY).toString().toLowerCase();
                    }
                    ZonedDateTime jZdt = null;
                    if(jEvent.getAttributes().containsKey(TIMESTAMP_KEY)) {
                        jZdt = zonedDateTimeOf(jEvent);
                    }
                    if(jActName.equals(iActName) && jLifecycle.equals("complete")) {
                        markedMap.put(jZdt, 0);
                        activitySequence.add(iActName);
                        break;
                    }
                }
            }
            if(iLifecycle.equals("complete") && !markedMap.containsKey(iZdt)) {
                activitySequence.add(iActName);
            }
        }
        return activitySequence;
    }

    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        XAttribute da =
                xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP);
        Date d = ((XAttributeTimestamp) da).getValue();
        ZonedDateTime z =
                ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
        return z;
    }

    private String getLabel() {
    	return this.eventClassifier;
    }

    public Map<String, Set<String>> getDirectFollowMap() {
        return directFollowMap;
    }

    public Map<String, Set<String>> getEventualFollowMap() {
        return eventualFollowMap;
    }

    public Map<Integer, List<String>> getVariantEventsMap() {
        return variantEventsMap;
    }
}
