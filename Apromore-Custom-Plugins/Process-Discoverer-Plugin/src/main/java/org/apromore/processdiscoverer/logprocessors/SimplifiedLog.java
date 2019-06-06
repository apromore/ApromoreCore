package org.apromore.processdiscoverer.logprocessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

/**
 * Simplified log: every trace is a list of integer, each integer is mapped to the corresponding classifier value
 * Example trace: 13456777882 (1 and 2 are used to mark the start and end of the trace)
 * Note: an activity with start and complete is stored separately, e.g. A_start, A_complete would
 * be stored as two separate events.
 * @author Bruce Nguyen
 *
 */
public class SimplifiedLog extends ArrayList<IntList> {
	public final static String START_NAME = "|>"; //marker for the start event in a trace in simplifiedNameMap
    public final static String END_NAME = "[]"; //marker for the end event in a trace in simplifiedNameMap
    public final static int START_INT = 1; // marker for the start event in a trace in the simplified log (index-based)
    public final static int END_INT = 2; // marker for the end event in a trace in the simplified log
    
	private HashBiMap<String, Integer> simplifiedNameMap = new HashBiMap<>(); //bi-directiona map between string name and integer name
	private Map<String,List<Integer>> collapsedNameMap = new HashMap<>(); //map from collapsed name to a list of integer-based names
	private Map<Integer,Boolean> startEventMap = new HashMap<>();
	private Map<Integer,Boolean> completeEventMap = new HashMap<>();
	private HashBiMap<Integer, Integer> startCompleteEventMap = new HashBiMap<>(); //map between integer-based start and complete events
    private XLog xlog;
    
	public SimplifiedLog(XLog xlog, XEventAttributeClassifier classifier) throws Exception {
		super();
		this.xlog = xlog;
		
        simplifiedNameMap.put(START_NAME, START_INT); 
        simplifiedNameMap.put(END_NAME, END_INT); 
        
        collapsedNameMap.put(START_NAME, Arrays.asList(new Integer[] {START_INT}));
        collapsedNameMap.put(END_NAME, Arrays.asList(new Integer[] {END_INT}));

        for(XTrace trace : xlog) {
            IntArrayList simplified_trace = new IntArrayList(trace.size());
            simplified_trace.add(START_INT); //add artificial start event to a trace

            IntIntHashMap eventsCount = new IntIntHashMap();
            for(XEvent event : trace) {
                String name = classifier.getClassIdentity(event);
                if(name.toLowerCase().endsWith(LogUtils.PLUS_START_CODE) || 
                		name.toLowerCase().endsWith(LogUtils.PLUS_COMPLETE_CODE)) {
                    String prename = name.substring(0, name.indexOf("+"));
                    String postname = name.substring(name.indexOf("+"));
                    name = prename + postname.toLowerCase(); //name = "xxx+start" or "xxx+complete" or "xxx+schedule"
                }
                else {
                	throw new Exception("Trace with id=" + 
                				LogUtils.getConceptName(trace) + ": event " + name + 
                				" have no lifecycle:transition or lifecycle:transition is not 'start' or 'complete'.");
                }

                Integer simplified_event;
                if((simplified_event = getEventNumber(name)) == null) {
                    simplified_event = simplifiedNameMap.size() + 1;
                    simplifiedNameMap.put(name, simplified_event);
                }
                
                startEventMap.put(simplified_event, LogUtils.isStartEvent(name));
                completeEventMap.put(simplified_event, LogUtils.isCompleteEvent(name));
                String collapsed_name = LogUtils.getCollapsedEvent(name);
                if (!collapsedNameMap.containsKey(collapsed_name)) {
                	collapsedNameMap.put(collapsed_name, new ArrayList<>());
                }
                if (!collapsedNameMap.get(collapsed_name).contains(simplified_event)) {
                	collapsedNameMap.get(collapsed_name).add(simplified_event);
                }
                if (collapsedNameMap.get(collapsed_name).size() == 2) {
                	if (startEventMap.get(collapsedNameMap.get(collapsed_name).get(0))) { //start event
                		startCompleteEventMap.put(collapsedNameMap.get(collapsed_name).get(0), collapsedNameMap.get(collapsed_name).get(1));
                	}
                	else {
                		startCompleteEventMap.put(collapsedNameMap.get(collapsed_name).get(1), collapsedNameMap.get(collapsed_name).get(0));
                	}
                }

                eventsCount.addToValue(simplified_event, 1);
                simplified_trace.add(simplified_event);
            }

            simplified_trace.add(END_INT); // add artificial end event to a traces
            this.add(simplified_trace);
        }

	}
	
