package org.apromore.logman.log.classifieraware;

import java.util.List;

import org.apromore.logman.Constants;
import org.apromore.logman.classifier.SimpleEventClassifier;
import org.apromore.logman.event.LogFilterListener;
import org.apromore.logman.event.LogFilteredEvent;
import org.apromore.logman.relation.DFSSReader;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

/**
 * An SimpleLog keeps events as integers and each trace is a list of integers
 * SimpleLog is created from XLog based on a chosen EventClassifier
 * All string-based names are converted to integers
 * An SimpleLog also allows to compute case variants from the trace 
 * 
 * @author Bruce Nguyen
 *
 */
public class SimpleLog extends FastList<IntArrayList> implements LogFilterListener {
	private HashBiMap<String, Integer> nameMap = new HashBiMap<>(); //bi-directiona map between string name and integer name
    private XLog xlog;
    private SimpleEventClassifier classifier;
    
	public SimpleLog(XLog xlog, SimpleEventClassifier classifier) {
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
	
	public SimpleEventClassifier getEventClassifier() {
		return classifier;
	}
	
    public String getName(int event) {
        return nameMap.inverse().get(event);
    }
    
    public Integer getNumber(String event) {
        return nameMap.get(event);
    }
    
    
    ///////////////////////// Update statistics //////////////////////////////
    
	@Override
	public void onLogFiltered(LogFilteredEvent filterEvent) {
		for (Pair<XTrace,XTrace> pair: filterEvent.getUpdatedTraces()) {
			XTrace old = pair.getOne();
			int oldIndex = xlog.indexOf(old);
			XTrace ne = pair.getTwo();
			for (XEvent e : old) {
				if (!ne.contains(e)) this.get(oldIndex).remove(old.indexOf(e));
			}
		}
		
		// NOTE: delete must be done AFTER update
		for (XTrace trace : filterEvent.getDeletedTraces()) {
			this.remove(xlog.indexOf(trace));
		}
	}
    
}