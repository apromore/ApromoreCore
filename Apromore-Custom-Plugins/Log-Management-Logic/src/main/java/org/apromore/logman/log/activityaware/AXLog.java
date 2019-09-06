package org.apromore.logman.log.activityaware;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;

/** 
 * This log contains only ActivityAwareTrace
 * @author Bruce Nguyen
 *
 */
public class AXLog extends XLogImpl {
	private XLog rawLog;	
	private Map<String,Integer> valueMapping;
	
	public AXLog(XLog log) {
		super(log.getAttributes());
	    this.rawLog = log;
		valueMapping = new HashMap<>();
		
		for (XTrace trace: rawLog) {
			this.add(new AXTrace(trace));
		}
	}
	
	public XLog getRawLog() {
		return this.rawLog;
	}
	
	private void compressXLog(XLog log, Map<String,Integer> valueMapping) {
	    
	}
}