    public SimplifiedLog(SimplifiedLog log, IntHashSet retained_activities, XEventAttributeClassifier classifier) throws Exception {
    	super(log.size());
    	
        for(int t = 0; t < log.size(); t++) {
            IntList trace = log.get(t);
            IntArrayList filtered_trace = new IntArrayList();
            for(int i = 0; i < trace.size(); i++) {
            	int event = trace.get(i);
                if(retained_activities.contains(event)) {
                    filtered_trace.add(event);
                    simplifiedNameMap.put(log.getNameMapping().inverse().get(event), event);
                    startEventMap.put(event, log.getStartEventMap().get(event));
                    completeEventMap.put(event, log.getCompleteEventMap().get(event));
                }
            }
            this.add(filtered_trace);
        }
        if (!this.containStartEvent() && !this.containCompleteEvent()) {
        	throw new Exception("Invalid log as it does not contain 'start' or 'complete' events");
        }
        this.xlog = this.convertToXLog(log.getXLog(), classifier);
    }
	
	public XLog getXLog() {
		return xlog;
	}
	
    public String getEventFullName(int event) {
        return simplifiedNameMap.inverse().get(event);
    }
    
    public String getEventCollapsedName(int event) {
    	return LogUtils.getCollapsedEvent(this.getEventFullName(event));
    }

    public Integer getEventNumber(String event) {
        return simplifiedNameMap.get(event);
    }
    
	public boolean containStartEvent() {
		return startEventMap.values().contains(Boolean.TRUE);
	}
	
	public boolean containCompleteEvent() {
		return completeEventMap.values().contains(Boolean.TRUE);
	}
	
	public Map<Integer,Boolean> getStartEventMap() {
		return Collections.unmodifiableMap(this.startEventMap);
	}
	
	public Map<Integer,Boolean> getCompleteEventMap() {
		return Collections.unmodifiableMap(this.completeEventMap);
	}

	// Event raw names may contain +start, +complete or may not
	public boolean isEventRawName(String eventName) {
    	return simplifiedNameMap.containsKey(eventName);
    }
	
	// Event collapsed names contain only event name, no +start or +complete
    public boolean isEventCollapsedName(String eventName) {
    	return collapsedNameMap.containsKey(eventName);
    }
	
	//Note that START_INT and END_INT are not start event
	public boolean isStartEvent(int event) {
		return (startEventMap.containsKey(event) && startEventMap.get(event));
	}
	
	//Note that START_INT and END_INT are not complete event
	public boolean isCompleteEvent(int event) {
		return (completeEventMap.containsKey(event) && completeEventMap.get(event));
	}

	// Get the complete event of the start event
//	public Integer getCompleteEvent(int event) {
//		String eventName = this.getEventFullName(event);
//		if (eventName == null) return null;
//		String completeEventName = LogUtils.getCompleteEvent(eventName);
//		return this.getEventNumber(completeEventName);
//	}
	
	// Get the start event of the complete event
//	public Integer getStartEvent(int event) {
//		String eventName = this.getEventFullName(event);
//		if (eventName == null) return null;
//		String startEventName = LogUtils.getStartEvent(eventName);
//		return this.getEventNumber(startEventName);
//	}
	
