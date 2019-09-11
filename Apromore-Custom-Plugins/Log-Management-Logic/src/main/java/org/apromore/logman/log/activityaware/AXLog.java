package org.apromore.logman.log.activityaware;

import java.util.Arrays;
import java.util.List;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;

/** 
 * This log contains AXTrace
 * @author Bruce Nguyen
 *
 */
public class AXLog extends XLogImpl {
	private XLog rawLog;	
	
	public AXLog(XLog log) {
		super(log.getAttributes());
	    this.rawLog = log;
		
		for (XTrace trace: rawLog) {
			this.add(new AXTrace(trace));
		}
	}
	
	public XLog getRawLog() {
		return this.rawLog;
	}
	
	public List<AXTrace> getTraces() {
		AXTrace[] traces = new AXTrace[this.size()];
		this.toArray(traces);
		return Arrays.asList(traces);
	}
}
