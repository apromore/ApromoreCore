package org.apromore.logman.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.logman.classifier.EventClassifier;
import org.apromore.logman.utils.LogUtils;
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
public class SimpleLog extends ArrayList<IntList> {
	public final static String START_NAME = "|>"; //marker for the start event in a trace in simplifiedNameMap
    public final static String END_NAME = "[]"; //marker for the end event in a trace in simplifiedNameMap
    public final static int START_INT = 1; // marker for the start event in a trace in the simplified log (index-based)
    public final static int END_INT = 2; // marker for the end event in a trace in the simplified log
    
	private HashBiMap<String, Integer> nameMap = new HashBiMap<>(); //bi-directiona map between string name and integer name
    private XLog xlog;
    private EventClassifier classifier;
    
	public SimpleLog(XLog xlog, EventClassifier classifier) throws Exception {
		super();
		this.xlog = xlog;
		this.classifier = classifier;
		
        nameMap.put(START_NAME, START_INT); 
        nameMap.put(END_NAME, END_INT); 
        
        for(XTrace trace : xlog) {
            IntArrayList simplified_trace = new IntArrayList(trace.size());
            simplified_trace.add(START_INT); //add artificial start event to a trace

            for(XEvent event : trace) {
                String name = classifier.getClassIdentity(event);
                if(name.toLowerCase().endsWith(Constants.PLUS_START_CODE) || 
                		name.toLowerCase().endsWith(Constants.PLUS_COMPLETE_CODE)) {
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
                    simplified_event = nameMap.size() + 1;
                    nameMap.put(name, simplified_event);
                }
                
            }

            simplified_trace.add(END_INT); // add artificial end event to a traces
            this.add(simplified_trace);
        }

	}
	
	
	public XLog getXLog() {
		return xlog;
	}
	
	public EventClassifier getEventClassifier() {
		return classifier;
	}
	
	public int getStartEvent() {
		return SimpleLog.START_INT;
	}
	
	public int getEndEvent() {
		return SimpleLog.END_INT;
	}
	
    public String getEventFullName(int event) {
        return nameMap.inverse().get(event);
    }
    
    public Integer getEventNumber(String event) {
        return nameMap.get(event);
    }
    
	// Event raw names may contain +start, +complete or may not
	public boolean isEventRawName(String eventName) {
    	return nameMap.containsKey(eventName);
    }
	
	// It is a one-to-one and bi-directional mapping 
    public MutableBiMap<String, Integer> getRawEventBiMap() {
    	return nameMap.asUnmodifiable();
    }
    
    public Map<String, Integer> getNameToEventMap() {
    	return Collections.unmodifiableMap(nameMap);
    }
    
    public Map<Integer, String> getEventToNameMap() {
    	return Collections.unmodifiableMap(nameMap.inverse());
    }
    
}