	public Integer getCorrespondingEvent(int event) {
		if (isStartEvent(event)) {
			return startCompleteEventMap.get(event);
		}
		else if (isCompleteEvent(event)) {
			return startCompleteEventMap.inverse().get(event);
		}
		else {
			return null;
		}
	}
	
	//Return true if an event name is not associated with any start or complete lifecycle transition
    //or if it only has either start or complete transition 
	public boolean isSingleTypeEvent(int event) {
		return (this.getCorrespondingEvent(event) == null);
	}
    
	// The mapping between raw event names in the log and the integer-based indexes
	// It is a one-to-one and bi-directional mapping 
    public MutableBiMap<String, Integer> getNameMapping() {
    	return simplifiedNameMap.asUnmodifiable();
    }
    
    // The mapping from collapsed event names to the integer-based indexes
    // It is a one-to-many and one-way mapping
    public Map<String,List<Integer>> getCollapsedNameMapping() {
    	return Collections.unmodifiableMap(this.collapsedNameMap);
    }
    
    public List<Integer> getStartCompletePair(String collapsedName) {
    	if (collapsedNameMap.get(collapsedName) != null && collapsedNameMap.get(collapsedName).size() == 2) {
    		List<Integer> events = collapsedNameMap.get(collapsedName);
    		if (isStartEvent(events.get(0))) {
    			return Arrays.asList(events.get(0), events.get(1));
    		}
    		else {
    			return Arrays.asList(events.get(1), events.get(0));
    		}
    	}
    	else {
    		return null;
    	}
    }
    
    //Return true if an event name is not associated with any start or complete lifecycle transition
    //or if it only has either start or complete transition 
//    public boolean isSingleTypeEvent(int event) {
//        String name = getEventFullName(event);
//        if(LogUtils.isStartEvent(name) && getEventNumber(LogUtils.getCompleteEvent(name)) != null) return false;
//        return !LogUtils.isCompleteEvent(name) || getEventNumber(LogUtils.getStartEvent(name)) == null;
//    }
    
    public SimplifiedLog filterActivities(IntHashSet retained_activities, XEventAttributeClassifier classifier) throws Exception {
    	return new SimplifiedLog(this, retained_activities, classifier);
    }
    
    /**
     * Converet this simplified log back to XLog
     */
    private XLog convertToXLog(XLog parentLog, XEventAttributeClassifier full_classifier) {
        //--------------------------------------------
        // The filtered_log created after initialization() is a simplified log which 
        // has filtered out activities that are not retained by the activity slider
        // This step is used to create an XLog from the original XLog based on filtered_log
        // This XLog is needed for SplitMiner to discoverer a BPMN model
        //--------------------------------------------
        XFactory factory = new XFactoryNaiveImpl();
        XLog filtered_xlog = factory.createLog(parentLog.getAttributes());
        for(int trace = 0; trace < this.size(); trace++) {
            XTrace filtered_xtrace = factory.createTrace(parentLog.get(trace).getAttributes());
            // filtered_trace is a list of event numbers that are mapped to full event names in simplifiedNameMap map.
            // Note that the first and last elements are "1" and "2" which are used to mark the two ends of the trace
            IntList filtered_trace = this.get(trace); 
            int unfiltered_event = 0;
            for(int event = 1; event < filtered_trace.size() - 1; event++) {
            	//Jump over events that are not in the filtered trace 
                while(!full_classifier.getClassIdentity(parentLog.get(trace).get(unfiltered_event)).equalsIgnoreCase(getEventFullName(filtered_trace.get(event)))) {
                    unfiltered_event++;
                }

                filtered_xtrace.add(parentLog.get(trace).get(unfiltered_event));
                unfiltered_event++;
            }
            if(filtered_xtrace.size() > 0) {
                filtered_xlog.add(filtered_xtrace);
            }
        }

        return filtered_xlog;
    }
	
}