package org.apromore.logman.log.classifieraware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apromore.logman.classifier.EventClassifier;
import org.apromore.logman.log.Constants;
import org.apromore.logman.log.event.LogFilterListener;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.relation.DFSSReader;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

/**
 * An IntLog keeps events as integers and each trace is a list of integers
 * IntLog is created from XLog based on a chosen EventClassifier
 * All string-based names are converted to integers
 * An IntLog also allows to compute case variants from the trace 
 * @author Bruce Nguyen
 *
 */
public class IntLog extends FastList<IntArrayList> implements LogFilterListener {
	private HashBiMap<String, Integer> nameMap = new HashBiMap<>(); //bi-directiona map between string name and integer name
    private XLog xlog;
    private EventClassifier classifier;
    private MutableList<IntArrayList> caseVariants;
    
	public IntLog(XLog xlog, EventClassifier classifier) {
		super();
		this.xlog = xlog;
		this.classifier = classifier;
		
        nameMap.put(Constants.START_NAME, Constants.START_INT); 
        nameMap.put(Constants.END_NAME, Constants.END_INT); 
        
        for(XTrace trace : xlog) {
            IntArrayList intTrace = new IntArrayList(trace.size());
            List<? extends XEvent> actTrace = DFSSReader.instance().read(trace);
            for(XEvent act : actTrace) {
                String name = classifier.getClassIdentity(act);
                Integer intEvent;
                if((intEvent = getNumber(name)) == null) {
                	intEvent = nameMap.size() + 1;
                    nameMap.put(name, intEvent);
                }
                intTrace.add(intEvent);
            }
            this.add(intTrace);
        }
	}
	
	public int totalTraceCount() {
		return this.size();
	}
	
	
	public XLog getXLog() {
		return xlog;
	}
	
	public EventClassifier getEventClassifier() {
		return classifier;
	}
	
    public String getName(int event) {
        return nameMap.inverse().get(event);
    }
    
    public Integer getNumber(String event) {
        return nameMap.get(event);
    }
    
	@Override
	public void onLogFiltered(LogFilteredEvent filterEvent) {
		for (int i : filterEvent.getDeletedTraces()) {
			this.remove(i);
		}
		
		for (Entry<Integer,Set<Integer>> event: filterEvent.getDeletedEvents().entrySet()) {
			int traceIndex = event.getKey();
			for (int i: event.getValue()) {
				this.get(traceIndex).remove(i);
			}
			if (this.get(traceIndex).isEmpty()) {
				this.remove(traceIndex);
			}
		}
		
		this.caseVariants = this.distinct();
	}
    
}